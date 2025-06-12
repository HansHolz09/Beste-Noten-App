package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun RepeatingBackground(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    scale: Float = 1.0f,
    offset: Offset = Offset.Zero
) {
    require(scale > 0f) { "Scale must be positive" }

    val scaledWidthFloat = imageBitmap.width * scale
    val scaledHeightFloat = imageBitmap.height * scale

    val scaledWidthInt = max(1, scaledWidthFloat.roundToInt())
    val scaledHeightInt = max(1, scaledHeightFloat.roundToInt())

    BoxWithConstraints(modifier = modifier) {
        val boxWidth = constraints.maxWidth
        val boxHeight = constraints.maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            val startX = (offset.x % scaledWidthFloat).let { if (it > 0) it - scaledWidthFloat else it }
            val startY = (offset.y % scaledHeightFloat).let { if (it > 0) it - scaledHeightFloat else it }

            val startXInt = startX.roundToInt()
            val startYInt = startY.roundToInt()

            val scaledSize = IntSize(scaledWidthInt, scaledHeightInt)

            for (x in startXInt until boxWidth step scaledWidthInt) {
                for (y in startYInt until boxHeight step scaledHeightInt) {
                    drawImage(
                        image = imageBitmap,
                        dstOffset = IntOffset(x, y),
                        dstSize = scaledSize
                    )
                }
            }
        }
    }
}

fun Modifier.repeatingBackground(
    imageBitmap: ImageBitmap,
    alpha: Float = 1.0f,
    scale: Float = 1.0f,
    offset: Offset = Offset.Zero
): Modifier {
    require(scale > 0f) { "Scale must be positive" }

    return this.drawBehind {
        val scaledWidthFloat = imageBitmap.width * scale
        val scaledHeightFloat = imageBitmap.height * scale

        val scaledWidthInt = max(1, scaledWidthFloat.roundToInt())
        val scaledHeightInt = max(1, scaledHeightFloat.roundToInt())

        val canvasWidth = size.width.roundToInt()
        val canvasHeight = size.height.roundToInt()

        val startX = (offset.x % scaledWidthFloat).let { if (it > 0) it - scaledWidthFloat else it }
        val startY = (offset.y % scaledHeightFloat).let { if (it > 0) it - scaledHeightFloat else it }

        val startXInt = startX.roundToInt()
        val startYInt = startY.roundToInt()

        val scaledSize = IntSize(scaledWidthInt, scaledHeightInt)

        for (x in startXInt until canvasWidth step scaledWidthInt) {
            for (y in startYInt until canvasHeight step scaledHeightInt) {
                drawImage(
                    image = imageBitmap,
                    dstOffset = IntOffset(x, y),
                    dstSize = scaledSize,
                    alpha = alpha
                )
            }
        }
    }
}