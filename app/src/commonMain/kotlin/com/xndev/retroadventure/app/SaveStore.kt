package com.xndev.retroadventure.app

import com.xndev.retroadventure.engine.SaveStore

/**
 * Where this platform keeps saved games.
 *
 * The engine cannot open files, so the app supplies storage. Each platform puts
 * it somewhere the OS keeps across upgrades and includes in backups, and
 * nowhere the player has to think about.
 */
expect fun platformSaveStore(): SaveStore

/**
 * The slot autosave writes to.
 *
 * Kept apart from the named slots so that leaving the app can never overwrite a
 * save the player made deliberately. It is also hidden from the slot list for
 * the same reason: it is the app's business, not a save the player manages.
 */
const val AUTOSAVE_SLOT = "__autosave"

/** True if [name] is a slot the player made rather than one the app manages. */
fun isPlayerSlot(name: String): Boolean = !name.startsWith("__")

/** Settings that outlive the process. Small enough not to need a database. */
expect fun platformSettings(): Settings

interface Settings {
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
}

const val SETTING_AUTOSAVE = "autosave"
