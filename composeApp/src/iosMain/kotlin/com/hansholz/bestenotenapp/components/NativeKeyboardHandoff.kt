package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.uikit.LocalUIView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UITextField
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun rememberNativeKeyboardHandoff(): ((() -> Unit) -> Unit) {
    val view = LocalUIView.current
    val scope = rememberCoroutineScope()
    return remember(view, scope) {
        { action ->
            // Keep the keyboard active while Compose replaces its native text input view.
            val keeper = UITextField(frame = CGRectMake(0.0, 0.0, 1.0, 1.0))
            keeper.alpha = 0.01
            view.addSubview(keeper)
            keeper.becomeFirstResponder()
            scope.launch {
                try {
                    withFrameNanos {}
                    action()
                    withTimeoutOrNull(750.milliseconds) {
                        while (keeper.isFirstResponder()) withFrameNanos {}
                    }
                } finally {
                    keeper.resignFirstResponder()
                    keeper.removeFromSuperview()
                }
            }
        }
    }
}
