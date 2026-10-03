package com.hansholz.bestenotenapp.utils

import androidx.compose.ui.graphics.ImageBitmap
import com.hansholz.bestenotenapp.components.CaptureController
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.impl.use

actual suspend fun CaptureController.captureAsyncAndSaveOrShare(fileName: String) {
    withContext(Dispatchers.IO) {
        val imageBitmap = this@captureAsyncAndSaveOrShare.capture()
        val imageBytes = imageBitmap.encodeToJpeg()
        val file =
            FileKit.openFileSaver(
                suggestedName = fileName,
                defaultExtension = "jpeg",
            )
        file?.write(bytes = imageBytes)
    }
}

internal actual fun ImageBitmap.encodeToJpeg(): ByteArray =
    Image
        .makeRaster(
            ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.UNPREMUL),
            toRgbaBytes(),
            width * 4,
        ).use { image ->
            image.encodeToData(EncodedImageFormat.JPEG, 100)?.use { it.bytes }
                ?: error("Image could not be saved.")
        }
