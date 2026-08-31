package com.xndev.retroadventure.app

import androidx.compose.runtime.Composable

/** Desktop has no background state worth saving on; Save covers it. */
@Composable
actual fun OnBackground(onBackground: () -> Unit) = Unit
