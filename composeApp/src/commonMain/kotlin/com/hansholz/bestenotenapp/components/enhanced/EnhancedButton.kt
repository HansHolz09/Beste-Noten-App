package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.ltfan.multihaptic.compose.rememberVibrator

@Composable
fun EnhancedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    contentPadding: PaddingValues = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val vibrator = rememberVibrator()

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    Button(
        onClick = {
            onClick()
            vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
        },
        shapes =
            ButtonShapes(
                shape = shapes.extraExtraLarge,
                pressedShape = shapes.small,
            ),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun EnhancedOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    contentPadding: PaddingValues = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val vibrator = rememberVibrator()

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    OutlinedButton(
        onClick = {
            onClick()
            vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
        },
        shapes =
            ButtonShapes(
                shape = shapes.extraExtraLarge,
                pressedShape = shapes.small,
            ),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}
