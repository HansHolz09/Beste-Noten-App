package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

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
            ),
        content = content,
    )
}
