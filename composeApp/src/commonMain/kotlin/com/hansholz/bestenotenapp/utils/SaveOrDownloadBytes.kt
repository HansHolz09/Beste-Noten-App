package com.hansholz.bestenotenapp.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

expect suspend fun FileKit.saveOrDownloadBytes(
    bytes: ByteArray,
    suggestedName: String,
    extension: String,
    directory: PlatformFile? = null,
    dialogSettings: FileKitDialogSettings = defaultFileKitDialogSettings(),
)
