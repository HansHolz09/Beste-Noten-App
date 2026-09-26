@file:OptIn(ExperimentalComposeUiApi::class, InternalComposeUiApi::class, kotlinx.cinterop.ExperimentalForeignApi::class)

package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.uikit.LocalNativeTextInputContext
import androidx.compose.ui.uikit.LocalUIView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hansholz.bestenotenapp.main.LocalNativeKeyboardHandoff
import com.hansholz.bestenotenapp.main.LocalNativeTextInputTint
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UITextField
import kotlin.time.Duration.Companion.milliseconds

@Suppress("UNUSED_PARAMETER")
@Composable
actual fun FullscreenDialog(
    onDismiss: () -> Unit,
    placeAboveAll: Boolean,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                usePlatformInsets = false,
                useSoftwareKeyboardInset = false,
                scrimColor = Color.Transparent,
                animateTransition = false,
            ),
    ) {
        val view = LocalUIView.current
        val nativeTextInputContext = LocalNativeTextInputContext.current
        val scope = rememberCoroutineScope()
        CompositionLocalProvider(
            LocalNativeTextInputTint provides { color -> nativeTextInputContext.updateNativeTextInputTintColor(color) },
            LocalNativeKeyboardHandoff provides { action ->
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
            },
            content = content,
        )
    }
}
