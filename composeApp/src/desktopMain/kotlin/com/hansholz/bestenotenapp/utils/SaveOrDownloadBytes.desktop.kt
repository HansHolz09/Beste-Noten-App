package com.hansholz.bestenotenapp.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.write

actual suspend fun FileKit.saveOrDownloadBytes(
    bytes: ByteArray,
    suggestedName: String,
    extension: String,
    directory: PlatformFile?,
    dialogSettings: FileKitDialogSettings,
) {
    openFileSaver(
        suggestedName = suggestedName,
        defaultExtension = extension,
        allowedExtensions = setOf(extension),
        directory = directory,
        dialogSettings = dialogSettings,
    )?.write(bytes)
}
