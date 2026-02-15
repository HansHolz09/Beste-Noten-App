package com.hansholz.bestenotenapp.utils

import dev.wonddak.capturable.controller.CaptureController
import dev.wonddak.capturable.extension.CapturableSaveImageType

expect suspend fun CaptureController.captureAsyncAndSaveOrShare(
    fileName: String = "capture_shared",
    imageType: CapturableSaveImageType = CapturableSaveImageType.JPEG(100),
)
