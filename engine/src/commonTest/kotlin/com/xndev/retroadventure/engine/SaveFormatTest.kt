package com.xndev.retroadventure.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * A save that restores *almost* everything is worse than no save at all: the
 * game looks right and goes wrong later, far from the cause. So this checks the
 * whole struct round-trips, not a sample of it.
 */
class SaveFormatTest {

    private fun playedGame(): GameState = GameState().apply {
        initialise(1838473132)
        // Move things around so the save has something to lose.
        loc = LOC_BUILDING
        oldloc = LOC_START
        turns = 42
        holdng = 2
        dflag = 2
        tally = 12
        closng = true
        blooded = true
        bonus = Adventure.Bonus.SPLATTER
        objectState[LAMP].prop = LAMP_BRIGHT
        objectState[LAMP].place = CARRIED
        dwarves[1].loc = LOC_Y2
        dwarves[1].seen = true
        hintState[3].used = true
        hintState[3].lc = 7
        repeat(20) { rng.randrange(100) }
    }

    @Test
    fun aGameRoundTripsThroughTheSaveFormat() {
        val original = playedGame()
        val text = original.snapshot()

        val loaded = GameState().apply { initialise(1) }
        loaded.restore(text)

        assertEquals(original.snapshot(), loaded.snapshot(), "round trip lost state")
        assertEquals(original.rng.x, loaded.rng.x, "the RNG position must survive")
        assertEquals(original.rng.zzword, loaded.rng.zzword)
        // The restored game must draw the same numbers from here on, or the
        // dwarves behave differently after a reload than before it.
        repeat(20) { assertEquals(original.rng.randrange(100), loaded.rng.randrange(100)) }
    }

    @Test
    fun aForeignFileIsRefused() {
        val e = assertFailsWith<SaveFormatException> {
            GameState().apply { initialise(1) }.restore("hello, I am not a save")
        }
        assertTrue(e.message!!.contains("does not look like a saved game"))
    }

    @Test
    fun aSaveFromAnotherVersionIsRefused() {
        val text = playedGame().snapshot().replace("version=$SAVE_VERSION", "version=9999")
        val e = assertFailsWith<SaveFormatException> {
            GameState().apply { initialise(1) }.restore(text)
        }
        assertTrue(e.message!!.contains("different version"))
    }

    @Test
    fun aDamagedSaveLeavesTheRunningGameAlone() {
        val game = playedGame()
        val before = game.snapshot()
        val truncated = before.lineSequence().filterNot { it.startsWith("objProp=") }
            .joinToString("\n")
        assertFailsWith<SaveFormatException> { game.restore(truncated) }
        assertEquals(before, game.snapshot(), "a failed restore must not half-overwrite")
    }
}
