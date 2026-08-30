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
fun App(seed: Int = Random.nextInt()) {
    val scope = rememberCoroutineScope()
    val session = remember { GameSession(seed) }
    var transcript by remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    var showAbout by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }

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
                    text = transcript,
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
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { submit() }) { Text("Go") }
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
