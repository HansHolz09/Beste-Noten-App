package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.uikit.LocalNativeTextInputContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hansholz.bestenotenapp.main.LocalNativeKeyboardHandoff
import com.hansholz.bestenotenapp.main.LocalNativeTextInputTint

@OptIn(ExperimentalComposeUiApi::class, InternalComposeUiApi::class)
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
        val nativeTextInputContext = LocalNativeTextInputContext.current
        CompositionLocalProvider(
            LocalNativeTextInputTint provides { color -> nativeTextInputContext.updateNativeTextInputTintColor(color) },
            LocalNativeKeyboardHandoff provides rememberNativeKeyboardHandoff(),
            content = content,
        )
    }
}
