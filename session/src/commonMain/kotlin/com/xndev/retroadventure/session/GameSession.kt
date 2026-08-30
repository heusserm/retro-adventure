package com.xndev.retroadventure.session

import com.xndev.retroadventure.engine.Adventure
import com.xndev.retroadventure.engine.Output
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Bridges the engine's pull-based loop to a UI's push-based one.
 *
 * The engine is a 1977 program in shape: it runs a loop, and when it wants a
 * line it asks for one and waits. A Compose screen is the other way round --
 * the user types, an event fires, and something is expected to return. Those two
 * models do not compose directly, and the usual fix is to rewrite the game loop
 * as a resumable state machine. That would mean restructuring every mid-turn
 * question ("Would you like instructions?", "Do you want the hint?") and is
 * exactly the kind of change that silently breaks transcript fidelity.
 *
 * So instead the loop keeps its shape and runs on its own coroutine, and this
 * class turns it inside out with a pair of rendezvous channels. [send] hands a
 * line to the engine and returns everything the engine printed before it asked
 * for the next one -- which is precisely one turn's output.
 *
 * The one wart is the `runBlocking` inside the [Adventure.InputSource]:
 * `readLine()` is an ordinary function, so it cannot suspend. Blocking is safe
 * here because the engine coroutine owns its thread on `Dispatchers.Default` and
 * nothing else is scheduled behind it. This is deliberate, not an oversight; the
 * alternative is the state-machine rewrite above.
 *
 * This module exists so that `engine/` stays dependency-free. Coroutines live
 * here, not there.
 */
class GameSession(private val seed: Int) {

    private val inbox = Channel<String>(Channel.RENDEZVOUS)
    private val outbox = Channel<String>(Channel.RENDEZVOUS)
    private val out = Output()
    private var job: Job? = null

    /** True until the engine loop finishes (the player quit, or input ran out). */
    val isRunning: Boolean get() = job?.isActive == true

    /**
     * Start the game and return its opening text, up to the first prompt.
     *
     * [scope] should outlive the session -- a `viewModelScope`, or a scope tied
     * to the screen. Cancelling it ends the game.
     */
    suspend fun start(scope: CoroutineScope): String {
        check(job == null) { "session already started" }
        job = scope.launch(Dispatchers.Default) {
            val adventure = Adventure(
                input = {
                    runBlocking {
                        // Handing over the accumulated output and waiting for
                        // the next line are the same event: the engine only
                        // asks for input once it has finished talking.
                        outbox.send(out.take())
                        try {
                            inbox.receive()
                        } catch (_: ClosedReceiveChannelException) {
                            null // treat a closed inbox as end of input
                        }
                    }
                },
                out = out,
            )
            try {
                adventure.run(seed)
            } finally {
                outbox.send(out.take())
                outbox.close()
            }
        }
        return outbox.receive()
    }

    /**
     * Send one line to the game and return everything it says in response.
     * Returns null once the game has ended.
     */
    suspend fun send(line: String): String? {
        if (!isRunning) return null
        inbox.send(line)
        return try {
            outbox.receive()
        } catch (_: ClosedReceiveChannelException) {
            null
        }
    }

    /** End the game and release the engine coroutine. */
    fun close() {
        inbox.close()
        job?.cancel()
    }
}
