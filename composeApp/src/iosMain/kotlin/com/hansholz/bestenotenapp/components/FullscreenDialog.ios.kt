package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hansholz.bestenotenapp.components.enhanced.EnhancedNativeTextInputProvider
import com.hansholz.bestenotenapp.main.LocalNativeAlignmentHaptic

@OptIn(ExperimentalComposeUiApi::class)
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
        CompositionLocalProvider(
            LocalNativeAlignmentHaptic provides rememberNativeAlignmentHaptic(),
        ) {
            EnhancedNativeTextInputProvider(content)
        }
    }
}
