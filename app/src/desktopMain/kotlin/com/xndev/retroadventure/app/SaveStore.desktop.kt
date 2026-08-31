package com.xndev.retroadventure.app

import com.xndev.retroadventure.engine.SaveStore
import java.io.File

internal val saveDir: File by lazy {
    File(System.getProperty("user.home"), ".retroadventure").apply { mkdirs() }
}

private fun slot(name: String) = File(saveDir, "${name.replace(Regex("[^A-Za-z0-9_.-]"), "_")}.adv")

actual fun platformSaveStore(): SaveStore = object : SaveStore {
    override fun write(name: String, data: String): Boolean = try {
        slot(name).writeText(data); true
    } catch (_: Exception) {
        false
    }

    override fun read(name: String): String? =
        slot(name).takeIf { it.isFile }?.let { runCatching { it.readText() }.getOrNull() }

    override fun delete(name: String): Boolean = slot(name).delete()

    override fun list(): List<String> =
        saveDir.listFiles { f -> f.extension == "adv" }
            ?.sortedByDescending { it.lastModified() }
            ?.map { it.nameWithoutExtension }
            ?.filter(::isPlayerSlot)
            ?: emptyList()
}

private val settingsFile: File by lazy { File(saveDir, "settings.properties") }

actual fun platformSettings(): Settings = object : Settings {
    private fun load(): java.util.Properties = java.util.Properties().apply {
        if (settingsFile.isFile) settingsFile.inputStream().use { load(it) }
    }

    override fun getBoolean(key: String, default: Boolean): Boolean =
        load().getProperty(key)?.toBooleanStrictOrNull() ?: default

    override fun putBoolean(key: String, value: Boolean) {
        val p = load()
        p.setProperty(key, value.toString())
        runCatching { settingsFile.outputStream().use { p.store(it, null) } }
    }
}
