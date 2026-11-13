package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.uikit.LocalUIView
import com.hansholz.bestenotenapp.main.LocalNativeKeyboardHandoff
import com.hansholz.bestenotenapp.main.LocalNativeTextInputOptions
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UITextField
import platform.UIKit.UITextInputProtocol
import platform.UIKit.UIView
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun EnhancedNativeTextInputProvider(content: @Composable () -> Unit) {
    val options = remember { PlatformImeOptions { usingNativeTextInput(true) } }
    CompositionLocalProvider(
        LocalNativeTextInputOptions provides options,
        LocalEnhancedTextFieldFocusOwner provides remember { mutableStateOf(null) },
        LocalNativeTextInputRefresh provides rememberNativeTextInputRefresh(),
        LocalNativeKeyboardHandoff provides rememberNativeKeyboardHandoff(),
        content = content,
    )
}

@Composable
private fun rememberNativeKeyboardHandoff(): ((() -> Unit) -> Unit) {
    val view = LocalUIView.current
    val scope = rememberCoroutineScope()
    return remember(view, scope) {
        { action ->
            action()
            dispatch_async(dispatch_get_main_queue()) {
                val input = view.focusedTextInput() as? UIView
                if (input == null || input.canBecomeFirstResponder()) return@dispatch_async
                // Keep the keyboard active while Compose replaces its native text input view.
                // The keeper refuses to resign, so Compose's overlay view (which has no keyboard)
                // cannot grab first responder in between; it hands over to the next text input itself.
                val keeper = KeyboardKeeper()
                keeper.alpha = 0.01
                view.addSubview(keeper)
                keeper.becomeFirstResponder()
                scope.launch {
                    try {
                        withTimeoutOrNull(750.milliseconds) {
                            while (keeper.isFirstResponder()) {
                                val next = view.findTextInput { it !== keeper && it !== input && it.canBecomeFirstResponder() }
                                if (next != null) {
                                    keeper.holding = false
                                    next.becomeFirstResponder()
                                    break
                                }
                                withFrameNanos {}
                            }
                        }
                    } finally {
                        keeper.holding = false
                        keeper.resignFirstResponder()
                        keeper.removeFromSuperview()
                    }
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

@OptIn(ExperimentalForeignApi::class)
private class KeyboardKeeper : UITextField(frame = CGRectMake(0.0, 0.0, 1.0, 1.0)) {
    var holding = true

    override fun canResignFirstResponder(): Boolean = !holding
}

private fun UIView.findTextInput(predicate: (UIView) -> Boolean): UIView? =
    subviews.firstNotNullOfOrNull { child ->
        (child as? UIView)?.let { if (it is UITextInputProtocol && predicate(it)) it else it.findTextInput(predicate) }
    }

private fun UIView.focusedTextInput(): UITextInputProtocol? {
    if (isFirstResponder()) return this as? UITextInputProtocol
    return subviews.firstNotNullOfOrNull { (it as? UIView)?.focusedTextInput() }
}
