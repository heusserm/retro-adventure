package com.xndev.retroadventure.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
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
fun App(
    seed: Int = Random.nextInt(),
    saves: SaveStore = platformSaveStore(),
    settings: Settings = platformSettings(),
) {
    val scope = rememberCoroutineScope()
    val focus: FocusManager = LocalFocusManager.current
    // Bumping this starts a fresh game: it re-keys the remember below, so the
    // old session and its transcript go away entirely.
    var generation by remember { mutableStateOf(0) }
    val session = remember(generation) { GameSession(seed + generation, saves) }
    var transcript by remember(generation) { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    var showAbout by remember { mutableStateOf(false) }
    var showSaves by remember { mutableStateOf(false) }
    var slots by remember { mutableStateOf(saves.list()) }
    var autosaveOn by remember {
        mutableStateOf(settings.getBoolean(SETTING_AUTOSAVE, true))
    }
    var busy by remember { mutableStateOf(false) }

    LaunchedEffect(session) {
        transcript = session.start(scope)
        // Pick up where they left off. Only the autosave slot resumes silently;
        // a named save is theirs to load when they want it.
        val auto = saves.read(AUTOSAVE_SLOT)
        if (auto != null && generation == 0) {
            if (session.restoreFrom(auto) == null) {
                transcript += session.send("look") ?: ""
                transcript += "\nResumed where you left off.\n"
            }
        }
    }

    // The last moment worth saving in: the OS may kill the process next.
    OnBackground {
        if (autosaveOn) session.snapshot()?.let { saves.write(AUTOSAVE_SLOT, it) }
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
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding)) {
                // Scale with the glass. See Typography.kt for why a tablet gets
                // a bigger font and a narrower column rather than the same text
                // stretched across thirteen inches.
                val fontSize = transcriptFontSize(maxWidth.value).sp
                val column = Modifier.widthIn(max = readingWidthDp(maxWidth.value).dp)
                    .fillMaxWidth()

                Column(
                    Modifier.fillMaxSize().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                val scroll = rememberScrollState()
                LaunchedEffect(transcript) { scroll.animateScrollTo(scroll.maxValue) }

                Text(
                    // Display only -- see reflow(). The engine's own output has
                    // to stay byte-exact for the transcript suite.
                    text = reflow(transcript),
                    fontFamily = FontFamily.Monospace,
                    fontSize = fontSize,
                    modifier = Modifier.weight(1f).then(column).verticalScroll(scroll)
                        // Tap the transcript to put the keyboard away. On a
                        // tablet it otherwise sits there permanently, and on a
                        // phone it covers half of what you are trying to read.
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { focus.clearFocus() },
                )

                Row(
                    column,
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
                    column,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    TextButton(
                        enabled = !busy,
                        onClick = { slots = saves.list(); showSaves = true },
                    ) { Text("Saves", fontSize = 12.sp) }

                    TextButton(onClick = {
                        // Starting over should not leave an autosave that would
                        // silently resume the abandoned game on next launch.
                        saves.delete(AUTOSAVE_SLOT)
                        generation++
                    }) { Text("Restart", fontSize = 12.sp) }
                }

                // The BSD notice has to be reachable from the screen; see
                // AboutDialog. Shortening this line is fine, removing the way
                // in is not.
                TextButton(onClick = { showAbout = true }) {
                    Text(ATTRIBUTION_LINE, fontSize = 11.sp)
                }
                }
            }

            if (showAbout) {
                AboutDialog(onDismiss = { showAbout = false })
            }

            if (showSaves) {
                SavesDialog(
                    slots = slots,
                    autosaveOn = autosaveOn,
                    onAutosaveChange = {
                        autosaveOn = it
                        settings.putBoolean(SETTING_AUTOSAVE, it)
                    },
                    onSave = { name ->
                        val data = session.snapshot()
                        when {
                            data == null -> note("The game has not started yet.")
                            saves.write(name, data) -> { slots = saves.list(); note("Saved as $name.") }
                            else -> note("Could not save.")
                        }
                        showSaves = false
                    },
                    onLoad = { name ->
                        val data = saves.read(name)
                        if (data == null) {
                            note("There is no save called $name.")
                        } else {
                            val error = session.restoreFrom(data)
                            if (error != null) {
                                note(error)
                            } else {
                                note("Loaded $name.")
                                busy = true
                                scope.launch {
                                    transcript += session.send("look") ?: ""
                                    busy = false
                                }
                            }
                        }
                        showSaves = false
                    },
                    onDelete = { name -> saves.delete(name); slots = saves.list() },
                    onDismiss = { showSaves = false },
                )
            }
        }
    }
}
