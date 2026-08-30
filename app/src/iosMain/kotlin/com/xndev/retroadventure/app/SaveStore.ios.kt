package com.xndev.retroadventure.app

import com.xndev.retroadventure.engine.SaveStore
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class)
private fun saveDir(): String? {
    val paths = NSSearchPathForDirectoriesInDomains(
        NSDocumentDirectory.toULong(), NSUserDomainMask.toULong(), true
    )
    val base = paths.firstOrNull() as? String ?: return null
    val dir = "$base/saves"
    NSFileManager.defaultManager.createDirectoryAtURL(
        NSURL.fileURLWithPath(dir), true, null, null
    )
    return dir
}

private fun slot(dir: String, name: String) =
    "$dir/${name.replace(Regex("[^A-Za-z0-9_.-]"), "_")}.adv"

@OptIn(ExperimentalForeignApi::class)
actual fun platformSaveStore(): SaveStore = object : SaveStore {
    override fun write(name: String, data: String): Boolean {
        val dir = saveDir() ?: return false
        return (data as NSString).writeToFile(
            slot(dir, name), atomically = true, encoding = NSUTF8StringEncoding, error = null
        )
    }

    override fun read(name: String): String? {
        val dir = saveDir() ?: return null
        return NSString.stringWithContentsOfFile(
            slot(dir, name), encoding = NSUTF8StringEncoding, error = null
        )
    }

    override fun list(): List<String> {
        val dir = saveDir() ?: return emptyList()
        val names = NSFileManager.defaultManager.contentsOfDirectoryAtPath(dir, null)
            ?: return emptyList()
        return names.filterIsInstance<String>()
            .filter { it.endsWith(".adv") }
            .map { it.removeSuffix(".adv") }
            .sorted()
    }
}
