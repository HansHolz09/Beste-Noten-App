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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.cupertinoHighlight
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import top.ltfan.multihaptic.compose.rememberVibrator

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EnhancedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val vibrator = rememberVibrator()
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val shape = shapes.extraExtraLarge

    Button(
        onClick = {
            onClick()
            vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
        },
        shapes =
            ButtonShapes(
                shape = shape,
                pressedShape = if (getPlatform() == Platform.ANDROID) shapes.small else shape,
            ),
        modifier = modifier.cupertinoHighlight(resolvedInteractionSource, shape, verticalInset = 4.dp, capsule = true),
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = resolvedInteractionSource,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EnhancedOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val vibrator = rememberVibrator()
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val shape = shapes.extraExtraLarge

    OutlinedButton(
        onClick = {
            onClick()
            vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
        },
        shapes =
            ButtonShapes(
                shape = shape,
                pressedShape = if (getPlatform() == Platform.ANDROID) shapes.small else shape,
            ),
        modifier = modifier.cupertinoHighlight(resolvedInteractionSource, shape, verticalInset = 4.dp, capsule = true),
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = resolvedInteractionSource,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EnhancedTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val vibrator = rememberVibrator()
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val shape = shapes.extraExtraLarge

    TextButton(
        onClick = {
            onClick()
            vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
        },
        shapes =
            ButtonShapes(
                shape = shape,
                pressedShape = if (getPlatform() == Platform.ANDROID) shapes.small else shape,
            ),
        modifier = modifier.cupertinoHighlight(resolvedInteractionSource, shape, verticalInset = 4.dp, capsule = true),
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = resolvedInteractionSource,
        content = content,
    )
}
