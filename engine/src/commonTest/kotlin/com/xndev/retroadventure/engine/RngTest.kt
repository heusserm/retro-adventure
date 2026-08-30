package com.xndev.retroadventure.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The RNG is the foundation the transcript suite stands on: if this drifts,
 * every seeded transcript diverges and the diffs point everywhere except here.
 * The expected values were taken from upstream's `advent` built from the
 * vendored source at the commit in vendor/open-adventure/UPSTREAM-COMMIT.txt,
 * run with `-d` (which prints each draw) -- see AGENTS.md for the recipe.
 */
class RngTest {

    @Test
    fun lcgParametersMatchUpstream() {
        assertEquals(1093, LCG_A)
        assertEquals(221587, LCG_C)
        assertEquals(1048576, LCG_M)
    }

    @Test
    fun seedIsReducedModulusAndNeverNegative() {
        val lcg = Lcg()
        lcg.setSeed(-1)
        assertTrue(lcg.x in 0 until LCG_M, "seed must land in [0, LCG_M): was ${lcg.x}")
        lcg.setSeed(Int.MAX_VALUE)
        assertTrue(lcg.x in 0 until LCG_M)
    }

    @Test
    fun magicWordHasApostropheSecond() {
        // set_seed burns five draws building the bird's magic word and forces
        // the second character to an apostrophe. A port that skips this is off
        // by five draws forever after.
        val lcg = Lcg()
        lcg.setSeed(1838473132)
        assertEquals(5, lcg.zzword.length)
        assertEquals('\'', lcg.zzword[1])
        assertTrue(lcg.zzword[0] in 'A'..'Z')
    }

    @Test
    fun sequenceIsReproducibleFromTheSameSeed() {
        val a = Lcg().apply { setSeed(1838473132) }
        val b = Lcg().apply { setSeed(1838473132) }
        assertEquals(a.zzword, b.zzword)
        repeat(50) { assertEquals(a.randrange(100), b.randrange(100)) }
    }
}
