package com.xndev.retroadventure.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import com.xndev.retroadventure.engine.SaveStore
import com.xndev.retroadventure.session.GameSession
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * The whole game, which is a scrolling transcript and one text field.
 *
 * That is the right shape for this: Adventure is a conversation, and every
 * attempt to dress it up as buttons loses the parser, which is the game. The
 * engine is driven through [GameSession], so the UI never blocks on it -- see
 * that class for why the loop keeps its 1977 shape.
 */
@Composable
fun App(seed: Int = Random.nextInt(), saves: SaveStore = platformSaveStore()) {
    val scope = rememberCoroutineScope()
    // Bumping this starts a fresh game: it re-keys the remember below, so the
    // old session and its transcript go away entirely.
    var generation by remember { mutableStateOf(0) }
    val session = remember(generation) { GameSession(seed + generation, saves) }
    var transcript by remember(generation) { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    var showAbout by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var hasSave by remember { mutableStateOf(saves.read(QUICK_SAVE) != null) }

    LaunchedEffect(session) {
        transcript = session.start(scope)
    }

    fun submit() {
        val line = input
        if (busy) return
        input = ""
        busy = true
        scope.launch {
            transcript += session.send(line) ?: ""
            busy = false
        }
    }

    fun note(text: String) {
        transcript += "\n$text\n"
    }

    MaterialTheme {
        Scaffold { padding ->
            // Scaffold already supplies the system-bar insets. Adding
            // safeContentPadding() here as well insets twice and leaves a dead
            // band under the status bar -- that bug was live in EncounterDeck
            // for a while and is easy to reintroduce.
            Column(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
                val scroll = rememberScrollState()
                LaunchedEffect(transcript) { scroll.animateScrollTo(scroll.maxValue) }

                Text(
                    // Display only -- see reflow(). The engine's own output has
                    // to stay byte-exact for the transcript suite.
                    text = reflow(transcript),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll),
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        singleLine = true,
                        label = { Text("What now?") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { submit() }),
                        // The IME Send action covers the on-screen keyboard, but
                        // not a hardware Return -- which is what you get in the
                        // iOS simulator and on a tablet with a keyboard. Without
                        // this, Return does nothing and the app looks broken.
                        modifier = Modifier.weight(1f).onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown &&
                                (event.key == Key.Enter || event.key == Key.NumPadEnter)
                            ) {
                                submit(); true
                            } else {
                                false
                            }
                        },
                    )
                    TextButton(onClick = { submit() }) { Text("Go") }
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    // Save and Restore go through GameSession's snapshot API,
                    // not the game's own save verb: upstream's save asks for a
                    // filename and then exits the game, which is right for a
                    // 1977 terminal and wrong for a phone. Typing `save` still
                    // behaves as upstream does.
                    TextButton(
                        enabled = !busy,
                        onClick = {
                            val data = session.snapshot()
                            when {
                                data == null -> note("The game has not started yet.")
                                saves.write(QUICK_SAVE, data) -> {
                                    hasSave = true
                                    note("Saved.")
                                }
                                else -> note("Could not save.")
                            }
                        },
                    ) { Text("Save", fontSize = 12.sp) }

                    TextButton(
                        enabled = !busy && hasSave,
                        onClick = {
                            val data = saves.read(QUICK_SAVE)
                            if (data == null) {
                                note("There is no saved game.")
                            } else {
                                val error = session.restoreFrom(data)
                                if (error != null) {
                                    note(error)
                                } else {
                                    // The loop is parked mid-turn, so it will
                                    // not describe the restored room by itself.
                                    // "look" makes it show you where you are.
                                    note("Restored.")
                                    busy = true
                                    scope.launch {
                                        transcript += session.send("look") ?: ""
                                        busy = false
                                    }
                                }
                            }
                        },
                    ) { Text("Restore", fontSize = 12.sp) }

                    TextButton(onClick = { generation++ }) { Text("Restart", fontSize = 12.sp) }
                }

                // The BSD notice has to be reachable from the screen; see
                // AboutDialog. Shortening this line is fine, removing the way
                // in is not.
                TextButton(onClick = { showAbout = true }) {
                    Text(ATTRIBUTION_LINE, fontSize = 11.sp)
                }
            }

            if (showAbout) {
                AboutDialog(onDismiss = { showAbout = false })
            }
        }
    }
}
