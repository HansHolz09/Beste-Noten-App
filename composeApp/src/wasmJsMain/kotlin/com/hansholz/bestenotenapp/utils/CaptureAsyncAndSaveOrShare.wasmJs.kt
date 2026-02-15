package com.hansholz.bestenotenapp.utils

import dev.wonddak.capturable.controller.CaptureController
import dev.wonddak.capturable.extension.CapturableSaveImageType
import dev.wonddak.capturable.extension.captureAsyncAndSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun CaptureController.captureAsyncAndSaveOrShare(
    fileName: String,
    imageType: CapturableSaveImageType,
) = withContext(Dispatchers.Unconfined) {
    captureAsyncAndSave(fileName, imageType)
}
