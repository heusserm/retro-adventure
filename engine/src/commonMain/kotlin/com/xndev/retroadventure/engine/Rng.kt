package com.xndev.retroadventure.engine

/*
 * The linear congruential generator from upstream's misc.c.
 *
 * This is not an implementation detail we are free to change. Almost every
 * transcript in vendor/open-adventure/tests starts with a `seed NNNN` command
 * and then depends on the exact sequence that follows -- which dwarf throws an
 * axe, whether the pirate shows up, what the magic word is. Substituting
 * Kotlin's Random here would make all 103 reproducible transcripts unusable as
 * a correctness oracle, which is the whole reason the port is testable at all.
 *
 * The parameters are upstream's, tested against Knuth vol. 2 section 3.3.4.
 */

const val LCG_A = 1093
const val LCG_C = 221587
const val LCG_M = 1048576

class Lcg {
    /** Matches `game.lcg_x`; part of the saved-game state upstream. */
    var x: Int = 0
        private set

    /** The bird's magic word, regenerated whenever the seed is set. */
    var zzword: String = ""
        private set

    /**
     * Upstream `set_seed()`. Note that setting the seed immediately burns five
     * values generating the magic word -- a port that defers that work will
     * diverge from the transcripts on the very next random draw.
     */
    fun setSeed(seedval: Int) {
        x = seedval % LCG_M
        if (x < 0) x += LCG_M
        val chars = CharArray(5)
        for (i in 0 until 5) {
            chars[i] = ('A'.code + randrange(26)).toChar()
        }
        chars[1] = '\'' // force second char to apostrophe
        zzword = chars.concatToString()
    }

    /** Upstream `get_next_lcg_value()`: return the current value, then iterate. */
    private fun nextLcgValue(): Int {
        val oldX = x
        x = (LCG_A * x + LCG_C) % LCG_M
        return oldX
    }

    /** Upstream `randrange()`: a random integer from [0, range). */
    fun randrange(range: Int): Int = range * nextLcgValue() / LCG_M

    /** Upstream `PCT(n)`: true n percent of the time. */
    fun pct(n: Int): Boolean = randrange(100) < n
}
