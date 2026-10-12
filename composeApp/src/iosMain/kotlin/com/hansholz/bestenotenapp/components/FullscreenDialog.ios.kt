package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
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
    // TODO: Remove in CMP 1.13.0 Migration
    // Read the host window size before entering the dialog's separate Compose scene.
    // During UIKit resize/presentation transitions that scene can have unbounded constraints:
    // fillMaxSize then wraps content, collapsing weighted content and native backdrops to zero.
    val windowSize = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val viewportModifier =
        if (windowSize.width > 0 && windowSize.height > 0) {
            with(density) {
                Modifier.requiredSize(windowSize.width.toDp(), windowSize.height.toDp())
            }
        } else {
            Modifier.fillMaxSize()
        }
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
            Box(viewportModifier) {
                EnhancedNativeTextInputProvider(content)
            }
        }
    }
}
