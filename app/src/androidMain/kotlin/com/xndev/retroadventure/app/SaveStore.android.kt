package com.xndev.retroadventure.app

import android.content.Context
import com.xndev.retroadventure.engine.SaveStore
import java.io.File

/**
 * Set once from MainActivity. Android needs a Context to find its files
 * directory, and the expect/actual signature has nowhere to pass one.
 */
internal var appContext: Context? = null

private fun saveDir(): File? =
    appContext?.let { File(it.filesDir, "saves").apply { mkdirs() } }

private fun slot(dir: File, name: String) =
    File(dir, "${name.replace(Regex("[^A-Za-z0-9_.-]"), "_")}.adv")

actual fun platformSaveStore(): SaveStore = object : SaveStore {
    override fun write(name: String, data: String): Boolean {
        val dir = saveDir() ?: return false
        return try { slot(dir, name).writeText(data); true } catch (_: Exception) { false }
    }

    override fun read(name: String): String? {
        val dir = saveDir() ?: return null
        return slot(dir, name).takeIf { it.isFile }
            ?.let { runCatching { it.readText() }.getOrNull() }
    }

    override fun list(): List<String> =
        saveDir()?.listFiles { f -> f.extension == "adv" }
            ?.sortedByDescending { it.lastModified() }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
}
