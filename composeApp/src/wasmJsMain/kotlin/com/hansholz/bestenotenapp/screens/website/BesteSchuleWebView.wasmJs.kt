package com.hansholz.bestenotenapp.screens.website

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

internal actual val isBesteSchuleWebViewSupported = false

@Composable
internal actual fun isBesteSchuleWebViewTransparent() = false

@Composable
internal actual fun BesteSchuleWebView(
    url: String,
    bridge: BesteSchuleWebBridge,
    transparent: Boolean,
    backgroundColor: Color,
    alpha: Float,
    modifier: Modifier,
) = Unit

internal actual fun encodeJpeg(image: ImageBitmap): ByteArray? = null
