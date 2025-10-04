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
    hapticEnabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable (enabled: Boolean) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    IconButton(
        onClick = {
            onClick()
            if (hapticEnabled) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
            }
        },
        shapes = IconButtonDefaults.shapes(),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = { content(enabled) },
    )
}
