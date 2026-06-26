package com.hansholz.bestenotenapp.utils

import dev.wonddak.capturable.controller.CaptureController
import dev.wonddak.capturable.extension.CapturableSaveImageType
import dev.wonddak.capturable.extension.encodeToByteArray
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun CaptureController.captureAsyncAndSaveOrShare(
    fileName: String,
    imageType: CapturableSaveImageType,
) {
    withContext(Dispatchers.IO) {
        val imageBitmap = this@captureAsyncAndSaveOrShare.captureAsync().await()
        val imageBytes = imageBitmap.encodeToByteArray(imageType)
        val file =
            FileKit.openFileSaver(
                suggestedName = fileName,
                defaultExtension = imageType.suffix,
            )
        file?.write(bytes = imageBytes)
    }
}
