package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable

@Composable
expect fun FullscreenDialog(
    onDismiss: () -> Unit = {},
    placeAboveAll: Boolean = false,
    visible: Boolean = true,
    content: @Composable () -> Unit,
)
