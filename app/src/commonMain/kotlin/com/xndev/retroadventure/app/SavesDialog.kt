package com.xndev.retroadventure.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Saved games: a list to load from, a box to name a new one, and the autosave
 * switch.
 *
 * Named slots rather than a single quicksave, because Adventure is a game you
 * can lock yourself out of -- drop the lamp somewhere stupid and the run is
 * over -- and one overwritable slot means the only recovery is starting again.
 *
 * Autosave lives here rather than in a settings screen of its own; the app has
 * exactly one setting and a screen for it would be ceremony.
 */
@Composable
fun SavesDialog(
    slots: List<String>,
    autosaveOn: Boolean,
    onAutosaveChange: (Boolean) -> Unit,
    onSave: (String) -> Unit,
    onLoad: (String) -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var newName by remember { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        title = { Text("Saved games") },
        text = {
            Column(Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState())) {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Save when I leave the app", Modifier.weight(1f), fontSize = 14.sp)
                    Switch(checked = autosaveOn, onCheckedChange = onAutosaveChange)
                }

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        singleLine = true,
                        label = { Text("Name this save") },
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        enabled = newName.isNotBlank(),
                        onClick = { onSave(newName.trim()); newName = "" },
                    ) { Text("Save") }
                }

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                if (slots.isEmpty()) {
                    Text("No saved games yet.", fontSize = 13.sp)
                } else {
                    for (slot in slots) {
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = slot,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f).clickable { onLoad(slot) },
                            )
                            TextButton(onClick = { onLoad(slot) }) { Text("Load", fontSize = 12.sp) }
                            // Two taps to delete. A save can be an hour of play
                            // and there is no undo behind this.
                            if (confirmDelete == slot) {
                                TextButton(onClick = { onDelete(slot); confirmDelete = null }) {
                                    Text("Sure?", fontSize = 12.sp)
                                }
                            } else {
                                TextButton(onClick = { confirmDelete = slot }) {
                                    Text("Delete", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}
