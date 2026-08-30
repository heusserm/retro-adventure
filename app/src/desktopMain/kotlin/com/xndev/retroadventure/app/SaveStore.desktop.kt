package com.xndev.retroadventure.app

import com.xndev.retroadventure.engine.SaveStore
import java.io.File

private val saveDir: File by lazy {
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

    override fun list(): List<String> =
        saveDir.listFiles { f -> f.extension == "adv" }
            ?.sortedByDescending { it.lastModified() }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
}
