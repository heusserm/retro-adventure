package com.xndev.retroadventure.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The license notice, and it is not optional.
 *
 * Open Adventure is BSD-2-Clause, and clause 2 requires a binary distribution to
 * reproduce the copyright notice, these conditions and the disclaimer "in the
 * documentation and/or other materials provided with the distribution". For an
 * app with no documentation, that means here, reachable from the screen.
 *
 * EncounterDeck shipped an About dialog that nothing ever composed, so its
 * license text was unreachable for three releases and every screenshot looked
 * correct. `AppTest` opens this one and asserts the attribution appears; keep
 * that test alive.
 */
const val ATTRIBUTION_LINE = "Based on Open Adventure by Crowther, Woods and Raymond."

private const val BSD_NOTICE = """Copyright (c) 1977, 2005 by Will Crowther and Don Woods
Game data copyright Eric S. Raymond

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

This app is a Kotlin port of the 430-point version. The game text and data are
reproduced as published, with one modification: the instructions credit the
porting work alongside the original authors."""

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = { Text("Adventure 430") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(ATTRIBUTION_LINE, fontSize = 13.sp)
                Text("", fontSize = 13.sp)
                Text(BSD_NOTICE, fontSize = 11.sp, textAlign = TextAlign.Start)
            }
        },
    )
}
