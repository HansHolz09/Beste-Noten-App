package com.hansholz.bestenotenapp.utils

import androidx.compose.ui.graphics.ImageBitmap
import com.hansholz.bestenotenapp.components.CaptureController

expect suspend fun CaptureController.captureAsyncAndSaveOrShare(fileName: String)

internal expect fun ImageBitmap.encodeToJpeg(): ByteArray

internal fun ImageBitmap.toRgbaBytes(): ByteArray {
    val pixels = IntArray(width * height)
    readPixels(pixels)
    return ByteArray(pixels.size * 4).also { bytes ->
        pixels.forEachIndexed { index, pixel ->
            val offset = index * 4
            bytes[offset] = (pixel ushr 16).toByte()
            bytes[offset + 1] = (pixel ushr 8).toByte()
            bytes[offset + 2] = pixel.toByte()
            bytes[offset + 3] = (pixel ushr 24).toByte()
        }
    }
}
