package com.xndev.retroadventure.engine

/**
 * The `struct game_t` of upstream's advent.h, plus the object-list helpers from
 * misc.c (carry/drop/move/put) that maintain it.
 *
 * Field names are kept close to upstream's on purpose. This struct is the thing
 * a port most easily gets subtly wrong, and every one of the 103 reproducible
 * transcripts is ultimately a statement about these fields; when a transcript
 * diverges, the fastest way to find out why is to be able to read the C and the
 * Kotlin side by side without translating names in your head.
 */

const val CARRIED = -1
const val IS_FIXED = -1
const val IS_FREE = 0
const val STATE_NOTFOUND = -1
const val STATE_FOUND = 0
const val STATE_IN_CAVITY = 1   // state common to all the gemstones

const val INVLIMIT = 7
const val GAMELIMIT = 330
const val NOVICELIMIT = 1000
const val WARNTIME = 30
const val FLASHTIME = 50
const val PANICTIME = 15
const val INTRANSITIVE = -1
const val PIRATE = NDWARVES
const val DALTLC = LOC_NUGGET   // alternate dwarf location
const val PIT_KILL_PROB = 35    // chance of dying from a fall in the dark

class ObjectState {
    var fixed: Int = IS_FREE
    var prop: Int = 0
    var place: Int = LOC_NOWHERE
}

class LocState {
    var abbrev: Int = 0
    var atloc: Int = 0
}

class DwarfState {
    var seen: Boolean = false
    var loc: Int = 0
    var oldloc: Int = 0
}

class HintState {
    var used: Boolean = false
    var lc: Int = 0
}

class GameState {
    val rng = Lcg()

    var abbnum = 5
    var chloc = LOC_MAZEEND12
    var chloc2 = LOC_DEADEND13
    var clock1 = WARNTIME
    var clock2 = FLASHTIME
    var clshnt = false
    var closed = false
    var closng = false
    var lmwarn = false
    var novice = false
    var panic = false
    var wzdark = false
    var blooded = false
    var conds = 0
    var detail = 0
    var dflag = 0
    var dkill = 0
    var dtotal = 0
    var foobar = WORD_EMPTY
    var holdng = 0
    var igo = 0
    var iwest = 0
    var knfloc = LOC_NOWHERE
    var limit = GAMELIMIT
    var loc = LOC_START
    var newloc = LOC_START
    var numdie = 0
    var oldloc = LOC_START
    var oldlc2 = LOC_START
    var oldobj = NO_OBJECT
    var saved = 0
    var tally = 0
    var thresh = 0
    var seenbigwords = false
    var trnluz = 0
    var turns = 0
    var bonus = Adventure.Bonus.NONE

    val zzword: String get() = rng.zzword

    val locs = Array(NLOCATIONS) { LocState() }
    val dwarves = Array(NDWARVES + 1) { DwarfState() }
    val objectState = Array(NOBJECTS) { ObjectState() }
    val hintState = Array(NHINTS) { HintState() }

    /**
     * Object-list links. Sized NOBJECTS*2 because a two-placed object such as
     * the grate occupies its second location under the index obj+NOBJECTS.
     */
    val link = IntArray(NOBJECTS * 2)

    /**
     * A private copy of the generated `conditions` table. `initialise()` writes
     * COND_FORCED into it, so it cannot be the shared immutable one -- two games
     * in the same process would otherwise contaminate each other. On a phone
     * that means starting a second game after the first, which is not exotic.
     */
    val conditions: IntArray = Dungeon_conditions.copyOf()

    // --- macros from advent.h ---

    fun toting(obj: Int): Boolean = objectState[obj].place == CARRIED
    fun at(obj: Int): Boolean = objectState[obj].place == loc || objectState[obj].fixed == loc
    fun here(obj: Int): Boolean = at(obj) || toting(obj)
    fun cndbit(l: Int, n: Int): Boolean = (conditions[l] and (1 shl n)) != 0
    fun forced(l: Int): Boolean = cndbit(l, COND_FORCED)
    fun forest(l: Int): Boolean = cndbit(l, COND_FOREST)
    fun outside(l: Int): Boolean = cndbit(l, COND_ABOVE) || forest(l)
    fun inside(l: Int): Boolean = !outside(l) || l == LOC_BUILDING
    fun indeep(l: Int): Boolean = cndbit(l, COND_DEEP)
    fun isDarkHere(): Boolean =
        !cndbit(loc, COND_LIT) && (objectState[LAMP].prop == LAMP_DARK || !here(LAMP))

    fun objectIsNotFound(obj: Int) = objectState[obj].prop == STATE_NOTFOUND
    fun objectIsStashed(obj: Int) = objectState[obj].prop < STATE_NOTFOUND
    fun objectSetFound(obj: Int) { objectState[obj].prop = STATE_FOUND }
    fun objectSetNotFound(obj: Int) { objectState[obj].prop = STATE_NOTFOUND }

    /** Upstream `OBJECT_STATE_EQUALS`: matches the stashed form of a state too. */
    fun objectStateEquals(obj: Int, pval: Int): Boolean =
        objectState[obj].prop == pval || objectState[obj].prop == -1 - pval

    /** Upstream `GSTONE()`. */
    fun gstone(obj: Int): Boolean = obj == EMERALD || obj == RUBY || obj == AMBER || obj == SAPPH

    fun liquid(): Int = when (objectState[BOTTLE].prop) {
        WATER_BOTTLE -> WATER
        OIL_BOTTLE -> OIL
        else -> NO_OBJECT
    }

    fun liqloc(l: Int): Int =
        if (cndbit(l, COND_FLUID)) (if (cndbit(l, COND_OILY)) OIL else WATER) else NO_OBJECT

    // --- object list maintenance, from misc.c ---

    /** Upstream `carry()`. */
    fun carry(obj: Int, where: Int) {
        if (obj < NOBJECTS) {
            if (objectState[obj].place == CARRIED) return
            objectState[obj].place = CARRIED
            // The bird is weightless so that "take bird" and "take cage" can
            // both work while it is caged. Dropping this check overcounts the
            // inventory, which upstream calls a cosmetic bug in the original.
            if (obj != BIRD) holdng++
        }
        if (locs[where].atloc == obj) {
            locs[where].atloc = link[obj]
            return
        }
        var temp = locs[where].atloc
        var hops = 0
        while (link[temp] != obj) {
            temp = link[temp]
            // Upstream walks this list without a bound because a caller that
            // hands carry() an object that is not actually at `where` is a bug
            // it does not have. A half-ported caller can, and the walk then
            // spins on link[0] forever -- which hangs the test suite instead of
            // failing it. The liquids are the trap: WATER and OIL are never on
            // any location's list, and vcarry() has to map them to the BOTTLE
            // before getting here.
            if (temp == 0 || ++hops > NOBJECTS * 2) {
                throw IllegalStateException(
                    "carry($obj) at location $where: object is not on that location's list"
                )
            }
        }
        link[temp] = link[obj]
    }

    /** Upstream `drop()`: prefix onto the location's atloc list. */
    fun drop(obj: Int, where: Int) {
        if (obj >= NOBJECTS) {
            objectState[obj - NOBJECTS].fixed = where
        } else {
            if (objectState[obj].place == CARRIED && obj != BIRD) holdng--
            objectState[obj].place = where
        }
        if (where == LOC_NOWHERE || where == CARRIED) return
        link[obj] = locs[where].atloc
        locs[where].atloc = obj
    }

    /** Upstream `move()`. */
    fun move(obj: Int, where: Int) {
        val from = if (obj < NOBJECTS) {
            if (objectState[obj].place == CARRIED) CARRIED else objectState[obj].place
        } else {
            objectState[obj - NOBJECTS].fixed
        }
        if (from != LOC_NOWHERE && from != CARRIED) carry(obj, from)
        drop(obj, where)
    }

    fun destroy(obj: Int) = move(obj, LOC_NOWHERE)

    /**
     * Upstream `juggle()`: pick an object up and put it down again, purely to
     * move it to the front of the list of things at its location. Cosmetic, but
     * it changes the order things are described in, so transcripts notice.
     */
    fun juggle(obj: Int) {
        val i = objectState[obj].place
        val j = objectState[obj].fixed
        move(obj, i)
        move(obj + NOBJECTS, j)
    }

    /** Upstream `atdwrf()`: index of the first dwarf here, ignoring the pirate. */
    fun atdwrf(where: Int): Int {
        if (dflag < 2) return 0
        var at = -1
        for (i in 1..NDWARVES - 1) {
            if (dwarves[i].loc == where) return i
            if (dwarves[i].loc != 0) at = 0
        }
        return at
    }

    /**
     * Upstream `initialise()` minus the time-based seeding: callers pass an
     * explicit seed so a game is reproducible. Note the object placement loop
     * runs backwards, because drop() prefixes and the lists have to come out in
     * forward order -- reversing it changes the order objects are described in
     * and breaks transcripts everywhere at once.
     */
    fun initialise(seedval: Int) {
        rng.setSeed(seedval)

        for (i in 1..NDWARVES) dwarves[i].loc = dwarflocs[i - 1]
        for (i in 1 until NOBJECTS) objectState[i].place = LOC_NOWHERE

        for (i in 1 until NLOCATIONS) {
            if (locations[i].big != null && tkey[i] != 0) {
                if (travel[tkey[i]].motion == HERE) {
                    conditions[i] = conditions[i] or (1 shl COND_FORCED)
                }
            }
        }

        for (i in NOBJECTS - 1 downTo 1) {
            if (objects[i].fixd > 0) {
                drop(i + NOBJECTS, objects[i].fixd)
                drop(i, objects[i].plac)
            }
        }
        for (k in NOBJECTS - 1 downTo 1) {
            objectState[k].fixed = objects[k].fixd
            if (objects[k].plac != 0 && objects[k].fixd <= 0) {
                drop(k, objects[k].plac)
            }
        }

        for (obj in 1 until NOBJECTS) {
            if (objects[obj].isTreasure) {
                tally++
                if (objects[obj].inventory != null) objectSetNotFound(obj)
            } else {
                objectSetFound(obj)
            }
        }
        conds = 1 shl COND_HBASE
    }
}

/** Alias so GameState can copy the generated table without shadowing its own field. */
private val Dungeon_conditions: IntArray get() = conditions
