package com.xndev.retroadventure.session

import com.xndev.retroadventure.engine.Adventure
import com.xndev.retroadventure.engine.NoSaveStore
import com.xndev.retroadventure.engine.Output
import com.xndev.retroadventure.engine.SaveFormatException
import com.xndev.retroadventure.engine.SaveStore
import com.xndev.retroadventure.engine.restore
import com.xndev.retroadventure.engine.snapshot
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
class GameSession(
    private val seed: Int,
    private val saves: SaveStore = NoSaveStore,
) {

    private val inbox = Channel<String>(Channel.RENDEZVOUS)
    private val outbox = Channel<String>(Channel.RENDEZVOUS)
    private val out = Output()
    private var job: Job? = null
    private var adventure: Adventure? = null

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
                saves = saves,
            )
            this@GameSession.adventure = adventure
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

    /**
     * Snapshot the game between turns, or null if it has not started.
     *
     * Safe to call from the UI thread, but only between turns -- which is the
     * only time the UI can call it anyway. The engine writes its state, hands
     * the turn's output over the rendezvous channel, and then parks waiting for
     * input; receiving that output is the synchronization point, so by the time
     * a caller has it the engine is no longer touching the state.
     *
     * Deliberately not the game's own `save` verb. Upstream's save asks for a
     * filename and then exits, which is right for a 1977 terminal and wrong for
     * an app, where saving should cost you nothing and interrupt nothing. The
     * verb still behaves as upstream does for anyone who types it.
     */
    fun snapshot(): String? = adventure?.game?.snapshot()

    /**
     * Load a snapshot into the running game. Returns an error message, or null
     * on success. The game in progress is untouched if the save is unusable.
     */
    fun restoreFrom(text: String): String? {
        val game = adventure?.game ?: return "The game has not started yet."
        return try {
            game.restore(text)
            null
        } catch (e: SaveFormatException) {
            e.message ?: "That saved game cannot be resumed."
        }
    }

    /** End the game and release the engine coroutine. */
    fun close() {
        inbox.close()
        job?.cancel()
    }
}
