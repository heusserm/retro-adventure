package com.xndev.retroadventure.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertNotNull
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Proves the UI bridge works the way a screen would drive it: push a line, get
 * that turn's text back, repeat.
 *
 * This test runs on the JVM *and* on Kotlin/Native, which is the point. The
 * bridge blocks a background thread inside `runBlocking`, and whether that is
 * safe on Native is the one thing about this design that could not be settled by
 * reading the JVM docs. If this test passes on `iosSimulatorArm64`, the input
 * model holds for the iOS app and the engine loop does not need restructuring.
 */
class GameSessionTest {

    private fun scope() = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Test
    fun openingTextArrivesBeforeAnyInput() = runTest {
        val session = GameSession(seed = 1)
        val opening = session.start(scope())
        assertContains(opening, "Welcome to Adventure!!")
        session.close()
    }

    @Test
    fun eachSendReturnsExactlyThatTurnsOutput() = runTest {
        val session = GameSession(seed = 1)
        session.start(scope())

        // Decline the instructions, then walk into the building and look around.
        val declined = assertNotNull(session.send("n"))
        assertContains(declined, "You are standing at the end of a road")

        val inside = assertNotNull(session.send("in"))
        assertContains(inside, "You are inside a building")
        assertContains(inside, "There is a shiny brass lamp nearby.")

        // Output is per turn, not cumulative: the road description does not
        // come back a second time.
        assertTrue(
            !inside.contains("end of a road"),
            "send() should return only the new turn's output, got:\n$inside",
        )

        val taken = assertNotNull(session.send("take lamp"))
        assertContains(taken, "OK")

        session.close()
    }

    /**
     * The opening line is a yes/no question, and anything else has to be
     * refused out loud. A player who types a real command there -- "look" is
     * the obvious one -- must not be met with silence.
     */
    @Test
    fun anonYesNoAnswerToTheOpeningQuestionIsRefusedOutLoud() = runTest {
        val session = GameSession(seed = 1)
        session.start(scope())
        val reply = assertNotNull(session.send("look"))
        assertContains(reply, "Please answer the question.")
        assertContains(reply, "Would you like instructions?")
        session.close()
    }

    /**
     * Save, walk somewhere else, restore, and confirm the game really went
     * back -- not just that a snapshot was produced.
     */
    @Test
    fun snapshotAndRestorePutTheGameBackWhereItWas() = runTest {
        val session = GameSession(seed = 1)
        session.start(scope())
        session.send("n")

        val atTheRoad = assertNotNull(session.snapshot())

        val inside = assertNotNull(session.send("in"))
        assertContains(inside, "You are inside a building")

        assertNull(session.restoreFrom(atTheRoad), "restore reported an error")
        val afterRestore = assertNotNull(session.send("look"))
        assertContains(afterRestore, "standing at the end of a road")

        session.close()
    }

    @Test
    fun aDamagedSnapshotIsReportedAndChangesNothing() = runTest {
        val session = GameSession(seed = 1)
        session.start(scope())
        session.send("n")
        session.send("in")

        val error = session.restoreFrom("not a saved game at all")
        assertNotNull(error)
        assertContains(error, "does not look like a saved game")

        // Still inside the building: a refused restore must change nothing.
        val look = assertNotNull(session.send("look"))
        assertContains(look, "inside a building")

        session.close()
    }

    /**
     * The branches nobody exercises by playing: a session that has not started,
     * one that has been closed, and a game that ends on its own. These are the
     * paths a UI hits when the user does something unexpected, and they are
     * exactly where a bridge deadlocks rather than returning.
     */
    @Test
    fun snapshotBeforeStartReturnsNothing() = runTest {
        val session = GameSession(seed = 1)
        assertNull(session.snapshot())
        assertNotNull(session.restoreFrom("anything"))
        assertContains(session.restoreFrom("anything")!!, "has not started")
    }

    @Test
    fun sendingToAClosedSessionReturnsNullRatherThanHanging() = runTest {
        val session = GameSession(seed = 1)
        session.start(scope())
        session.close()
        assertFalse(session.isRunning, "close() should stop the session")
        assertNull(session.send("n"), "a closed session must not block a caller")
    }

    @Test
    fun quittingEndsTheSessionAndFurtherInputIsRefused() = runTest {
        val session = GameSession(seed = 1)
        session.start(scope())
        session.send("n")
        // quit asks for confirmation, then the game prints a score and stops.
        assertNotNull(session.send("quit"))
        val farewell = session.send("y")
        assertNotNull(farewell)
        assertContains(farewell, "You scored")

        assertNull(session.send("look"), "the game is over; input must not block")
        session.close()
    }

    @Test
    fun aSecondStartIsRefused() = runTest {
        val session = GameSession(seed = 1)
        session.start(scope())
        assertFailsWith<IllegalStateException> { session.start(scope()) }
        session.close()
    }

    @Test
    fun theSameSeedGivesTheSameGameThroughTheBridge() = runTest {
        suspend fun play(): String {
            val s = GameSession(seed = 1)
            val sb = StringBuilder(s.start(scope()))
            for (line in listOf("n", "seed 1838473132", "in", "take lamp", "xyzzy", "on")) {
                sb.append(s.send(line) ?: "")
            }
            s.close()
            return sb.toString()
        }
        assertTrue(play() == play(), "the bridge must not introduce nondeterminism")
    }
}
