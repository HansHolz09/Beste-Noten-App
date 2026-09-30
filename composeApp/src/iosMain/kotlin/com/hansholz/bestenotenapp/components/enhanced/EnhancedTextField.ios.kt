package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.uikit.LocalNativeTextInputContext
import androidx.compose.ui.uikit.LocalUIView
import com.hansholz.bestenotenapp.main.LocalNativeKeyboardHandoff
import com.hansholz.bestenotenapp.main.LocalNativeTextInputOptions
import com.hansholz.bestenotenapp.main.LocalNativeTextInputTint
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UITextField
import platform.UIKit.UITextInputProtocol
import platform.UIKit.UIView
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalComposeUiApi::class, InternalComposeUiApi::class)
@Composable
internal fun EnhancedNativeTextInputProvider(content: @Composable () -> Unit) {
    val options = remember { PlatformImeOptions { usingNativeTextInput(true) } }
    val context = LocalNativeTextInputContext.current
    CompositionLocalProvider(
        LocalNativeTextInputOptions provides options,
        LocalEnhancedTextFieldFocusOwner provides remember { mutableStateOf<Any?>(null) },
        LocalNativeTextInputRefresh provides rememberNativeTextInputRefresh(),
        LocalNativeTextInputTint provides { color -> context.updateNativeTextInputTintColor(color) },
        LocalNativeKeyboardHandoff provides rememberNativeKeyboardHandoff(),
        content = content,
    )
}

@OptIn(ExperimentalForeignApi::class)
@Composable
private fun rememberNativeKeyboardHandoff(): ((() -> Unit) -> Unit) {
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

@Composable
private fun rememberNativeTextInputRefresh(): () -> Unit {
    val view = LocalUIView.current
    return remember(view) {
        {
            view.focusedTextInput()?.let { input ->
                input.inputDelegate()?.selectionWillChange(input)
                input.inputDelegate()?.selectionDidChange(input)
            }
        }
    }
}

private fun UIView.focusedTextInput(): UITextInputProtocol? {
    if (isFirstResponder()) return this as? UITextInputProtocol
    return subviews.firstNotNullOfOrNull { (it as? UIView)?.focusedTextInput() }
}
