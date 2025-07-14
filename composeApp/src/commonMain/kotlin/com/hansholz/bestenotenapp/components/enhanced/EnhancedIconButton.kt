package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun EnhancedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isFilled: Boolean = false,
    colors: IconButtonColors = if (isFilled) IconButtonDefaults.filledIconButtonColors() else IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    IconButton(
        onClick = {
            onClick()
            hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
        },
        shapes = IconButtonDefaults.shapes(),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = content
    )
}