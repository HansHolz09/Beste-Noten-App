package com.hansholz.bestenotenapp.screens.website

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64
import kotlin.math.max
import kotlin.math.roundToInt

internal suspend fun besteSchuleBackdropJpeg(
    image: ImageBitmap,
    background: Color,
) = withContext(Dispatchers.Default) {
    runCatching {
        val scale = (2048f / max(image.width, image.height)).coerceAtMost(1f)
        val size = IntSize((image.width * scale).roundToInt().coerceAtLeast(1), (image.height * scale).roundToInt().coerceAtLeast(1))
        val flattened = ImageBitmap(size.width, size.height)
        Canvas(flattened).apply {
            drawRect(0f, 0f, size.width.toFloat(), size.height.toFloat(), Paint().apply { color = background })
            drawImageRect(
                image = image,
                srcSize = IntSize(image.width, image.height),
                dstOffset = IntOffset.Zero,
                dstSize = size,
                paint = Paint().apply { filterQuality = FilterQuality.Medium },
            )
        }
        encodeJpeg(flattened)?.let { Base64.encode(it) }
    }.getOrNull()
}

internal expect fun encodeJpeg(image: ImageBitmap): ByteArray?
