package com.hansholz.bestenotenapp.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.hansholz.bestenotenapp.components.CaptureController
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.dialogs.shareFile
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

actual suspend fun CaptureController.captureAsyncAndSaveOrShare(fileName: String) =
    withContext(Dispatchers.IO) {
        val imageBitmap = this@captureAsyncAndSaveOrShare.capture()
        val imageBytes = imageBitmap.encodeToJpeg()
        val file = PlatformFile(FileKit.cacheDir, "$fileName.jpeg")
        file.write(bytes = imageBytes)
        FileKit.shareFile(file)
    }

internal actual fun ImageBitmap.encodeToJpeg(): ByteArray =
    ByteArrayOutputStream().use { bytes ->
        check(asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, bytes))
        bytes.toByteArray()
    }
