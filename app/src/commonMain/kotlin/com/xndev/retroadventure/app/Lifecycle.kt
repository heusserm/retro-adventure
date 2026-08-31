package com.xndev.retroadventure.app

import androidx.compose.runtime.Composable

/**
 * Calls [onBackground] when the app stops being the thing the user is looking
 * at, which on a phone is the last moment worth saving in: the OS may kill the
 * process afterwards without asking, and a text adventure can represent an hour
 * of play with nothing on disk.
 *
 * Desktop has no equivalent moment -- the window is either open or the process
 * is gone -- so its implementation does nothing and the Save button covers it.
 */
@Composable
expect fun OnBackground(onBackground: () -> Unit)
