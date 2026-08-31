package com.xndev.retroadventure.engine

/**
 * Serializing a game in progress.
 *
 * Upstream writes its `struct game_t` straight to disk as raw bytes. That is
 * fine for one C program on one machine and wrong here for three reasons: the
 * engine compiles for JVM, Android and iOS with different layouts; a phone
 * upgrade must not invalidate a save; and `engine/` takes no dependencies, so
 * there is no serialization library to reach for.
 *
 * So this is a plain line-based text format written by hand. It is verbose and
 * completely legible, which matters when a bug report arrives with a save file
 * attached.
 *
 * [SAVE_VERSION] guards it. A save from a different version is refused rather
 * than half-read: restoring a struct that has gained a field since is how you
 * get a game that looks fine and behaves strangely twenty turns later.
 */

const val SAVE_MAGIC = "retro-adventure-save"
const val SAVE_VERSION = 1

/** Why a save could not be used. The game says something different for each. */
enum class SaveProblem { NOT_A_SAVE, WRONG_VERSION, DAMAGED }

/**
 * Thrown when a save cannot be used. [message] is for a UI that wants to say
 * something itself; the game's own `resume` verb speaks upstream's wording
 * instead, keyed off [problem].
 */
class SaveFormatException(
    val problem: SaveProblem,
    message: String,
    /** The version found in the file, when that is what went wrong. */
    val foundVersion: Int = 0,
) : Exception(message)

private fun StringBuilder.put(key: String, value: Any) {
    append(key).append('=').append(value).append('\n')
}

private fun StringBuilder.putInts(key: String, values: IntArray) {
    append(key).append('=').append(values.joinToString(",")).append('\n')
}

/** Serialize a game in progress. */
fun GameState.snapshot(): String = buildString {
    put("magic", SAVE_MAGIC)
    put("version", SAVE_VERSION)

    put("lcg", rng.x)
    put("zzword", rng.zzword)

    put("abbnum", abbnum); put("chloc", chloc); put("chloc2", chloc2)
    put("clock1", clock1); put("clock2", clock2); put("clshnt", clshnt)
    put("closed", closed); put("closng", closng); put("lmwarn", lmwarn)
    put("novice", novice); put("panic", panic); put("wzdark", wzdark)
    put("blooded", blooded); put("conds", conds); put("detail", detail)
    put("dflag", dflag); put("dkill", dkill); put("dtotal", dtotal)
    put("foobar", foobar); put("holdng", holdng); put("igo", igo)
    put("iwest", iwest); put("knfloc", knfloc); put("limit", limit)
    put("loc", loc); put("newloc", newloc); put("numdie", numdie)
    put("oldloc", oldloc); put("oldlc2", oldlc2); put("oldobj", oldobj)
    put("saved", saved); put("tally", tally); put("thresh", thresh)
    put("seenbigwords", seenbigwords); put("trnluz", trnluz); put("turns", turns)
    put("bonus", bonus.name)

    putInts("conditions", conditions)
    putInts("link", link)
    putInts("locAbbrev", IntArray(NLOCATIONS) { locs[it].abbrev })
    putInts("locAtloc", IntArray(NLOCATIONS) { locs[it].atloc })
    putInts("objFixed", IntArray(NOBJECTS) { objectState[it].fixed })
    putInts("objProp", IntArray(NOBJECTS) { objectState[it].prop })
    putInts("objPlace", IntArray(NOBJECTS) { objectState[it].place })
    putInts("dwarfLoc", IntArray(NDWARVES + 1) { dwarves[it].loc })
    putInts("dwarfOldloc", IntArray(NDWARVES + 1) { dwarves[it].oldloc })
    putInts("dwarfSeen", IntArray(NDWARVES + 1) { if (dwarves[it].seen) 1 else 0 })
    putInts("hintUsed", IntArray(NHINTS) { if (hintState[it].used) 1 else 0 })
    putInts("hintLc", IntArray(NHINTS) { hintState[it].lc })
}

/**
 * Restore a game in progress, or throw [SaveFormatException] if the text is not
 * a usable save. Nothing is written to the state until the whole save has
 * parsed, so a bad file leaves the game running rather than half-overwritten.
 */
fun GameState.restore(text: String) {
    val fields = mutableMapOf<String, String>()
    for (line in text.lineSequence()) {
        if (line.isBlank()) continue
        val i = line.indexOf('=')
        if (i <= 0) throw SaveFormatException(
            SaveProblem.NOT_A_SAVE, "This does not look like a saved game."
        )
        fields[line.substring(0, i)] = line.substring(i + 1)
    }

    if (fields["magic"] != SAVE_MAGIC) {
        throw SaveFormatException(
            SaveProblem.NOT_A_SAVE, "This does not look like a saved game."
        )
    }
    val found = fields["version"]?.toIntOrNull()
    if (found != SAVE_VERSION) {
        throw SaveFormatException(
            SaveProblem.WRONG_VERSION,
            "That save is from a different version of the game and cannot be resumed.",
            foundVersion = found ?: 0,
        )
    }

    fun int(key: String): Int = fields[key]?.toIntOrNull()
        ?: throw SaveFormatException(SaveProblem.DAMAGED, "That saved game is damaged.")
    fun bool(key: String): Boolean = fields[key]?.toBooleanStrictOrNull()
        ?: throw SaveFormatException(SaveProblem.DAMAGED, "That saved game is damaged.")
    fun damaged(): Nothing =
        throw SaveFormatException(SaveProblem.DAMAGED, "That saved game is damaged.")

    fun ints(key: String, expected: Int): IntArray {
        val raw = fields[key] ?: damaged()
        val parts = if (raw.isEmpty()) emptyList() else raw.split(",")
        if (parts.size != expected) damaged()
        return IntArray(expected) { parts[it].toIntOrNull() ?: damaged() }
    }

    // Parse everything before touching the live game.
    val newConditions = ints("conditions", NLOCATIONS)
    val newLink = ints("link", NOBJECTS * 2)
    val locAbbrev = ints("locAbbrev", NLOCATIONS)
    val locAtloc = ints("locAtloc", NLOCATIONS)
    val objFixed = ints("objFixed", NOBJECTS)
    val objProp = ints("objProp", NOBJECTS)
    val objPlace = ints("objPlace", NOBJECTS)
    val dwarfLoc = ints("dwarfLoc", NDWARVES + 1)
    val dwarfOldloc = ints("dwarfOldloc", NDWARVES + 1)
    val dwarfSeen = ints("dwarfSeen", NDWARVES + 1)
    val hintUsed = ints("hintUsed", NHINTS)
    val hintLc = ints("hintLc", NHINTS)
    val newBonus = try {
        Adventure.Bonus.valueOf(fields["bonus"] ?: "NONE")
    } catch (_: IllegalArgumentException) {
        damaged()
    }

    rng.x = int("lcg")
    rng.zzword = fields["zzword"] ?: ""

    abbnum = int("abbnum"); chloc = int("chloc"); chloc2 = int("chloc2")
    clock1 = int("clock1"); clock2 = int("clock2"); clshnt = bool("clshnt")
    closed = bool("closed"); closng = bool("closng"); lmwarn = bool("lmwarn")
    novice = bool("novice"); panic = bool("panic"); wzdark = bool("wzdark")
    blooded = bool("blooded"); conds = int("conds"); detail = int("detail")
    dflag = int("dflag"); dkill = int("dkill"); dtotal = int("dtotal")
    foobar = int("foobar"); holdng = int("holdng"); igo = int("igo")
    iwest = int("iwest"); knfloc = int("knfloc"); limit = int("limit")
    loc = int("loc"); newloc = int("newloc"); numdie = int("numdie")
    oldloc = int("oldloc"); oldlc2 = int("oldlc2"); oldobj = int("oldobj")
    saved = int("saved"); tally = int("tally"); thresh = int("thresh")
    seenbigwords = bool("seenbigwords"); trnluz = int("trnluz"); turns = int("turns")
    bonus = newBonus

    newConditions.copyInto(conditions)
    newLink.copyInto(link)
    for (i in 0 until NLOCATIONS) {
        locs[i].abbrev = locAbbrev[i]
        locs[i].atloc = locAtloc[i]
    }
    for (i in 0 until NOBJECTS) {
        objectState[i].fixed = objFixed[i]
        objectState[i].prop = objProp[i]
        objectState[i].place = objPlace[i]
    }
    for (i in 0..NDWARVES) {
        dwarves[i].loc = dwarfLoc[i]
        dwarves[i].oldloc = dwarfOldloc[i]
        dwarves[i].seen = dwarfSeen[i] != 0
    }
    for (i in 0 until NHINTS) {
        hintState[i].used = hintUsed[i] != 0
        hintState[i].lc = hintLc[i]
    }
}
