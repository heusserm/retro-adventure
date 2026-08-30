package com.xndev.retroadventure.app

import com.xndev.retroadventure.engine.SaveStore

/**
 * Where this platform keeps saved games.
 *
 * The engine cannot open files, so the app supplies storage. Each platform puts
 * it somewhere the OS will keep across upgrades and include in backups, and
 * nowhere the user has to think about.
 */
expect fun platformSaveStore(): SaveStore

/** The slot the Save button writes to. Typing `save` lets you name your own. */
const val QUICK_SAVE = "quicksave"
