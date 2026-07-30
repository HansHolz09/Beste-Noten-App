package com.hansholz.bestenotenapp.data

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.isDirectory
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.readString
import io.github.vinceglb.filekit.size
import io.github.vinceglb.filekit.writeString

actual suspend fun readBesteSchuleCache(
    student: String,
    key: String,
): String? {
    val file = cacheFile(student, key)
    return if (file.exists()) file.readString() else null
}

actual suspend fun writeBesteSchuleCache(
    student: String,
    key: String,
    value: String,
) {
    cacheFile(student, key).writeString(value)
}

actual suspend fun clearBesteSchuleCache() {
    PlatformFile(FileKit.cacheDir, "besteSchule").deleteRecursively()
}

actual suspend fun besteSchuleCacheSize(): Long = PlatformFile(FileKit.cacheDir, "besteSchule").directorySize()

private fun cacheFile(
    student: String,
    key: String,
): PlatformFile {
    val directory = PlatformFile(FileKit.cacheDir, "besteSchule/$student")
    directory.createDirectories()
    return PlatformFile(directory, "$key.json")
}

private suspend fun PlatformFile.deleteRecursively() {
    if (!exists()) return
    if (isDirectory()) list().forEach { it.deleteRecursively() }
    delete(false)
}

private fun PlatformFile.directorySize(): Long =
    when {
        !exists() -> 0
        isDirectory() -> list().sumOf { it.directorySize() }
        else -> size()
    }
