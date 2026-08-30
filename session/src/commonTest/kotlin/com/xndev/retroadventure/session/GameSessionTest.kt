package com.xndev.retroadventure.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertNotNull
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
