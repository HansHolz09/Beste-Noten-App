package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import com.hansholz.bestenotenapp.main.LocalNativeTextInputTint

@Composable
fun Modifier.nativeTextInputTint(color: Color): Modifier {
    val updateTint = LocalNativeTextInputTint.current ?: return this
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(focused, color) {
        if (focused) {
            withFrameNanos {}
            updateTint(color)
            withFrameNanos {}
            updateTint(color)
        }
    }
    return onFocusChanged { focused = it.isFocused }
}
