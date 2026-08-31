package com.xndev.retroadventure.engine

/**
 * A single playthrough: the main loop from upstream's main.c.
 *
 * PORT STATUS: partial. The data tables, the RNG, the vocabulary, movement and
 * the output pipeline are ported and verified. `action()` -- upstream's
 * actions.c, 1677 lines -- is mostly not, and unported verbs answer with the
 * NOT_PORTED marker below rather than silently doing nothing. Run
 * TranscriptTest to see exactly which transcript diverges where.
 *
 * On the input model: upstream pulls lines from stdin from inside the loop, and
 * the loop asks questions mid-turn ("Would you like instructions?"), so it is
 * not a `String -> String` function per command. Rather than flatten that into a
 * state machine -- which would mean restructuring every yes/no site and is the
 * kind of change that quietly breaks transcript fidelity -- the loop keeps its
 * shape and pulls from an [InputSource]. The transcript harness passes a file;
 * a UI passes a blocking queue fed from the text field. See AGENTS.md.
 */

fun interface InputSource {
    /** Next line of input, or null at end of input. */
    fun readLine(): String?
}

/**
 * Where suspended games live.
 *
 * The engine cannot open files -- it is compiled for three platforms and stays
 * dependency-free -- so save and resume ask this instead. The default refuses
 * every name, which is what the failure-path transcripts expect and what an app
 * gets before it wires up its own storage. A phone would not prompt for a
 * filename at all; it would autosave to app storage and hand back a fixed name.
 */
interface SaveStore {
    /** Persist [data] under [name]. Return false if it could not be written. */
    fun write(name: String, data: String): Boolean = false

    /** Return what was stored under [name], or null if there is nothing there. */
    fun read(name: String): String? = null

    /** Names of the saves that exist, newest first where that is knowable. */
    fun list(): List<String> = emptyList()
}

/** The default store: nothing can be saved or loaded. */
object NoSaveStore : SaveStore

const val PROMPT = "> "

/** Emitted where upstream would run a verb this port has not reached yet. */
const val NOT_PORTED = "[not ported yet]"

/** How many forced moves in a row without input before we call it a loop. */
const val FORCED_MOVE_LIMIT = 200

class Adventure(
    private val input: InputSource,
    val out: Output = Output(),
    private val prompt: Boolean = true,
    private val saves: SaveStore = NoSaveStore,
) {
    val game = GameState()

    private var word1: Word = EMPTY_WORD
    private var word2: Word = EMPTY_WORD

    // --- output helpers, from misc.c ---

    private fun vspeak(msg: String?, blank: Boolean, args: List<Any> = emptyList()) {
        val rendered = renderMessage(msg, args, inside = game.inside(game.loc)) ?: return
        if (rendered.isEmpty()) return
        if (blank) out.line()
        out.line(rendered)
    }

    private fun speak(msg: String?, vararg args: Any) = vspeak(msg, true, args.toList())

    private fun rspeak(i: Int, vararg args: Any) =
        vspeak(arbitraryMessages[i], true, args.toList())

    /** Upstream `sspeak()`: unconditional newline, no blank-line suppression. */
    private fun sspeak(msg: Int, vararg args: Any) {
        out.line()
        out.line(renderMessage(arbitraryMessages[msg], args.toList(), inside = game.inside(game.loc)) ?: "")
    }

    private fun pspeak(obj: Int, mode: SpeakType, blank: Boolean, skip: Int, vararg args: Any) {
        val msg = when (mode) {
            SpeakType.TOUCH -> objects[obj].inventory
            SpeakType.LOOK -> objects[obj].descriptions.getOrNull(skip)
            SpeakType.HEAR -> objects[obj].sounds.getOrNull(skip)
            SpeakType.STUDY -> objects[obj].texts.getOrNull(skip)
            SpeakType.CHANGE -> objects[obj].changes.getOrNull(skip)
        }
        vspeak(msg, blank, args.toList())
    }

    // --- input, from misc.c get_input() ---

    private fun getInput(): String? {
        val inputPrompt = if (prompt) PROMPT else ""
        out.line() // upstream prints one blank line per input, before the comment loop
        while (true) {
            val line = input.readLine()
            if (line == null) {
                // At end of input upstream still emits the prompt it was about
                // to read against -- readline() prints it before returning NULL.
                // The transcripts record that bare "> " as the second-to-last
                // line, so leaving it out fails every one of them at the end.
                out.raw(inputPrompt)
                return null
            }
            if (line.startsWith("#")) continue // comments in test scripts
            inputsRead++
            val stripped = line.trimEnd('\n', '\r')
            out.raw(inputPrompt)
            out.line(stripped)
            return stripped
        }
    }

    /**
     * Upstream `get_command_input()`. Loops until it has something worth
     * calling a command.
     *
     * This wrapper is where the turn counter gets its meaning: a blank line and
     * a rejected three-word command both consume input but neither counts as a
     * turn. Counting every line instead put the final score's turn total one
     * too high on nearly every transcript -- correct in every other respect, and
     * wrong in the one number the player is graded on.
     */
    private fun getCommandInput(): String? {
        while (true) {
            val input = getInput() ?: return null
            if (input.trim().split(Regex("[ \t]+")).filter { it.isNotEmpty() }.size > 2) {
                rspeak(TWO_WORDS)
                continue
            }
            if (input.isNotEmpty()) return input
        }
    }

    /** Upstream `yes_or_no()`. */
    private fun yesOrNo(question: String?, yesResponse: String?, noResponse: String?): Boolean {
        while (true) {
            speak(question)
            val reply = getInput() ?: return false
            if (reply.isEmpty()) {
                rspeak(PLEASE_ANSWER)
                continue
            }
            val firstword = reply.trim().split(Regex("[ \t]+"))[0].lowercase()
            when {
                "yes".startsWith(firstword) || firstword.startsWith("y") -> {
                    speak(yesResponse); return true
                }
                "no".startsWith(firstword) || firstword.startsWith("n") -> {
                    speak(noResponse); return false
                }
                else -> rspeak(PLEASE_ANSWER)
            }
        }
    }

    // --- the loop, from main.c ---

    /** Upstream `describe_location()`. */
    private fun describeLocation() {
        var msg = locations[game.loc].small
        if (game.locs[game.loc].abbrev % game.abbnum == 0 || msg == null) {
            msg = locations[game.loc].big
        }
        if (!game.forced(game.loc) && game.isDarkHere()) {
            msg = arbitraryMessages[PITCH_DARK]
        }
        if (game.toting(BEAR)) rspeak(TAME_BEAR)
        speak(msg)
        if (game.loc == LOC_Y2 && game.rng.pct(25) && !game.closng) rspeak(SAYS_PLUGH)
    }

    /** Upstream `listobjects()`. */
    private fun listObjects() {
        if (game.isDarkHere()) return
        game.locs[game.loc].abbrev++
        var i = game.locs[game.loc].atloc
        while (i != 0) {
            var obj = i
            if (obj >= NOBJECTS) obj -= NOBJECTS
            if (!(obj == STEPS && game.toting(NUGGET))) {
                if (game.objectIsStashed(obj) || game.objectIsNotFound(obj)) {
                    if (!game.closed) {
                        game.objectSetFound(obj)
                        if (obj == RUG) game.objectState[RUG].prop = RUG_DRAGON
                        if (obj == CHAIN) game.objectState[CHAIN].prop = CHAINING_BEAR
                        if (obj == EGGS) game.seenbigwords = true
                        game.tally--
                    }
                }
                if (!(game.closed && (game.objectIsStashed(obj) || game.objectIsNotFound(obj)))) {
                    var kk = game.objectState[obj].prop
                    if (obj == STEPS) {
                        kk = if (game.loc == game.objectState[STEPS].fixed) STEPS_UP else STEPS_DOWN
                    }
                    pspeak(obj, SpeakType.LOOK, true, kk)
                }
            }
            i = game.link[i]
        }
    }

    /** Upstream `traveleq()`. */
    private fun traveleq(a: Int, b: Int): Boolean =
        travel[a].condType == travel[b].condType &&
            travel[a].condArg1 == travel[b].condArg1 &&
            travel[a].condArg2 == travel[b].condArg2 &&
            travel[a].destType == travel[b].destType &&
            travel[a].destVal == travel[b].destVal

    /**
     * Upstream `playermove()`. The dest_special branch dispatches into
     * main.c's special-travel code, which is not ported; it announces
     * NOT_PORTED and leaves the player put.
     */
    private fun playermove(motionIn: Int) {
        var motion = motionIn
        var te = tkey[game.loc]
        game.newloc = game.loc
        if (te == 0) return

        when {
            motion == NUL -> return

            motion == BACK -> {
                motion = game.oldloc
                if (game.forced(motion)) motion = game.oldlc2
                game.oldlc2 = game.oldloc
                game.oldloc = game.loc
                if (game.cndbit(game.loc, COND_NOBACK)) { rspeak(TWIST_TURN); return }
                if (motion == game.loc) { rspeak(FORGOT_PATH); return }

                var teTmp = 0
                while (true) {
                    val destType = travel[te].destType
                    val scratchloc = travel[te].destVal
                    if (destType != DestType.GOTO || scratchloc != motion) {
                        if (destType == DestType.GOTO &&
                            game.forced(scratchloc) &&
                            travel[tkey[scratchloc]].destVal == motion
                        ) {
                            teTmp = te
                        }
                        if (!travel[te].stop) { te++; continue }
                        te = teTmp
                        if (te == 0) { rspeak(NOT_CONNECTED); return }
                    }
                    motion = travel[te].motion
                    te = tkey[game.loc]
                    break
                }
            }

            motion == LOOK -> {
                if (game.detail < 3) rspeak(NO_MORE_DETAIL)
                game.detail++
                game.wzdark = false
                game.locs[game.loc].abbrev = 0
                return
            }

            motion == CAVE -> {
                rspeak(if (game.outside(game.loc) && game.loc != LOC_GRATE) FOLLOW_STREAM else NEED_DETAIL)
                return
            }

            else -> {
                game.oldlc2 = game.oldloc
                game.oldloc = game.loc
            }
        }

        // Find a travel entry for this motion.
        while (true) {
            if (travel[te].motion == HERE || travel[te].motion == motion) break
            if (travel[te].stop) {
                when (motion) {
                    EAST, WEST, SOUTH, NORTH, NE, NW, SW, SE, UP, DOWN -> rspeak(BAD_DIRECTION)
                    FORWARD, LEFT, RIGHT -> rspeak(UNSURE_FACING)
                    OUTSIDE, INSIDE -> rspeak(NO_INOUT_HERE)
                    XYZZY, PLUGH -> rspeak(NOTHING_HAPPENS)
                    CRAWL -> rspeak(WHICH_WAY)
                    else -> rspeak(CANT_APPLY)
                }
                return
            }
            te++
        }

        // Check the conditionals on this destination and any that follow it.
        // The outer loop exists for special travel 2, which rewrites the entry
        // and re-enters the conditional check -- upstream's "goto L12".
        conditionals@ while (true) {
            while (true) {
                val condType = travel[te].condType
                val condArg1 = travel[te].condArg1
                val condArg2 = travel[te].condArg2
                val ok = when {
                    condType == CondType.GOTO || condType == CondType.PCT ->
                        condArg1 == 0 || game.rng.pct(condArg1)
                    condType == CondType.CARRY || condType == CondType.WITH ->
                        game.toting(condArg1) || (condType == CondType.WITH && game.at(condArg1))
                    else -> game.objectState[condArg1].prop != condArg2
                }
                if (ok) break
                // Conditional failed: skip to the next non-matching destination.
                var teTmp = te
                do {
                    if (travel[teTmp].stop) return // upstream BUGs here; a port should not crash a phone
                    teTmp++
                } while (traveleq(te, teTmp))
                te = teTmp
            }

            game.newloc = travel[te].destVal
            when (travel[te].destType) {
                DestType.GOTO -> return
                DestType.SPEAK -> {
                    rspeak(game.newloc)
                    game.newloc = game.loc
                    return
                }
                DestType.SPECIAL -> when (game.newloc) {
                    1 -> {
                        // Plover-alcove passage: you can carry only the emerald
                        // through it. The travel table deliberately holds
                        // "useless" entries for this passage that can never be
                        // used for motion but can be spotted by "go back".
                        game.newloc = if (game.loc == LOC_PLOVER) LOC_ALCOVE else LOC_PLOVER
                        if (game.holdng > 1 || (game.holdng == 1 && !game.toting(EMERALD))) {
                            game.newloc = game.loc
                            rspeak(MUST_DROP)
                        }
                        return
                    }
                    2 -> {
                        // Plover transport: drop the emerald so he is forced to
                        // use the passage to get it out, then carry on as
                        // though he had not been carrying it.
                        game.drop(EMERALD, game.loc)
                        var teTmp = te
                        do {
                            if (travel[teTmp].stop) return
                            teTmp++
                        } while (traveleq(te, teTmp))
                        te = teTmp
                        continue@conditionals
                    }
                    3 -> {
                        // Troll bridge. Done as special motion only, so that the
                        // dwarves do not wander across and meet the bear.
                        if (game.objectState[TROLL].prop == TROLL_PAIDONCE) {
                            // He has crossed since paying, so the troll steps
                            // out and blocks him.
                            pspeak(TROLL, SpeakType.LOOK, true, TROLL_PAIDONCE)
                            game.objectState[TROLL].prop = TROLL_UNPAID
                            game.destroy(TROLL2)
                            game.move(TROLL2 + NOBJECTS, IS_FREE)
                            game.move(TROLL, objects[TROLL].plac)
                            game.move(TROLL + NOBJECTS, objects[TROLL].fixd)
                            game.juggle(CHASM)
                            game.newloc = game.loc
                            return
                        }
                        game.newloc = objects[TROLL].plac + objects[TROLL].fixd - game.loc
                        if (game.objectState[TROLL].prop == TROLL_UNPAID) {
                            game.objectState[TROLL].prop = TROLL_PAIDONCE
                        }
                        if (!game.toting(BEAR)) return
                        // The bear is too heavy for the bridge.
                        stateChange(CHASM, BRIDGE_WRECKED)
                        game.objectState[TROLL].prop = TROLL_GONE
                        game.drop(BEAR, game.newloc)
                        game.objectState[BEAR].fixed = IS_FIXED
                        game.objectState[BEAR].prop = BEAR_DEAD
                        game.oldlc2 = game.newloc
                        croak()
                        return
                    }
                    else -> return
                }
            }
        }
    }

    /**
     * Upstream `spotted_by_pirate()`. Only ever true for the pirate, who is the
     * sixth "dwarf" and shares nothing with the others but the movement rules.
     */
    private fun spottedByPirate(i: Int): Boolean {
        if (i != PIRATE) return false

        // The pirate leaves you alone once the chest has been found.
        if (game.loc == game.chloc || !game.objectIsNotFound(CHEST)) return true

        var snarfed = 0
        var movechest = false
        var robplayer = false
        for (treasure in 1 until NOBJECTS) {
            if (!objects[treasure].isTreasure) continue
            // The pirate will not take the pyramid from the plover room or the
            // dark room -- too easy.
            if (treasure == PYRAMID &&
                (game.loc == objects[PYRAMID].plac || game.loc == objects[EMERALD].plac)
            ) continue
            if (game.toting(treasure) || game.here(treasure)) snarfed++
            if (game.toting(treasure)) { movechest = true; robplayer = true }
        }
        // Force chest placement before the player finds the last treasure.
        if (game.tally == 1 && snarfed == 0 &&
            game.objectState[CHEST].place == LOC_NOWHERE &&
            game.here(LAMP) && game.objectState[LAMP].prop == LAMP_BRIGHT
        ) {
            rspeak(PIRATE_SPOTTED)
            movechest = true
        }
        // Order matters: the chest moves before the robbery, so that the chest
        // is listed last at the maze location.
        if (movechest) {
            game.move(CHEST, game.chloc)
            game.move(MESSAG, game.chloc2)
            game.dwarves[PIRATE].loc = game.chloc
            game.dwarves[PIRATE].oldloc = game.chloc
            game.dwarves[PIRATE].seen = false
        } else if (game.dwarves[PIRATE].oldloc != game.dwarves[PIRATE].loc && game.rng.pct(20)) {
            // You get a hint of the pirate even when the chest does not move.
            rspeak(PIRATE_RUSTLES)
        }
        if (robplayer) {
            rspeak(PIRATE_POUNCES)
            for (treasure in 1 until NOBJECTS) {
                if (!objects[treasure].isTreasure) continue
                if (treasure == PYRAMID &&
                    (game.loc == objects[PYRAMID].plac || game.loc == objects[EMERALD].plac)
                ) continue
                if (game.at(treasure) && game.objectState[treasure].fixed == IS_FREE) {
                    game.carry(treasure, game.loc)
                }
                if (game.toting(treasure)) game.drop(treasure, game.chloc)
            }
        }
        return true
    }

    /**
     * Upstream `dwarfmove()`. Returns true if the player survives.
     *
     * Note that this uses `game.newloc` as a scratch variable while scanning
     * travel entries, long after `do_move()` has already copied it into
     * `game.loc`. That looks like a bug and is not one -- keep it, because the
     * RNG draws it makes are part of every seeded transcript.
     */
    private fun dwarfmove(): Boolean {
        val tk = IntArray(21)

        // Don't let the dwarves follow him into a pit or a wall. The whole mess
        // activates the first time he reaches the Hall of Mists. Locations
        // forbidden to the pirate are skipped so he can't steal the return toll
        // and the dwarves can't meet the bear.
        if (game.loc == LOC_NOWHERE || game.forced(game.loc) ||
            game.cndbit(game.newloc, COND_NOARRR)
        ) return true

        if (game.dflag == 0) {
            if (game.indeep(game.loc)) game.dflag = 1
            return true
        }

        // On meeting the first dwarf, kill 0, 1 or 2 of the 5. Any survivor
        // standing on the player is moved to the alternate location.
        if (game.dflag == 1) {
            if (!game.indeep(game.loc) ||
                (game.rng.pct(95) && (!game.cndbit(game.loc, COND_NOBACK) || game.rng.pct(85)))
            ) return true
            game.dflag = 2
            for (i in 1..2) {
                val j = 1 + game.rng.randrange(NDWARVES - 1)
                if (game.rng.pct(50)) game.dwarves[j].loc = 0
            }
            for (i in 1..NDWARVES - 1) {
                if (game.dwarves[i].loc == game.loc) game.dwarves[i].loc = DALTLC
                game.dwarves[i].oldloc = game.dwarves[i].loc
            }
            rspeak(DWARF_RAN)
            game.drop(AXE, game.loc)
            return true
        }

        // Full swing: move each dwarf at random, except that one who has seen
        // us sticks with us. If they don't have to move, they attack.
        game.dtotal = 0
        var attack = 0
        var stick = 0
        for (i in 1..NDWARVES) {
            if (game.dwarves[i].loc == 0) continue

            // Fill tk with everywhere this dwarf might go.
            var j = 1
            var kk = tkey[game.dwarves[i].loc]
            if (kk != 0) {
                do {
                    val destType = travel[kk].destType
                    game.newloc = travel[kk].destVal
                    val skip = destType != DestType.GOTO ||
                        !game.indeep(game.newloc) ||
                        game.newloc == game.dwarves[i].oldloc ||
                        (j > 1 && game.newloc == tk[j - 1]) ||
                        j >= tk.size - 1 ||
                        game.newloc == game.dwarves[i].loc ||
                        game.forced(game.newloc) ||
                        (i == PIRATE && game.cndbit(game.newloc, COND_NOARRR)) ||
                        travel[kk].noDwarves
                    if (!skip) tk[j++] = game.newloc
                } while (!travel[kk++].stop)
            }
            tk[j] = game.dwarves[i].oldloc
            if (j >= 2) j--
            j = 1 + game.rng.randrange(j)
            game.dwarves[i].oldloc = game.dwarves[i].loc
            game.dwarves[i].loc = tk[j]
            game.dwarves[i].seen = (game.dwarves[i].seen && game.indeep(game.loc)) ||
                (game.dwarves[i].loc == game.loc || game.dwarves[i].oldloc == game.loc)
            if (!game.dwarves[i].seen) continue
            game.dwarves[i].loc = game.loc
            if (spottedByPirate(i)) continue

            // This threatening little dwarf is in the room with him.
            game.dtotal++
            if (game.dwarves[i].oldloc == game.dwarves[i].loc) {
                attack++
                if (game.knfloc >= LOC_NOWHERE) game.knfloc = game.loc
                if (game.rng.randrange(1000) < 95 * (game.dflag - 2)) stick++
            }
        }

        if (game.dtotal == 0) return true
        rspeak(if (game.dtotal == 1) DWARF_SINGLE else DWARF_PACK, game.dtotal)
        if (attack == 0) return true
        if (game.dflag == 2) game.dflag = 3
        if (attack > 1) {
            rspeak(THROWN_KNIVES, attack)
            rspeak(if (stick > 1) MULTIPLE_HITS else if (stick == 1) ONE_HIT else NONE_HIT, stick)
        } else {
            rspeak(KNIFE_THROWN)
            rspeak(if (stick != 0) GETS_YOU else MISSES_YOU)
        }
        if (stick == 0) return true
        game.oldlc2 = game.loc
        return false
    }

    /**
     * Upstream `croak()`: "You're dead, Jim."
     *
     * Each death offers reincarnation. On acceptance everything carried is
     * dropped where he died -- backwards, so the bird lands before the cage --
     * except the lamp, which is turned off and left outside the building so he
     * is not stranded. `oldloc` is zapped so he cannot simply retreat.
     */
    private fun croak() {
        val query = obituaries[game.numdie].query
        val yesResponse = obituaries[game.numdie].yesResponse
        game.numdie++

        if (game.closng) {
            // Died during closing time. No resurrection.
            rspeak(DEATH_CLOSING)
            terminate(Termination.ENDGAME)
        } else if (!yesOrNo(query, yesResponse, arbitraryMessages[OK_MAN]) ||
            game.numdie == NDEATHS
        ) {
            terminate(Termination.ENDGAME)
        } else {
            game.objectState[WATER].place = LOC_NOWHERE
            game.objectState[OIL].place = LOC_NOWHERE
            if (game.toting(LAMP)) game.objectState[LAMP].prop = LAMP_DARK
            for (jj in 1 until NOBJECTS) {
                val i = NOBJECTS - jj
                if (game.toting(i)) {
                    // Always leave the lamp somewhere reachable aboveground.
                    game.drop(i, if (i == LAMP) LOC_START else game.oldlc2)
                }
            }
            game.oldloc = LOC_BUILDING
            game.loc = LOC_BUILDING
            game.newloc = LOC_BUILDING
        }
    }

    /** Upstream `enum termination`. */
    enum class Termination { ENDGAME, QUITGAME, SCOREGAME }

    /** Upstream `enum scorebonus`: how the endgame was reached. */
    enum class Bonus { NONE, SPLATTER, DEFEAT, VICTORY }

    /** Set by score(); the maximum achievable, needed by terminate(). */
    private var mxscor = 0

    /**
     * Upstream `score()`.
     *
     * Treasures count only if they are in the building and unbroken, with two
     * points just for having found each one. The rest is how far you got and
     * how you finished, less deductions for hints, wasted turns and saves.
     */
    private fun score(mode: Termination): Int {
        var score = 0
        mxscor = 0

        for (i in 1 until NOBJECTS) {
            if (!objects[i].isTreasure) continue
            if (objects[i].inventory != null) {
                var k = 12
                if (i == CHEST) k = 14
                if (i > CHEST) k = 16
                if (!game.objectIsStashed(i) && !game.objectIsNotFound(i)) score += 2
                if (game.objectState[i].place == LOC_BUILDING &&
                    game.objectState[i].prop == STATE_FOUND
                ) score += k - 2
                mxscor += k
            }
        }

        score += (NDEATHS - game.numdie) * 10
        mxscor += NDEATHS * 10
        if (mode == Termination.ENDGAME) score += 4
        mxscor += 4
        if (game.dflag != 0) score += 25
        mxscor += 25
        if (game.closng) score += 25
        mxscor += 25
        if (game.closed) {
            when (game.bonus) {
                Bonus.NONE -> score += 10
                Bonus.SPLATTER -> score += 25
                Bonus.DEFEAT -> score += 30
                Bonus.VICTORY -> score += 45
            }
        }
        mxscor += 45

        // Did he come to Witt's End as he should?
        if (game.objectState[MAGAZINE].place == LOC_WITTSEND) score += 1
        mxscor += 1

        // Round it off.
        score += 2
        mxscor += 2

        for (i in 0 until NHINTS) {
            if (game.hintState[i].used) score -= hints[i].penalty
        }
        if (game.novice) score -= 5
        if (game.clshnt) score -= 10
        score -= game.trnluz + game.saved

        if (mode == Termination.SCOREGAME) {
            rspeak(GARNERED_POINTS, score, mxscor, game.turns, game.turns)
        }
        return score
    }

    /**
     * Upstream `terminate()`. Ends the game and tells you how you did.
     *
     * Upstream calls exit() from inside here; a phone cannot, so this sets
     * `finished` and the run loop unwinds instead.
     */
    private fun terminate(mode: Termination) {
        val points = score(mode)

        if (points + game.trnluz + 1 >= mxscor && game.trnluz != 0) rspeak(TOOK_LONG)
        if (points + game.saved + 1 >= mxscor && game.saved != 0) rspeak(WITHOUT_SUSPENDS)
        rspeak(TOTAL_SCORE, points, mxscor, game.turns, game.turns)

        for (i in 1 until NCLASSES) {
            if (classes[i].threshold >= points) {
                speak(classes[i].message)
                if (i < NCLASSES - 1) {
                    val nxt = classes[i].threshold + 1 - points
                    rspeak(NEXT_HIGHER, nxt, nxt)
                } else {
                    rspeak(NO_HIGHER)
                }
                finished = true
                return
            }
        }
        rspeak(OFF_SCALE)
        finished = true
    }

    /** Upstream `chain()`: do something to the bear's chain. */
    private fun chain(): Phase {
        if (verb != LOCK) {
            if (game.objectState[BEAR].prop == UNTAMED_BEAR) {
                rspeak(BEAR_BLOCKS); return Phase.CLEAROBJ
            }
            if (game.objectState[CHAIN].prop == CHAIN_HEAP) {
                rspeak(ALREADY_UNLOCKED); return Phase.CLEAROBJ
            }
            game.objectState[CHAIN].prop = CHAIN_HEAP
            game.objectState[CHAIN].fixed = IS_FREE
            if (game.objectState[BEAR].prop != BEAR_DEAD) {
                game.objectState[BEAR].prop = CONTENTED_BEAR
            }
            game.objectState[BEAR].fixed =
                if (game.objectState[BEAR].prop == BEAR_DEAD) IS_FIXED else IS_FREE
            rspeak(CHAIN_UNLOCKED)
            return Phase.CLEAROBJ
        }

        if (game.objectState[CHAIN].prop != CHAIN_HEAP) {
            rspeak(ALREADY_LOCKED); return Phase.CLEAROBJ
        }
        if (game.loc != objects[CHAIN].plac) {
            rspeak(NO_LOCKSITE); return Phase.CLEAROBJ
        }
        game.objectState[CHAIN].prop = CHAIN_FIXED
        if (game.toting(CHAIN)) game.drop(CHAIN, game.loc)
        game.objectState[CHAIN].fixed = IS_FIXED
        rspeak(CHAIN_LOCKED)
        return Phase.CLEAROBJ
    }

    /** Upstream `do_move()`. */
    private fun doMove(): Boolean {
        // Can't leave the cave once it's closing, except by the main office.
        if (game.outside(game.newloc) && game.newloc != 0 && game.closng) {
            rspeak(EXIT_CLOSED)
            game.newloc = game.loc
            if (!game.panic) game.clock2 = PANICTIME
            game.panic = true
        }

        // If a dwarf has seen him and comes from where he wants to go, the
        // dwarf is blocking his way.
        if (game.newloc != game.loc && !game.forced(game.loc) &&
            !game.cndbit(game.loc, COND_NOARRR)
        ) {
            for (i in 1..NDWARVES - 1) {
                if (game.dwarves[i].oldloc == game.newloc && game.dwarves[i].seen) {
                    game.newloc = game.loc
                    rspeak(DWARF_BLOCK)
                    break
                }
            }
        }
        game.loc = game.newloc

        if (!dwarfmove()) croak()
        if (finished) return true
        if (game.loc == LOC_NOWHERE) {
            croak()
            if (finished) return true
        }

        // The easiest way to get killed is to fall into a pit in pitch darkness.
        if (!game.forced(game.loc) && game.isDarkHere() && game.wzdark &&
            game.rng.pct(PIT_KILL_PROB)
        ) {
            rspeak(PIT_FALL)
            game.oldlc2 = game.loc
            croak()
            return false
        }
        return true
    }

    /**
     * Upstream's `phase_codes_t`. These are not cosmetic: they decide whether
     * the next turn re-describes the location (TOP), goes straight back to the
     * prompt (CLEAROBJ), or reprocesses the second word without reading new
     * input (WORD2). Getting them wrong inserts a room description after every
     * verb, which a transcript diff catches and a play-through does not.
     */
    private enum class Phase { CLEAROBJ, TOP, EXECUTED, TERMINATE, WORD2, UNKNOWN, CHECKHINT, MOVE, DWARFWAKE }

    /** Upstream's `enum speechpart`. */
    private enum class Part { UNKNOWN, INTRANSITIVE, TRANSITIVE }

    /**
     * Counts inputs consumed, so the loop can tell that it is spinning.
     *
     * A forced location moves the player without asking for input. If the move
     * out of one lands back on itself -- which it can while special travel is
     * unported -- the loop describes the room forever, consuming nothing. The
     * real game cannot do this; a half-ported one can, and it hangs the test
     * suite instead of failing it. The guard below turns that into a named
     * error naming the location, which is a bug report rather than a timeout.
     */
    private var inputsRead = 0

    /** Set by terminate(); ends the run loop the way upstream's exit() does. */
    private var finished = false

    private var part = Part.UNKNOWN
    private var verb = 0
    private var obj = NO_OBJECT

    private fun clearCommand() {
        word1 = EMPTY_WORD
        word2 = EMPTY_WORD
        part = Part.UNKNOWN
        verb = 0
        // The bird hint reads oldobj, so the previous object survives the clear.
        game.oldobj = obj
        obj = NO_OBJECT
    }

    /**
     * Upstream `preprocess_command()`. Teases out the irregular input forms
     * before the main analysis sees them: "enter water", object-then-verb
     * ("rod wave"), bare "grate" as a direction, "water plant" as "pour water",
     * and "cage bird" as "carry bird".
     *
     * Returns false when the command has already been fully handled and the
     * loop should just go get another one.
     */
    private fun preprocessCommand(): Boolean {
        if (word1.type == WordType.MOTION && word1.id == ENTER &&
            (word2.id == STREAM || word2.id == WATER)
        ) {
            rspeak(if (game.liqloc(game.loc) == WATER) FEET_WET else WHERE_QUERY)
            return false
        }

        if (word1.type == WordType.OBJECT) {
            // From object-verb to verb-object form.
            if (word2.type == WordType.ACTION) {
                val stage = word1
                word1 = word2
                word2 = stage
            }

            if (word1.id == GRATE) {
                var id = word1.id
                if (game.loc == LOC_START || game.loc == LOC_VALLEY || game.loc == LOC_SLIT) {
                    id = DEPRESSION
                }
                if (game.loc == LOC_COBBLE || game.loc == LOC_DEBRIS ||
                    game.loc == LOC_AWKWARD || game.loc == LOC_BIRDCHAMBER ||
                    game.loc == LOC_PITTOP
                ) {
                    id = ENTRANCE
                }
                word1 = word1.copy(id = id, type = WordType.MOTION)
            }
            if ((word1.id == WATER || word1.id == OIL) &&
                (word2.id == PLANT || word2.id == DOOR)
            ) {
                if (game.at(word2.id)) {
                    word2 = word1
                    word1 = Word("pour", POUR, WordType.ACTION)
                }
            }
            if (word1.id == CAGE && word2.id == BIRD && game.here(CAGE) && game.here(BIRD)) {
                word1 = word1.copy(id = CARRY, type = WordType.ACTION)
            }
        }

        // With no type for the first word, assume it is a motion.
        if (word1.type == WordType.NONE) word1 = word1.copy(type = WordType.MOTION)
        return true
    }

    /** Upstream `state_change()`: set the state and announce the change. */
    private fun stateChange(o: Int, state: Int) {
        game.objectState[o].prop = state
        pspeak(o, SpeakType.CHANGE, true, state)
    }

    /**
     * Upstream `action()`.
     *
     * The three-way `part` split is the load-bearing structure, not decoration.
     * "take lamp" arrives as an intransitive CARRY with a second word, which
     * returns WORD2; the loop then re-enters with "lamp" as word one, `verb`
     * still holding CARRY, and this function promotes the pair to transitive.
     * That is how every two-word command in the game is resolved, so verbs are
     * cheap to add once it is right.
     */
    private fun action(): Phase {
        if (actions[verb].noAction) {
            speak(actions[verb].message)
            return Phase.CLEAROBJ
        }

        if (part == Part.UNKNOWN) {
            // Analyse an object word: is the thing here, do we have a verb yet?
            // Water and oil are funny -- they are never actually dropped at a
            // location, but may be here in the bottle or as a feature of the
            // room.
            when {
                game.here(obj) -> {}
                obj == DWARF && game.atdwrf(game.loc) > 0 -> {}
                !game.closed && ((game.liquid() == obj && game.here(BOTTLE)) ||
                    obj == game.liqloc(game.loc)) -> {}
                obj == OIL && game.here(URN) && game.objectState[URN].prop != URN_EMPTY ->
                    obj = URN
                obj == PLANT && game.at(PLANT2) &&
                    game.objectState[PLANT2].prop != PLANT_THIRSTY -> obj = PLANT2
                obj == KNIFE && game.knfloc == game.loc -> {
                    game.knfloc = -1
                    rspeak(KNIVES_VANISH)
                    return Phase.CLEAROBJ
                }
                obj == ROD && game.here(ROD2) -> obj = ROD2
                (verb == FIND || verb == INVENTORY) &&
                    (word2.id == WORD_EMPTY || word2.id == WORD_NOT_FOUND) -> {}
                else -> {
                    sspeak(NO_SEE, word1.raw)
                    return Phase.CLEAROBJ
                }
            }
            if (verb != 0) part = Part.TRANSITIVE
        }

        when (part) {
            Part.INTRANSITIVE -> {
                if (!word2.isEmpty && verb != SAY) return Phase.WORD2
                if (verb == SAY) {
                    // KEYS is not special; anything that is not NO_OBJECT or
                    // INTRANSITIVE will do. This stops an unknown word being
                    // read as an intransitive verb.
                    obj = if (!word2.isEmpty) KEYS else NO_OBJECT
                }
                if (obj == NO_OBJECT || obj == INTRANSITIVE) {
                    return when (verb) {
                        CARRY -> vcarry(INTRANSITIVE)
                        NOTHING -> { rspeak(OK_MAN); Phase.CLEAROBJ }
                        UNLOCK, LOCK -> lock(INTRANSITIVE)
                        LIGHT -> light(INTRANSITIVE)
                        EXTINGUISH -> extinguish(INTRANSITIVE)
                        GO -> { speak(actions[verb].message); Phase.CLEAROBJ }
                        QUIT -> quit()
                        POUR -> pour(INTRANSITIVE)
                        EAT -> eat(INTRANSITIVE)
                        DRINK -> drink(INTRANSITIVE)
                        FILL -> fill(INTRANSITIVE)
                        LISTEN -> listen()
                        PART -> reservoir()
                        READ -> { obj = INTRANSITIVE; read(INTRANSITIVE) }
                        FEE, FIE, FOE, FOO, FUM -> bigwords(word1.id)
                        INVENTORY -> inven()
                        SEED, WASTE -> { rspeak(NUMERIC_REQUIRED); Phase.TOP }
                        SCORE -> { score(Termination.SCOREGAME); Phase.CLEAROBJ }
                        FLY -> fly(INTRANSITIVE)
                        BRIEF -> brief()
                        BLAST -> { blast(); Phase.CLEAROBJ }
                        SAVE -> suspend()
                        RESUME -> resume()
                        ATTACK -> { obj = INTRANSITIVE; attack(INTRANSITIVE) }
                        DROP, SAY, WAVE, TAME, RUB, THROW, FIND, FEED, BREAK, WAKE ->
                            Phase.UNKNOWN
                        else -> notPorted()
                    }
                }
                // An object turned up after all; fall through as transitive.
                return transitive()
            }
            Part.TRANSITIVE -> return transitive()
            Part.UNKNOWN -> {
                // Unknown verb, couldn't deduce an object -- might need a hint.
                sspeak(WHAT_DO, word1.raw)
                return Phase.CHECKHINT
            }
        }
    }

    private fun transitive(): Phase = when (verb) {
        CARRY -> vcarry(obj)
        DROP -> discard(obj)
        LIGHT -> light(obj)
        EXTINGUISH -> extinguish(obj)
        LOCK, UNLOCK -> lock(obj)
        WAVE -> wave(obj)
        ATTACK -> attack(obj)
        DRINK -> drink(obj)
        EAT -> eat(obj)
        FEED -> feed(obj)
        FILL -> fill(obj)
        POUR -> pour(obj)
        THROW -> throwit(obj)
        WAKE -> wake(obj)
        RUB -> rub(obj)
        READ -> read(obj)
        PART -> reservoir()
        SAY -> say()
        FLY -> fly(obj)
        BLAST -> { blast(); Phase.CLEAROBJ }
        LISTEN -> { speak(actions[verb].message); Phase.CLEAROBJ }
        NOTHING -> { rspeak(OK_MAN); Phase.CLEAROBJ }
        SCORE -> { speak(actions[verb].message); Phase.CLEAROBJ }
        // Verbs that only mean anything intransitively still answer when given
        // an object, with their own message rather than a parser complaint:
        // "foo bar" is "I don't know how.", not "I don't know that word".
        QUIT, BRIEF, SAVE, RESUME, FEE, FIE, FOE, FOO, FUM ->
            { speak(actions[verb].message); Phase.CLEAROBJ }
        BREAK -> vbreak(obj)
        FIND, INVENTORY -> find(obj)
        TAME, GO -> { speak(actions[verb].message); Phase.CLEAROBJ }
        WASTE -> waste(word2.raw.toIntOrNull() ?: 0)
        SEED -> {
            // Speaks the *action's* own message, not an arbitrary message --
            // actions carry a message field of their own and rspeak() would
            // index the wrong table entirely.
            val n = word2.raw.toIntOrNull() ?: 0
            speak(actions[SEED].message, n)
            game.rng.setSeed(n)
            game.turns--
            Phase.TOP
        }
        else -> notPorted()
    }

    /**
     * Name the *verb*, not `word1.raw`. By the time a transitive verb gets here
     * the word shift has already put the object in word one, so reporting the
     * raw word blames "rod" for an unported "wave".
     */
    private fun notPorted(): Phase {
        val name = actions.getOrNull(verb)?.words?.firstOrNull { it.length > 1 } ?: word1.raw
        out.line()
        out.line("$NOT_PORTED verb $name")
        return Phase.CLEAROBJ
    }

    /** Upstream `light()`. Applicable only to the lamp and the urn. */
    private fun light(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            var selects = 0
            if (game.here(LAMP) && game.objectState[LAMP].prop == LAMP_DARK && game.limit >= 0) {
                o = LAMP; selects++
            }
            if (game.here(URN) && game.objectState[URN].prop == URN_DARK) {
                o = URN; selects++
            }
            if (selects != 1) return Phase.UNKNOWN
        }
        when (o) {
            URN -> stateChange(URN, if (game.objectState[URN].prop == URN_EMPTY) URN_EMPTY else URN_LIT)
            LAMP -> {
                if (game.limit < 0) {
                    rspeak(LAMP_OUT)
                } else {
                    stateChange(LAMP, LAMP_BRIGHT)
                    // Lighting the lamp in a room you entered in the dark means
                    // you finally get to see it, so go round to describe it.
                    if (game.wzdark) return Phase.TOP
                }
            }
            else -> speak(actions[verb].message)
        }
        return Phase.CLEAROBJ
    }

    /** Upstream `extinguish()`. Lamp, urn, and dragon/volcano (nice try). */
    private fun extinguish(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            if (game.here(LAMP) && game.objectState[LAMP].prop == LAMP_BRIGHT) o = LAMP
            if (game.here(URN) && game.objectState[URN].prop == URN_LIT) o = URN
            if (o == INTRANSITIVE) return Phase.UNKNOWN
        }
        when (o) {
            URN -> if (game.objectState[URN].prop != URN_EMPTY) {
                stateChange(URN, URN_DARK)
            } else {
                pspeak(URN, SpeakType.CHANGE, true, URN_DARK)
            }
            LAMP -> {
                stateChange(LAMP, LAMP_DARK)
                rspeak(if (game.isDarkHere()) PITCH_DARK else NO_MESSAGE)
            }
            DRAGON, VOLCANO -> rspeak(BEYOND_POWER)
            else -> speak(actions[verb].message)
        }
        return Phase.CLEAROBJ
    }

    /** Upstream `lock()`, which serves both "open" and "close". */
    private fun lock(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            if (game.here(CLAM)) o = CLAM
            if (game.here(OYSTER)) o = OYSTER
            if (game.at(DOOR)) o = DOOR
            if (game.at(GRATE)) o = GRATE
            if (game.here(CHAIN)) o = CHAIN
            if (o == INTRANSITIVE) {
                rspeak(NOTHING_LOCKED)
                return Phase.CLEAROBJ
            }
        }
        when (o) {
            CHAIN -> if (game.here(KEYS)) return chain() else rspeak(NO_KEYS)
            GRATE -> if (game.here(KEYS)) {
                if (game.closng) {
                    rspeak(EXIT_CLOSED)
                    if (!game.panic) game.clock2 = PANICTIME
                    game.panic = true
                } else {
                    stateChange(GRATE, if (verb == LOCK) GRATE_CLOSED else GRATE_OPEN)
                }
            } else {
                rspeak(NO_KEYS)
            }
            CLAM -> when {
                verb == LOCK -> rspeak(HUH_MAN)
                game.toting(CLAM) -> rspeak(DROP_CLAM)
                !game.toting(TRIDENT) -> rspeak(CLAM_OPENER)
                else -> {
                    game.destroy(CLAM)
                    game.drop(OYSTER, game.loc)
                    game.drop(PEARL, LOC_CULDESAC)
                    rspeak(PEARL_FALLS)
                }
            }
            OYSTER -> when {
                verb == LOCK -> rspeak(HUH_MAN)
                game.toting(OYSTER) -> rspeak(DROP_OYSTER)
                !game.toting(TRIDENT) -> rspeak(OYSTER_OPENER)
                else -> rspeak(OYSTER_OPENS)
            }
            DOOR -> rspeak(if (game.objectState[DOOR].prop == DOOR_UNRUSTED) OK_MAN else RUSTY_DOOR)
            CAGE -> rspeak(NO_LOCK)
            KEYS -> rspeak(CANNOT_UNLOCK)
            else -> speak(actions[verb].message)
        }
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `vcarry()`. The liquids are the interesting part: water and oil
     * are never on any location's object list, so they must be mapped onto the
     * bottle before `carry()` sees them.
     */
    private fun vcarry(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            // Carry with no object given is OK only if exactly one thing is here.
            if (game.locs[game.loc].atloc == NO_OBJECT ||
                game.link[game.locs[game.loc].atloc] != 0 ||
                game.atdwrf(game.loc) > 0
            ) {
                return Phase.UNKNOWN
            }
            o = game.locs[game.loc].atloc
        }

        if (game.toting(o)) { speak(actions[verb].message); return Phase.CLEAROBJ }

        if (o == MESSAG) {
            rspeak(REMOVE_MESSAGE)
            game.destroy(MESSAG)
            return Phase.CLEAROBJ
        }

        if (game.objectState[o].fixed != IS_FREE) {
            when (o) {
                PLANT -> rspeak(
                    if (game.objectState[PLANT].prop == PLANT_THIRSTY || game.objectIsStashed(PLANT))
                        DEEP_ROOTS else YOU_JOKING
                )
                BEAR -> rspeak(
                    if (game.objectState[BEAR].prop == SITTING_BEAR) BEAR_CHAINED else YOU_JOKING
                )
                CHAIN -> rspeak(
                    if (game.objectState[BEAR].prop != UNTAMED_BEAR) STILL_LOCKED else YOU_JOKING
                )
                RUG -> rspeak(
                    if (game.objectState[RUG].prop == RUG_HOVER) RUG_HOVERS else YOU_JOKING
                )
                URN -> rspeak(URN_NOBUDGE)
                CAVITY -> rspeak(DOUGHNUT_HOLES)
                BLOOD -> rspeak(FEW_DROPS)
                SIGN -> rspeak(HAND_PASSTHROUGH)
                else -> rspeak(YOU_JOKING)
            }
            return Phase.CLEAROBJ
        }

        if (o == WATER || o == OIL) {
            if (!game.here(BOTTLE) || game.liquid() != o) {
                if (!game.toting(BOTTLE)) { rspeak(NO_CONTAINER); return Phase.CLEAROBJ }
                if (game.objectState[BOTTLE].prop == EMPTY_BOTTLE) return fill(BOTTLE)
                rspeak(BOTTLE_FULL)
                return Phase.CLEAROBJ
            }
            o = BOTTLE
        }

        if (game.holdng >= INVLIMIT) { rspeak(CARRY_LIMIT); return Phase.CLEAROBJ }

        if (o == BIRD && game.objectState[BIRD].prop != BIRD_CAGED &&
            !game.objectIsStashed(BIRD)
        ) {
            if (game.objectState[BIRD].prop == BIRD_FOREST_UNCAGED) {
                game.destroy(BIRD)
                rspeak(BIRD_CRAP)
                return Phase.CLEAROBJ
            }
            if (!game.toting(CAGE)) { rspeak(CANNOT_CARRY); return Phase.CLEAROBJ }
            if (game.toting(ROD)) { rspeak(BIRD_EVADES); return Phase.CLEAROBJ }
            game.objectState[BIRD].prop = BIRD_CAGED
        }
        if ((o == BIRD || o == CAGE) && game.objectStateEquals(BIRD, BIRD_CAGED)) {
            // This expression maps BIRD to CAGE and CAGE to BIRD.
            game.carry(BIRD + CAGE - o, game.loc)
        }

        game.carry(o, game.loc)

        if (o == BOTTLE && game.liquid() != NO_OBJECT) {
            game.objectState[game.liquid()].place = CARRIED
        }

        if (game.gstone(o) && game.objectState[o].prop != STATE_FOUND) {
            game.objectSetFound(o)
            game.objectState[CAVITY].prop = CAVITY_EMPTY
        }
        rspeak(OK_MAN)
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `discard()`. "Throw" lands here for most objects too.
     *
     * The bird is the case that matters early: dropping it sets its state to
     * uncaged, and `wave rod` later reads that state to decide whether the bird
     * fetches the jade necklace. Stub this out and the necklace never appears,
     * far away from anything that looks like a drop bug.
     */
    private fun discard(objIn: Int): Phase {
        var o = objIn
        if (o == ROD && !game.toting(ROD) && game.toting(ROD2)) o = ROD2

        if (o == INTRANSITIVE || !game.toting(o)) {
            speak(actions[verb].message)
            return Phase.CLEAROBJ
        }

        if (game.gstone(o) && game.at(CAVITY) &&
            game.objectState[CAVITY].prop != CAVITY_FULL
        ) {
            rspeak(GEM_FITS)
            game.objectState[o].prop = STATE_IN_CAVITY
            game.objectState[CAVITY].prop = CAVITY_FULL
            if (game.here(RUG) &&
                ((o == EMERALD && game.objectState[RUG].prop != RUG_HOVER) ||
                    (o == RUBY && game.objectState[RUG].prop == RUG_HOVER))
            ) {
                when {
                    o == RUBY -> rspeak(RUG_SETTLES)
                    game.toting(RUG) -> rspeak(RUG_WIGGLES)
                    else -> rspeak(RUG_RISES)
                }
                if (!game.toting(RUG) || o == RUBY) {
                    var k = if (game.objectState[RUG].prop == RUG_HOVER) RUG_FLOOR else RUG_HOVER
                    game.objectState[RUG].prop = k
                    if (k == RUG_HOVER) k = objects[SAPPH].plac
                    game.move(RUG + NOBJECTS, k)
                }
            }
            game.drop(o, game.loc)
            return Phase.CLEAROBJ
        }

        if (o == COINS && game.here(VEND)) {
            game.destroy(COINS)
            game.drop(BATTERY, game.loc)
            pspeak(BATTERY, SpeakType.LOOK, true, FRESH_BATTERIES)
            return Phase.CLEAROBJ
        }

        if (game.liquid() == o) o = BOTTLE
        if (o == BOTTLE && game.liquid() != NO_OBJECT) {
            game.objectState[game.liquid()].place = LOC_NOWHERE
        }

        if (o == BEAR && game.at(TROLL)) {
            stateChange(TROLL, TROLL_GONE)
            game.move(TROLL, LOC_NOWHERE)
            game.move(TROLL + NOBJECTS, IS_FREE)
            game.move(TROLL2, objects[TROLL].plac)
            game.move(TROLL2 + NOBJECTS, objects[TROLL].fixd)
            game.juggle(CHASM)
            game.drop(o, game.loc)
            return Phase.CLEAROBJ
        }

        if (o == VASE && game.loc != objects[PILLOW].plac) {
            stateChange(VASE, if (game.at(PILLOW)) VASE_WHOLE else VASE_DROPPED)
            if (game.objectState[VASE].prop != VASE_WHOLE) {
                game.objectState[VASE].fixed = IS_FIXED
            }
            game.drop(o, game.loc)
            return Phase.CLEAROBJ
        }

        if (o == CAGE && game.objectState[BIRD].prop == BIRD_CAGED) {
            game.drop(BIRD, game.loc)
        }

        if (o == BIRD) {
            if (game.at(DRAGON) && game.objectState[DRAGON].prop == DRAGON_BARS) {
                rspeak(BIRD_BURNT)
                game.destroy(BIRD)
                return Phase.CLEAROBJ
            }
            if (game.here(SNAKE)) {
                rspeak(BIRD_ATTACKS)
                if (game.closed) return Phase.DWARFWAKE
                game.destroy(SNAKE)
                // Set the state for use by the travel options.
                game.objectState[SNAKE].prop = SNAKE_CHASED
            } else {
                rspeak(OK_MAN)
            }
            game.objectState[BIRD].prop =
                if (game.forest(game.loc)) BIRD_FOREST_UNCAGED else BIRD_UNCAGED
            game.drop(o, game.loc)
            return Phase.CLEAROBJ
        }

        rspeak(OK_MAN)
        game.drop(o, game.loc)
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `silent_yes_or_no()`: same as yesOrNo but asks nothing itself.
     * The dragon uses it -- "attack dragon" prints its own question first.
     */
    private fun silentYesOrNo(): Boolean {
        while (true) {
            val reply = getInput() ?: return false
            if (reply.isEmpty()) { rspeak(PLEASE_ANSWER); continue }
            val firstword = reply.trim().split(Regex("[ \t]+"))[0].lowercase()
            when {
                "yes".startsWith(firstword) || firstword.startsWith("y") -> return true
                "no".startsWith(firstword) || firstword.startsWith("n") -> return false
                else -> rspeak(PLEASE_ANSWER)
            }
        }
    }

    /**
     * Upstream `attack()`. "Throw" links here for most objects.
     *
     * Targets fall into enemies (snake, dwarf, troll...) and others (bird,
     * clam, machine); the command is ambiguous if two enemies are present, or
     * no enemies and two others. The dragon branch is the famous one: insist
     * and you win.
     */
    private fun attack(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            var changes = 0
            if (game.atdwrf(game.loc) > 0) { o = DWARF; changes++ }
            if (game.here(SNAKE)) { o = SNAKE; changes++ }
            if (game.at(DRAGON) && game.objectState[DRAGON].prop == DRAGON_BARS) {
                o = DRAGON; changes++
            }
            if (game.at(TROLL)) { o = TROLL; changes++ }
            if (game.at(OGRE)) { o = OGRE; changes++ }
            if (game.here(BEAR) && game.objectState[BEAR].prop == UNTAMED_BEAR) {
                o = BEAR; changes++
            }
            if (o == INTRANSITIVE) {
                // Low-priority targets. You cannot attack the bird or the
                // machine by throwing the axe at it.
                if (game.here(BIRD) && verb != THROW) { o = BIRD; changes++ }
                if (game.here(VEND) && verb != THROW) { o = VEND; changes++ }
                // Clam and oyster are both treated as the clam here; no harm.
                if (game.here(CLAM) || game.here(OYSTER)) { o = CLAM; changes++ }
            }
            if (changes >= 2) return Phase.UNKNOWN
        }

        if (o == BIRD) {
            if (game.closed) {
                rspeak(UNHAPPY_BIRD)
            } else {
                game.destroy(BIRD)
                rspeak(BIRD_DEAD)
            }
            return Phase.CLEAROBJ
        }
        if (o == VEND) {
            stateChange(
                VEND,
                if (game.objectState[VEND].prop == VEND_BLOCKS) VEND_UNBLOCKS else VEND_BLOCKS
            )
            return Phase.CLEAROBJ
        }
        if (o == BEAR) {
            when (game.objectState[BEAR].prop) {
                UNTAMED_BEAR -> rspeak(BEAR_HANDS)
                SITTING_BEAR -> rspeak(BEAR_CONFUSED)
                CONTENTED_BEAR -> rspeak(BEAR_CONFUSED)
                BEAR_DEAD -> rspeak(ALREADY_DEAD)
            }
            return Phase.CLEAROBJ
        }
        if (o == DRAGON && game.objectState[DRAGON].prop == DRAGON_BARS) {
            rspeak(BARE_HANDS_QUERY)
            if (!silentYesOrNo()) {
                speak(arbitraryMessages[NASTY_DRAGON])
                return Phase.MOVE
            }
            stateChange(DRAGON, DRAGON_DEAD)
            game.objectState[RUG].prop = RUG_FLOOR
            game.move(DRAGON + NOBJECTS, IS_FIXED)
            game.move(RUG + NOBJECTS, IS_FREE)
            game.move(DRAGON, LOC_SECRET5)
            game.move(RUG, LOC_SECRET5)
            game.drop(BLOOD, LOC_SECRET5)
            for (i in 1 until NOBJECTS) {
                if (game.objectState[i].place == objects[DRAGON].plac ||
                    game.objectState[i].place == objects[DRAGON].fixd
                ) {
                    game.move(i, LOC_SECRET5)
                }
            }
            game.loc = LOC_SECRET5
            return Phase.MOVE
        }
        if (o == OGRE) {
            rspeak(OGRE_DODGE)
            if (game.atdwrf(game.loc) == 0) return Phase.CLEAROBJ
            rspeak(KNIFE_THROWN)
            game.destroy(OGRE)
            var dwarves = 0
            for (i in 1 until PIRATE) {
                if (game.dwarves[i].loc == game.loc) {
                    dwarves++
                    game.dwarves[i].loc = LOC_LONGWEST
                    game.dwarves[i].seen = false
                }
            }
            rspeak(if (dwarves > 1) OGRE_PANIC1 else OGRE_PANIC2)
            return Phase.CLEAROBJ
        }

        when (o) {
            INTRANSITIVE -> rspeak(NO_TARGET)
            CLAM, OYSTER -> rspeak(SHELL_IMPERVIOUS)
            SNAKE -> rspeak(SNAKE_WARNING)
            DWARF -> {
                if (game.closed) return Phase.DWARFWAKE
                rspeak(BARE_HANDS_QUERY)
            }
            DRAGON -> rspeak(ALREADY_DEAD)
            TROLL -> rspeak(ROCKY_TROLL)
            else -> speak(actions[verb].message)
        }
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `drink()`. With no object, assume water: from the bottle if it
     * holds any, otherwise from a stream here.
     */
    private fun drink(o: Int): Phase {
        if (o == INTRANSITIVE && game.liqloc(game.loc) != WATER &&
            (game.liquid() != WATER || !game.here(BOTTLE))
        ) return Phase.UNKNOWN

        if (o == BLOOD) {
            game.destroy(BLOOD)
            stateChange(DRAGON, DRAGON_BLOODLESS)
            game.blooded = true
            return Phase.CLEAROBJ
        }
        if (o != INTRANSITIVE && o != WATER) {
            rspeak(RIDICULOUS_ATTEMPT)
            return Phase.CLEAROBJ
        }
        if (game.liquid() == WATER && game.here(BOTTLE)) {
            game.objectState[WATER].place = LOC_NOWHERE
            stateChange(BOTTLE, EMPTY_BOTTLE)
            return Phase.CLEAROBJ
        }
        speak(actions[verb].message)
        return Phase.CLEAROBJ
    }

    /** Upstream `eat()`. Food is fine; some things merely lose your appetite. */
    private fun eat(o: Int): Phase {
        when (o) {
            INTRANSITIVE -> {
                if (!game.here(FOOD)) return Phase.UNKNOWN
                game.destroy(FOOD)
                rspeak(THANKS_DELICIOUS)
            }
            FOOD -> {
                game.destroy(FOOD)
                rspeak(THANKS_DELICIOUS)
            }
            BIRD, SNAKE, CLAM, OYSTER, DWARF, DRAGON, TROLL, BEAR, OGRE ->
                rspeak(LOST_APPETITE)
            else -> speak(actions[verb].message)
        }
        return Phase.CLEAROBJ
    }

    /** Upstream `feed()`. Feeding the bear the food is how you tame it. */
    private fun feed(o: Int): Phase {
        when (o) {
            BIRD -> rspeak(BIRD_PINING)
            DRAGON -> rspeak(
                if (game.objectState[DRAGON].prop != DRAGON_BARS) RIDICULOUS_ATTEMPT
                else NOTHING_EDIBLE
            )
            SNAKE -> if (!game.closed && game.here(BIRD)) {
                game.destroy(BIRD)
                rspeak(BIRD_DEVOURED)
            } else {
                rspeak(NOTHING_EDIBLE)
            }
            TROLL -> rspeak(TROLL_VICES)
            DWARF -> if (game.here(FOOD)) {
                game.dflag += 2
                rspeak(REALLY_MAD)
            } else {
                speak(actions[verb].message)
            }
            BEAR -> when {
                game.objectState[BEAR].prop == BEAR_DEAD -> rspeak(RIDICULOUS_ATTEMPT)
                game.objectState[BEAR].prop == UNTAMED_BEAR -> if (game.here(FOOD)) {
                    game.destroy(FOOD)
                    game.objectState[AXE].fixed = IS_FREE
                    game.objectState[AXE].prop = AXE_HERE
                    stateChange(BEAR, SITTING_BEAR)
                } else {
                    rspeak(NOTHING_EDIBLE)
                }
                else -> speak(actions[verb].message)
            }
            OGRE -> if (game.here(FOOD)) rspeak(OGRE_FULL) else speak(actions[verb].message)
            else -> rspeak(AM_GAME)
        }
        return Phase.CLEAROBJ
    }

    /** Upstream `fill()`. The bottle or urn must be empty and liquid available. */
    private fun fill(o: Int): Phase {
        if (o == VASE) {
            if (game.liqloc(game.loc) == NO_OBJECT) { rspeak(FILL_INVALID); return Phase.CLEAROBJ }
            if (!game.toting(VASE)) { rspeak(ARENT_CARRYING); return Phase.CLEAROBJ }
            rspeak(SHATTER_VASE)
            game.objectState[VASE].prop = VASE_BROKEN
            game.objectState[VASE].fixed = IS_FIXED
            game.drop(VASE, game.loc)
            return Phase.CLEAROBJ
        }

        if (o == URN) {
            if (game.objectState[URN].prop != URN_EMPTY) { rspeak(FULL_URN); return Phase.CLEAROBJ }
            if (!game.here(BOTTLE)) { rspeak(FILL_INVALID); return Phase.CLEAROBJ }
            val k = game.liquid()
            when (k) {
                WATER -> {
                    game.objectState[BOTTLE].prop = EMPTY_BOTTLE
                    rspeak(WATER_URN)
                }
                OIL -> {
                    game.objectState[URN].prop = URN_DARK
                    game.objectState[BOTTLE].prop = EMPTY_BOTTLE
                    rspeak(OIL_URN)
                }
                else -> { rspeak(FILL_INVALID); return Phase.CLEAROBJ }
            }
            game.objectState[k].place = LOC_NOWHERE
            return Phase.CLEAROBJ
        }

        if (o != INTRANSITIVE && o != BOTTLE) {
            speak(actions[verb].message)
            return Phase.CLEAROBJ
        }
        if (o == INTRANSITIVE && !game.here(BOTTLE)) return Phase.UNKNOWN

        if (game.here(URN) && game.objectState[URN].prop != URN_EMPTY) {
            rspeak(URN_NOPOUR); return Phase.CLEAROBJ
        }
        if (game.liquid() != NO_OBJECT) { rspeak(BOTTLE_FULL); return Phase.CLEAROBJ }
        if (game.liqloc(game.loc) == NO_OBJECT) { rspeak(NO_LIQUID); return Phase.CLEAROBJ }

        stateChange(BOTTLE, if (game.liqloc(game.loc) == OIL) OIL_BOTTLE else WATER_BOTTLE)
        if (game.toting(BOTTLE)) game.objectState[game.liquid()].place = CARRIED
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `pour()`. With no object, or the bottle, assume its contents.
     * Watering the plant cycles it through three states, which is the puzzle.
     */
    private fun pour(objIn: Int): Phase {
        var o = objIn
        if (o == BOTTLE || o == INTRANSITIVE) o = game.liquid()
        if (o == NO_OBJECT) return Phase.UNKNOWN
        if (!game.toting(o)) { speak(actions[verb].message); return Phase.CLEAROBJ }
        if (o != OIL && o != WATER) { rspeak(CANT_POUR); return Phase.CLEAROBJ }
        if (game.here(URN) && game.objectState[URN].prop == URN_EMPTY) return fill(URN)

        game.objectState[BOTTLE].prop = EMPTY_BOTTLE
        game.objectState[o].place = LOC_NOWHERE
        if (!(game.at(PLANT) || game.at(DOOR))) { rspeak(GROUND_WET); return Phase.CLEAROBJ }
        if (!game.at(DOOR)) {
            return if (o == WATER) {
                stateChange(PLANT, (game.objectState[PLANT].prop + 1) % 3)
                game.objectState[PLANT2].prop = game.objectState[PLANT].prop
                Phase.MOVE
            } else {
                rspeak(SHAKING_LEAVES)
                Phase.CLEAROBJ
            }
        }
        stateChange(DOOR, if (o == OIL) DOOR_UNRUSTED else DOOR_RUSTED)
        return Phase.CLEAROBJ
    }

    /** Upstream `quit()`. Verify intent before ending the game. */
    private fun quit(): Phase {
        if (yesOrNo(
                arbitraryMessages[REALLY_QUIT],
                arbitraryMessages[OK_MAN],
                arbitraryMessages[OK_MAN],
            )
        ) {
            terminate(Termination.QUITGAME)
            return Phase.TERMINATE
        }
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `listen()`. Intransitive only.
     *
     * The bird carries two parallel series of sounds depending on whether the
     * player has drunk the dragon's blood, which is why its state gets 3 added
     * to it here rather than going through state_change() like everything else.
     * Upstream calls this "unpleasant magic"; it is load-bearing regardless.
     */
    private fun listen(): Phase {
        var soundlatch = false
        val sound = locations[game.loc].sound
        if (sound != SILENT) {
            rspeak(sound)
            if (!locations[game.loc].loud) rspeak(NO_MESSAGE)
            soundlatch = true
        }
        for (i in 1 until NOBJECTS) {
            if (!game.here(i) || objects[i].sounds.isEmpty() ||
                game.objectIsStashed(i) || game.objectIsNotFound(i)
            ) continue
            var mi = game.objectState[i].prop
            if (i == BIRD) mi += 3 * (if (game.blooded) 1 else 0)
            pspeak(i, SpeakType.HEAR, true, mi, game.zzword)
            rspeak(NO_MESSAGE)
            if (i == BIRD && mi == BIRD_ENDSTATE) game.destroy(BIRD)
            soundlatch = true
        }
        if (!soundlatch) rspeak(ALL_SILENT)
        return Phase.CLEAROBJ
    }

    /** Upstream `throw_support()`: say something, then drop the axe and redescribe. */
    private fun throwSupport(spk: Int): Phase {
        rspeak(spk)
        game.drop(AXE, game.loc)
        return Phase.MOVE
    }

    /**
     * Upstream `throwit()`. Same as discard unless it is the axe, in which case
     * it is nearly attack -- and throwing the axe at a dwarf is the only way to
     * kill one.
     */
    private fun throwit(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE || !game.toting(o)) {
            speak(actions[verb].message)
            return Phase.CLEAROBJ
        }
        if (objects[o].isTreasure && game.at(TROLL)) {
            // Snarf a treasure for the troll.
            game.drop(o, LOC_NOWHERE)
            game.move(TROLL, LOC_NOWHERE)
            game.move(TROLL + NOBJECTS, IS_FREE)
            game.drop(TROLL2, objects[TROLL].plac)
            game.drop(TROLL2 + NOBJECTS, objects[TROLL].fixd)
            game.juggle(CHASM)
            rspeak(TROLL_SATISFIED)
            return Phase.CLEAROBJ
        }
        if (o == FOOD && game.here(BEAR)) {
            // Throwing food is another story.
            obj = BEAR
            return feed(BEAR)
        }
        if (o != AXE) return discard(o)

        if (game.atdwrf(game.loc) <= 0) {
            if (game.at(DRAGON) && game.objectState[DRAGON].prop == DRAGON_BARS) {
                return throwSupport(DRAGON_SCALES)
            }
            if (game.at(TROLL)) return throwSupport(TROLL_RETURNS)
            if (game.at(OGRE)) return throwSupport(OGRE_DODGE)
            if (game.here(BEAR) && game.objectState[BEAR].prop == UNTAMED_BEAR) {
                // This'll teach him to throw the axe at the bear.
                game.drop(AXE, game.loc)
                game.objectState[AXE].fixed = IS_FIXED
                game.juggle(BEAR)
                stateChange(AXE, AXE_LOST)
                return Phase.CLEAROBJ
            }
            obj = INTRANSITIVE
            return attack(INTRANSITIVE)
        }

        return if (game.rng.randrange(NDWARVES + 1) < game.dflag) {
            throwSupport(DWARF_DODGES)
        } else {
            val i = game.atdwrf(game.loc)
            game.dwarves[i].seen = false
            game.dwarves[i].loc = LOC_NOWHERE
            throwSupport(if (++game.dkill == 1) DWARF_SMOKE else KILLED_DWARF)
        }
    }

    /** Upstream `wake()`. Its only use is disturbing the dwarves. */
    private fun wake(o: Int): Phase {
        if (o != DWARF || !game.closed) {
            speak(actions[verb].message)
            return Phase.CLEAROBJ
        }
        rspeak(PROD_DWARF)
        return Phase.DWARFWAKE
    }

    /**
     * Upstream `reservoir()`: the Z'ZZZ magic word, which is recomputed from
     * the seed at startup and so differs every game. Saying it away from the
     * reservoir but at its bottom drowns you.
     */
    private fun reservoir(): Phase {
        if (!game.at(RESER) && game.loc != LOC_RESBOTTOM) {
            rspeak(NOTHING_HAPPENS)
            return Phase.CLEAROBJ
        }
        stateChange(
            RESER,
            if (game.objectState[RESER].prop == WATERS_PARTED) WATERS_UNPARTED
            else WATERS_PARTED
        )
        if (game.at(RESER)) return Phase.CLEAROBJ
        game.oldlc2 = game.loc
        game.newloc = LOC_NOWHERE
        rspeak(NOT_BRIGHT)
        return Phase.EXECUTED
    }

    /** Upstream `rub()`. Snide remarks, except for the lit urn. */
    private fun rub(o: Int): Phase {
        if (o == URN && game.objectState[URN].prop == URN_LIT) {
            game.destroy(URN)
            game.drop(AMBER, game.loc)
            game.objectState[AMBER].prop = AMBER_IN_ROCK
            game.tally--
            game.drop(CAVITY, game.loc)
            rspeak(URN_GENIES)
        } else if (o != LAMP) {
            rspeak(PECULIAR_NOTHING)
        } else {
            speak(actions[verb].message)
        }
        return Phase.CLEAROBJ
    }

    /** Upstream `say()`: echo the second word, unless it is a magic word. */
    private fun say(): Phase {
        if (word2.type == WordType.MOTION &&
            (word2.id == XYZZY || word2.id == PLUGH || word2.id == PLOVER)
        ) return Phase.WORD2
        if (word2.type == WordType.ACTION && word2.id == PART) return reservoir()
        if (word2.type == WordType.ACTION &&
            (word2.id == FEE || word2.id == FIE || word2.id == FOE ||
                word2.id == FOO || word2.id == FUM)
        ) return bigwords(word2.id)
        sspeak(OKEY_DOKEY, word2.raw)
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `bigwords()`: FEE FIE FOE FOO (and FUM). Only advances if given
     * in order; the last word zips the eggs back to the giant room.
     */
    private fun bigwords(id: Int): Phase {
        val foobar = if (game.foobar < 0) -game.foobar else game.foobar

        // Only FEE can start the sequence.
        if (foobar == WORD_EMPTY && (id == FIE || id == FOE || id == FOO || id == FUM)) {
            rspeak(NOTHING_HAPPENS)
            return Phase.CLEAROBJ
        }

        if ((foobar == WORD_EMPTY && id == FEE) || (foobar == FEE && id == FIE) ||
            (foobar == FIE && id == FOE) || (foobar == FOE && id == FOO)
        ) {
            game.foobar = id
            if (id != FOO) {
                rspeak(OK_MAN)
                return Phase.CLEAROBJ
            }
            game.foobar = WORD_EMPTY
            if (game.objectState[EGGS].place == objects[EGGS].plac ||
                (game.toting(EGGS) && game.loc == objects[EGGS].plac)
            ) {
                rspeak(NOTHING_HAPPENS)
                return Phase.CLEAROBJ
            }
            // Bring the troll back if we steal the eggs from him before crossing.
            if (game.objectState[EGGS].place == LOC_NOWHERE &&
                game.objectState[TROLL].place == LOC_NOWHERE &&
                game.objectState[TROLL].prop == TROLL_UNPAID
            ) {
                game.objectState[TROLL].prop = TROLL_PAIDONCE
            }
            when {
                game.here(EGGS) -> pspeak(EGGS, SpeakType.LOOK, true, EGGS_VANISHED)
                game.loc == objects[EGGS].plac -> pspeak(EGGS, SpeakType.LOOK, true, EGGS_HERE)
                else -> pspeak(EGGS, SpeakType.LOOK, true, EGGS_DONE)
            }
            game.move(EGGS, objects[EGGS].plac)
            return Phase.CLEAROBJ
        }

        // Sequence was started but is incorrect.
        rspeak(if (game.seenbigwords) START_OVER else WELL_POINTLESS)
        game.foobar = WORD_EMPTY
        return Phase.CLEAROBJ
    }

    /** Upstream `read()`. The oyster is the special case, as ever. */
    private fun read(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            var matches = 0
            var resolved = NO_OBJECT
            for (i in 1 until NOBJECTS) {
                if (game.here(i) && objects[i].texts.isNotEmpty() && !game.objectIsStashed(i)) {
                    matches++
                    if (matches == 1) resolved = i
                }
            }
            if (matches != 1 || game.isDarkHere()) return Phase.UNKNOWN
            o = resolved
        }

        when {
            game.isDarkHere() -> sspeak(NO_SEE, word1.raw)
            o == OYSTER -> when {
                !game.toting(OYSTER) || !game.closed -> rspeak(DONT_UNDERSTAND)
                !game.clshnt -> game.clshnt = yesOrNo(
                    arbitraryMessages[CLUE_QUERY],
                    arbitraryMessages[WAYOUT_CLUE],
                    arbitraryMessages[OK_MAN],
                )
                // Not really a sound, but oh well.
                else -> pspeak(OYSTER, SpeakType.HEAR, true, 1)
            }
            objects[o].texts.isEmpty() || game.objectIsNotFound(o) ->
                speak(actions[verb].message)
            else -> pspeak(o, SpeakType.STUDY, true, game.objectState[o].prop)
        }
        return Phase.CLEAROBJ
    }

    /** Upstream `lampcheck()`: tick the lamp down and warn before it dies. */
    private fun lampcheck() {
        if (game.objectState[LAMP].prop == LAMP_BRIGHT) game.limit--

        // When the lamp gets close to dying we warn him. Fresh batteries here
        // replace it and carry on; otherwise he gets one warning and can still
        // explore outside for a while after it goes out.
        if (game.limit <= WARNTIME) {
            if (game.here(BATTERY) &&
                game.objectState[BATTERY].prop == FRESH_BATTERIES && game.here(LAMP)
            ) {
                rspeak(REPLACE_BATTERIES)
                game.objectState[BATTERY].prop = DEAD_BATTERIES
                game.limit += BATTERYLIFE
                game.lmwarn = false
            } else if (!game.lmwarn && game.here(LAMP)) {
                game.lmwarn = true
                rspeak(
                    when {
                        game.objectState[BATTERY].prop == DEAD_BATTERIES -> MISSING_BATTERIES
                        game.objectState[BATTERY].place == LOC_NOWHERE -> LAMP_DIM
                        else -> GET_BATTERIES
                    }
                )
            }
        }
        if (game.limit == 0) {
            game.limit = -1
            game.objectState[LAMP].prop = LAMP_DARK
            if (game.here(LAMP)) rspeak(LAMP_OUT)
        }
    }

    /**
     * Upstream `closecheck()`: the cave closes `clock1` turns after the last
     * treasure is *located* -- not taken. When the first warning comes the grate
     * locks, the bridge goes, every dwarf dies, and from then on he cannot leave
     * or be resurrected. When clock2 runs out he is moved into the final puzzle.
     */
    private fun closecheck(): Boolean {
        // Apply any turn-threshold penalty and say so.
        for (i in 0 until NTHRESHOLDS) {
            if (game.turns == turnThresholds[i].threshold + 1) {
                game.trnluz += turnThresholds[i].pointLoss
                speak(turnThresholds[i].message)
            }
        }

        // clock1 only ticks well inside the cave, and not at Y2.
        if (game.tally == 0 && game.indeep(game.loc) && game.loc != LOC_Y2) game.clock1--

        if (game.clock1 == 0) {
            game.objectState[GRATE].prop = GRATE_CLOSED
            game.objectState[FISSURE].prop = UNBRIDGED
            for (i in 1..NDWARVES) {
                game.dwarves[i].seen = false
                game.dwarves[i].loc = LOC_NOWHERE
            }
            game.destroy(TROLL)
            game.move(TROLL + NOBJECTS, IS_FREE)
            game.move(TROLL2, objects[TROLL].plac)
            game.move(TROLL2 + NOBJECTS, objects[TROLL].fixd)
            game.juggle(CHASM)
            if (game.objectState[BEAR].prop != BEAR_DEAD) game.destroy(BEAR)
            game.objectState[CHAIN].prop = CHAIN_HEAP
            game.objectState[CHAIN].fixed = IS_FREE
            game.objectState[AXE].prop = AXE_HERE
            game.objectState[AXE].fixed = IS_FREE
            rspeak(CAVE_CLOSING)
            game.clock1 = -1
            game.closng = true
            return game.closed
        } else if (game.clock1 < 0) {
            game.clock2--
        }

        if (game.clock2 == 0) {
            // Set up the storage room: two hardwired locations, everything he
            // could cause trouble with dropped, and a flash of light.
            game.put(BOTTLE, LOC_NE, EMPTY_BOTTLE)
            game.put(PLANT, LOC_NE, PLANT_THIRSTY)
            game.put(OYSTER, LOC_NE, STATE_FOUND)
            game.put(LAMP, LOC_NE, LAMP_DARK)
            game.put(ROD, LOC_NE, STATE_FOUND)
            game.put(DWARF, LOC_NE, STATE_FOUND)
            game.loc = LOC_NE
            game.oldloc = LOC_NE
            game.newloc = LOC_NE

            // Leave the grate with a normal property. Reuse the sign.
            game.move(GRATE, LOC_SW)
            game.move(SIGN, LOC_SW)
            game.objectState[SIGN].prop = ENDGAME_SIGN
            game.put(SNAKE, LOC_SW, SNAKE_CHASED)
            game.put(BIRD, LOC_SW, BIRD_CAGED)
            game.put(CAGE, LOC_SW, STATE_FOUND)
            game.put(ROD2, LOC_SW, STATE_FOUND)
            game.put(PILLOW, LOC_SW, STATE_FOUND)

            game.put(MIRROR, LOC_NE, STATE_FOUND)
            game.objectState[MIRROR].fixed = LOC_SW

            for (i in 1 until NOBJECTS) {
                if (game.toting(i)) game.destroy(i)
            }

            rspeak(CAVE_CLOSED)
            game.closed = true
            return game.closed
        }

        lampcheck()
        return false
    }

    /**
     * Upstream `checkhints()`. Each hint has a location condition and a dwell
     * count; stay long enough somewhere the hint applies and the game offers it,
     * for a price. The per-hint guards below are upstream's, case for case.
     */
    private fun checkhints() {
        if (game.conditions[game.loc] < game.conds) return
        for (hint in 0 until NHINTS) {
            if (game.hintState[hint].used) continue
            if (!game.cndbit(game.loc, hint + 1 + COND_HBASE)) game.hintState[hint].lc = -1
            game.hintState[hint].lc++
            if (game.hintState[hint].lc < hints[hint].turns) continue

            val offer = when (hint) {
                0 -> // cave
                    if (game.objectState[GRATE].prop == GRATE_CLOSED && !game.here(KEYS)) true
                    else { game.hintState[hint].lc = 0; return }
                1 -> // bird
                    if (game.objectState[BIRD].place == game.loc && game.toting(ROD) &&
                        game.oldobj == BIRD
                    ) true else return
                2 -> // snake
                    if (game.here(SNAKE) && !game.here(BIRD)) true
                    else { game.hintState[hint].lc = 0; return }
                3 -> // maze
                    if (game.locs[game.loc].atloc == NO_OBJECT &&
                        game.locs[game.oldloc].atloc == NO_OBJECT &&
                        game.locs[game.oldlc2].atloc == NO_OBJECT && game.holdng > 1
                    ) true else { game.hintState[hint].lc = 0; return }
                4 -> // dark
                    if (!game.objectIsNotFound(EMERALD) && game.objectIsNotFound(PYRAMID)) true
                    else { game.hintState[hint].lc = 0; return }
                5 -> true // witt
                6 -> // urn
                    if (game.dflag == 0) true else { game.hintState[hint].lc = 0; return }
                7 -> // woods
                    if (game.locs[game.loc].atloc == NO_OBJECT &&
                        game.locs[game.oldloc].atloc == NO_OBJECT &&
                        game.locs[game.oldlc2].atloc == NO_OBJECT
                    ) true else return
                8 -> { // ogre
                    val i = game.atdwrf(game.loc)
                    if (i < 0) { game.hintState[hint].lc = 0; return }
                    if (game.here(OGRE) && i == 0) true else return
                }
                9 -> // jade
                    if (game.tally == 1 &&
                        (game.objectIsStashed(JADE) || game.objectIsNotFound(JADE))
                    ) true else { game.hintState[hint].lc = 0; return }
                else -> return
            }
            if (!offer) return

            game.hintState[hint].lc = 0
            if (!yesOrNo(
                    hints[hint].question,
                    arbitraryMessages[NO_MESSAGE],
                    arbitraryMessages[OK_MAN],
                )
            ) return
            rspeak(HINT_COST, hints[hint].penalty, hints[hint].penalty)
            game.hintState[hint].used = yesOrNo(
                arbitraryMessages[WANT_HINT],
                hints[hint].hint,
                arbitraryMessages[OK_MAN],
            )
            if (game.hintState[hint].used && game.limit > WARNTIME) {
                game.limit += WARNTIME * hints[hint].penalty
            }
        }
    }

    /**
     * Read a bare line, as upstream's `myreadline()` does for a file name.
     *
     * Note what is NOT echoed. With input redirected, libedit suppresses the
     * prompt while reading, so the transcripts show no "File name:" before each
     * attempt -- only the one upstream prints explicitly when the read returns
     * end-of-input. Echoing on every read looks more helpful and fails the
     * transcripts.
     */
    private fun readFileName(): String? {
        val line = input.readLine()
        if (line == null) {
            out.line()
            out.raw("File name: ")
            return null
        }
        return line.trim()
    }

    /**
     * Upstream `suspend()`. Costs 5 points, so a saved game cannot be used to
     * retry a lost battle or to start over knowing the magic word.
     */
    private fun suspend(): Phase {
        rspeak(SUSPEND_WARNING)
        if (!yesOrNo(
                arbitraryMessages[THIS_ACCEPTABLE],
                arbitraryMessages[OK_MAN],
                arbitraryMessages[OK_MAN],
            )
        ) return Phase.CLEAROBJ
        game.saved += 5

        while (true) {
            val name = readFileName() ?: return Phase.TOP
            if (name.isEmpty()) return Phase.TOP
            if (saves.write(name, game.snapshot())) {
                rspeak(RESUME_HELP)
                finished = true
                return Phase.TERMINATE
            }
            out.line("Can't open file $name, try again.")
        }
    }

    /** Upstream `resume()`: read a suspended game back. */
    private fun resume(): Phase {
        if (game.loc != LOC_START || game.locs[LOC_START].abbrev != 1) {
            rspeak(RESUME_ABANDON)
            if (!yesOrNo(
                    arbitraryMessages[THIS_ACCEPTABLE],
                    arbitraryMessages[OK_MAN],
                    arbitraryMessages[OK_MAN],
                )
            ) return Phase.CLEAROBJ
        }
        while (true) {
            val name = readFileName() ?: return Phase.TOP
            if (name.isEmpty()) return Phase.TOP
            val data = saves.read(name)
            if (data == null) {
                out.line("Can't open file $name, try again.")
                continue
            }
            try {
                game.restore(data)
            } catch (e: SaveFormatException) {
                // A damaged or foreign save must not leave a half-restored game
                // running -- restore() parses everything before it writes
                // anything, so the game in progress is untouched here.
                if (e.problem == SaveProblem.WRONG_VERSION) {
                    rspeak(
                        VERSION_SKEW,
                        e.foundVersion / 10, e.foundVersion % 10,
                        SAVE_VERSION / 10, SAVE_VERSION % 10,
                    )
                } else {
                    rspeak(BAD_SAVE)
                }
                return Phase.TOP
            }
            return Phase.TOP
        }
    }

    /** Upstream `fly()`. Snide remarks unless the hovering rug is here. */
    private fun fly(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            if (!game.here(RUG)) { rspeak(FLAP_ARMS); return Phase.CLEAROBJ }
            if (game.objectState[RUG].prop != RUG_HOVER) {
                rspeak(RUG_NOTHING2); return Phase.CLEAROBJ
            }
            o = RUG
        }
        if (o != RUG) { speak(actions[verb].message); return Phase.CLEAROBJ }
        if (game.objectState[RUG].prop != RUG_HOVER) {
            rspeak(RUG_NOTHING1); return Phase.CLEAROBJ
        }

        when (game.loc) {
            LOC_CLIFF -> {
                game.oldlc2 = game.oldloc
                game.oldloc = game.loc
                game.newloc = LOC_LEDGE
                rspeak(RUG_GOES)
            }
            LOC_LEDGE -> {
                game.oldlc2 = game.oldloc
                game.oldloc = game.loc
                game.newloc = LOC_CLIFF
                rspeak(RUG_RETURNS)
            }
            else -> rspeak(NOTHING_HAPPENS) // should never happen
        }
        return Phase.EXECUTED
    }

    /** Upstream `vbreak()`. Works only on the mirror and, of course, the vase. */
    private fun vbreak(o: Int): Phase {
        when {
            o == MIRROR -> if (game.closed) {
                stateChange(MIRROR, MIRROR_BROKEN)
                return Phase.DWARFWAKE
            } else {
                rspeak(TOO_FAR)
            }
            o == VASE && game.objectState[VASE].prop == VASE_WHOLE -> {
                if (game.toting(VASE)) game.drop(VASE, game.loc)
                stateChange(VASE, VASE_BROKEN)
                game.objectState[VASE].fixed = IS_FIXED
            }
            else -> speak(actions[verb].message)
        }
        return Phase.CLEAROBJ
    }

    /** Upstream `find()`. Also serves "inventory <object>". */
    private fun find(o: Int): Phase {
        if (game.toting(o)) { rspeak(ALREADY_CARRYING); return Phase.CLEAROBJ }
        if (game.closed) { rspeak(NEEDED_NEARBY); return Phase.CLEAROBJ }
        if (game.at(o) || (game.liquid() == o && game.at(BOTTLE)) ||
            o == game.liqloc(game.loc) || (o == DWARF && game.atdwrf(game.loc) > 0)
        ) {
            rspeak(YOU_HAVEIT)
            return Phase.CLEAROBJ
        }
        speak(actions[verb].message)
        return Phase.CLEAROBJ
    }

    /** Upstream `brief()`. Suppress the long descriptions after the first time. */
    private fun brief(): Phase {
        game.abbnum = 10000
        game.detail = 3
        rspeak(BRIEF_CONFIRM)
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `blast()`. No effect unless you have the dynamite, which is a
     * neat trick -- and it is how the game is actually won.
     */
    private fun blast() {
        if (game.objectIsNotFound(ROD2) || !game.closed) {
            rspeak(REQUIRES_DYNAMITE)
            return
        }
        when {
            game.here(ROD2) -> { game.bonus = Bonus.SPLATTER; rspeak(SPLATTER_MESSAGE) }
            game.loc == LOC_NE -> { game.bonus = Bonus.DEFEAT; rspeak(DEFEAT_MESSAGE) }
            else -> { game.bonus = Bonus.VICTORY; rspeak(VICTORY_MESSAGE) }
        }
        terminate(Termination.ENDGAME)
    }

    /** Upstream `waste()`: burn turns off the lamp. */
    private fun waste(turns: Int): Phase {
        game.limit -= turns
        speak(actions[verb].message, game.limit)
        return Phase.TOP
    }

    /** Upstream `wave()`. No effect unless waving the rod at the fissure or the bird. */
    private fun wave(o: Int): Phase {
        if (o != ROD || !game.toting(o) ||
            (!game.here(BIRD) && (game.closng || !game.at(FISSURE)))
        ) {
            speak(
                if (!game.toting(o) && (o != ROD || !game.toting(ROD2)))
                    arbitraryMessages[ARENT_CARRYING]
                else
                    actions[verb].message
            )
            return Phase.CLEAROBJ
        }

        if (game.objectState[BIRD].prop == BIRD_UNCAGED &&
            game.loc == game.objectState[STEPS].place &&
            game.objectIsNotFound(JADE)
        ) {
            game.drop(JADE, game.loc)
            game.objectSetFound(JADE)
            game.tally--
            rspeak(NECKLACE_FLY)
            return Phase.CLEAROBJ
        }

        if (game.closed) {
            rspeak(if (game.objectState[BIRD].prop == BIRD_CAGED) CAGE_FLY else FREE_FLY)
            return Phase.DWARFWAKE
        }
        if (game.closng || !game.at(FISSURE)) {
            rspeak(if (game.objectState[BIRD].prop == BIRD_CAGED) CAGE_FLY else FREE_FLY)
            return Phase.CLEAROBJ
        }
        if (game.here(BIRD)) {
            rspeak(if (game.objectState[BIRD].prop == BIRD_CAGED) CAGE_FLY else FREE_FLY)
        }
        stateChange(
            FISSURE,
            if (game.objectState[FISSURE].prop == BRIDGED) UNBRIDGED else BRIDGED
        )
        return Phase.CLEAROBJ
    }

    /** Upstream `inven()`. */
    private fun inven(): Phase {
        var empty = true
        for (i in 1 until NOBJECTS) {
            if (i == BEAR || !game.toting(i)) continue
            if (empty) { rspeak(NOW_HOLDING); empty = false }
            pspeak(i, SpeakType.TOUCH, false, -1)
        }
        if (game.toting(BEAR)) { rspeak(TAME_BEAR); empty = false }
        if (empty) rspeak(NO_CARRY)
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `do_command()`. The loop nesting is upstream's and it matters:
     * the outer loop describes the location and lists what is here, the middle
     * one takes commands until one actually moves the player, and the inner one
     * reprocesses a shifted second word without reading new input. Collapsing
     * any of them re-describes the room at the wrong time.
     */
    private fun doCommand(): Boolean {
        var spinGuard = inputsRead
        var spins = 0
        outer@ while (true) {
            if (inputsRead == spinGuard) {
                if (++spins > FORCED_MOVE_LIMIT) {
                    throw IllegalStateException(
                        "stuck in a forced-location loop at location ${game.loc}" +
                            " after ${game.turns} turns; likely unported special travel"
                    )
                }
            } else {
                spinGuard = inputsRead
                spins = 0
            }
            describeLocation()
            if (game.forced(game.loc)) {
                playermove(HERE)
                return true
            }
            listObjects()
            clearCommand()

            input@ while (true) {
                // blast() and the dwarf-wake ending terminate mid-turn. Upstream
                // exits from there; the port has to stop asking for input or it
                // prints a stray prompt after the final score.
                if (finished) return false

                if (game.closed) {
                    // At closing time, unstash anything being carried, so that
                    // things are not described until they have been picked up
                    // and put down away from their pile.
                    if ((game.objectIsNotFound(OYSTER) || game.objectIsStashed(OYSTER)) &&
                        game.toting(OYSTER)
                    ) {
                        pspeak(OYSTER, SpeakType.LOOK, true, 1)
                    }
                    for (i in 1 until NOBJECTS) {
                        if (game.toting(i) &&
                            (game.objectIsNotFound(i) || game.objectIsStashed(i))
                        ) {
                            game.objectState[i].prop = -1 - game.objectState[i].prop
                        }
                    }
                }

                // Whether the room was dark on entry. light() consults this to
                // decide whether turning the lamp on should re-describe the
                // room you have just revealed -- without it, the description
                // never appears and 88 transcripts diverge at the same line.
                game.wzdark = game.isDarkHere()

                // If the knife is not here it permanently disappears.
                if (game.knfloc > LOC_NOWHERE && game.knfloc != game.loc) {
                    game.knfloc = LOC_NOWHERE
                }

                checkhints()

                val line = getCommandInput() ?: return false

                // Every input, check the "foobar" flag: if positive make it
                // negative, if negative he skipped a word so reset it.
                game.foobar = if (game.foobar > WORD_EMPTY) -game.foobar else WORD_EMPTY
                game.turns++

                val (w1, w2) = Vocabulary.tokenize(line, game.zzword)
                word1 = w1
                word2 = w2

                // Runs once per input, not once per word shift -- upstream puts
                // it here for that reason.
                if (!preprocessCommand()) continue@input

                // Check whether the cave is closing, and bail out if it closed.
                if (closecheck()) return true

                while (true) { // reprocess after a word shift, no new input
                    // No isEmpty shortcut here. get_command_input has already
                    // rejected a genuinely empty line, but a line of only
                    // spaces is not empty -- it tokenizes to an empty word,
                    // which preprocess retypes as motion zero, and upstream
                    // answers "I don't know how to apply that word here".
                    // Skipping it silently also skips the turn, and the dwarves
                    // desynchronize from there on.

                    // Test the id alone. preprocessCommand() has already
                    // retyped an unclassified word as a motion by this point,
                    // so also requiring type == NONE means an unknown word is
                    // answered with "I don't know how to apply that word here"
                    // instead of "I don't know the word".
                    if (word1.id == WORD_NOT_FOUND) {
                        sspeak(DONT_KNOW, word1.raw)
                        clearCommand()
                        continue@input
                    }

                    // Nudge the player toward the shorter forms, as upstream does.
                    if (word1.raw.equals("west", ignoreCase = true)) {
                        if (++game.iwest == 10) rspeak(W_IS_WEST)
                    }
                    if (word1.raw.equals("go", ignoreCase = true) && !word2.isEmpty) {
                        if (++game.igo == 10) rspeak(GO_UNNEEDED)
                    }

                    when (word1.type) {
                        WordType.MOTION -> {
                            playermove(word1.id)
                            return true
                        }
                        WordType.NUMERIC -> {
                            sspeak(DONT_KNOW, word1.raw)
                            clearCommand()
                            continue@input
                        }
                        WordType.OBJECT -> {
                            part = Part.UNKNOWN
                            obj = word1.id
                        }
                        WordType.ACTION -> {
                            part = if (word2.type == WordType.NUMERIC) Part.TRANSITIVE
                                   else Part.INTRANSITIVE
                            verb = word1.id
                        }
                        WordType.NONE -> continue@input
                    }

                    when (action()) {
                        Phase.TERMINATE -> return false
                        Phase.EXECUTED -> return true
                        Phase.MOVE -> {
                            playermove(NUL)
                            return true
                        }
                        Phase.DWARFWAKE -> {
                            // He has disturbed the dwarves; that ends the game.
                            rspeak(DWARVES_AWAKEN)
                            terminate(Termination.ENDGAME)
                            return false
                        }
                        Phase.TOP -> continue@outer
                        Phase.WORD2 -> {
                            // Shift the second word up and analyse it, keeping
                            // `verb` -- this is how "take lamp" resolves.
                            word1 = word2
                            word2 = EMPTY_WORD
                            continue
                        }
                        Phase.UNKNOWN -> {
                            // Random intransitive verbs land here. Clear the
                            // object just in case.
                            val raw = word1.raw.replaceFirstChar { it.uppercaseChar() }
                            sspeak(DO_WHAT, raw)
                            obj = NO_OBJECT
                            continue@input
                        }
                        Phase.CHECKHINT -> continue@input
                        Phase.CLEAROBJ -> {
                            clearCommand()
                            continue@input
                        }
                    }
                }
            }
        }
    }

    /** Upstream `main()`: initialise, greet, then loop until end of input. */
    fun run(seedval: Int) {
        game.initialise(seedval)
        game.novice = yesOrNo(
            arbitraryMessages[WELCOME_YOU],
            arbitraryMessages[CAVE_NEARBY],
            arbitraryMessages[NO_MESSAGE],
        )
        if (game.novice) game.limit = NOVICELIMIT

        while (!finished) {
            if (!doMove()) continue
            // A death that used up the last life terminates inside doMove.
            // Upstream exits there and never reaches do_command; without this
            // check the port asks for one more line and prints a stray prompt
            // after the final score.
            if (finished) break
            if (!doCommand()) break
        }

        // Running out of input is a quit, not a silent stop: upstream falls out
        // of the same loop straight into terminate(quitgame), so every
        // transcript ends with a score.
        if (!finished) terminate(Termination.QUITGAME)
    }
}
