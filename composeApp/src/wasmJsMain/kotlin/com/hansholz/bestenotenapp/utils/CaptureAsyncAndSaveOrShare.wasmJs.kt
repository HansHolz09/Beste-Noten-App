package com.hansholz.bestenotenapp.utils

import dev.wonddak.capturable.controller.CaptureController
import dev.wonddak.capturable.extension.CapturableSaveImageType
import dev.wonddak.capturable.extension.encodeToByteArray
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.download
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun CaptureController.captureAsyncAndSaveOrShare(
    fileName: String,
    imageType: CapturableSaveImageType,
) = withContext(Dispatchers.Unconfined) {
    val imageBitmap = this@captureAsyncAndSaveOrShare.captureAsync().await()
    val imageBytes = imageBitmap.encodeToByteArray(imageType)
    FileKit.download(imageBytes, "$fileName.${imageType.suffix}")
}
