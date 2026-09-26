package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.hansholz.bestenotenapp.main.LocalNativeKeyboardHandoff

@Composable
fun Modifier.keepKeyboardOnPress(otherFocused: Boolean): Modifier {
    val nativeKeyboardHandoff = LocalNativeKeyboardHandoff.current ?: return this
    return pointerInput(otherFocused, nativeKeyboardHandoff) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            if (otherFocused) nativeKeyboardHandoff {}
        }
    }
}
