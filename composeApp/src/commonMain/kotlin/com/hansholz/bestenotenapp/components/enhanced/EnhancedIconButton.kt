package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.ltfan.multihaptic.compose.rememberVibrator

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
    val vibrator = rememberVibrator()

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    IconButton(
        onClick = {
            onClick()
            if (hapticEnabled) {
                vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
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
