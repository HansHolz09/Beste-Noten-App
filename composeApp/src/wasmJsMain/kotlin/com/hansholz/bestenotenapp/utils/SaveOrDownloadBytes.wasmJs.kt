package com.hansholz.bestenotenapp.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.download

actual suspend fun FileKit.saveOrDownloadBytes(
    bytes: ByteArray,
    suggestedName: String,
    extension: String,
    directory: PlatformFile?,
    dialogSettings: FileKitDialogSettings,
) = download(bytes, "$suggestedName.$extension")
