package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap

@Composable
fun RepeatingBackground(imageBitmap: ImageBitmap, modifier: Modifier = Modifier) {
    val imageWidth = imageBitmap.width
    val imageHeight = imageBitmap.height

    BoxWithConstraints(modifier = modifier) {
        val boxWidth = constraints.maxWidth
        val boxHeight = constraints.maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (x in 0 until boxWidth step imageWidth) {
                for (y in 0 until boxHeight step imageHeight) {
                    drawImage(imageBitmap, Offset(x.toFloat(), y.toFloat()))
                }
            }
        }
    }
}
