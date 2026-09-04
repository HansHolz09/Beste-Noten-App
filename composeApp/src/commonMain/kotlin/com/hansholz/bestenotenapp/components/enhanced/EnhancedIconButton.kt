package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.cupertinoHighlight
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import top.ltfan.multihaptic.compose.rememberVibrator

@Composable
fun EnhancedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isFilled: Boolean = false,
    colors: IconButtonColors = if (isFilled) IconButtonDefaults.filledIconButtonColors() else IconButtonDefaults.iconButtonColors(),
    isExpressive: Boolean = true,
    hapticEnabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable (enabled: Boolean) -> Unit,
) {
    val vibrator = rememberVibrator()
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    IconButton(
        onClick = {
            onClick()
            if (hapticEnabled) {
                vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
            }
        },
        shapes =
            if (isExpressive && getPlatform() == Platform.ANDROID) {
                IconButtonDefaults.shapes()
            } else {
                IconButtonDefaults.shapes(CircleShape, CircleShape)
            },
        modifier =
            modifier.cupertinoHighlight(
                resolvedInteractionSource,
                CircleShape,
                horizontalInset = 4.dp,
                verticalInset = 4.dp,
                capsule = true,
            ),
        enabled = enabled,
        colors = colors,
        interactionSource = resolvedInteractionSource,
        content = { content(enabled) },
    )
}
