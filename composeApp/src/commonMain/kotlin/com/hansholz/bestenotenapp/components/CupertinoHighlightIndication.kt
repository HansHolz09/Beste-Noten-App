package com.hansholz.bestenotenapp.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal data class CupertinoHighlightIndication(
    val color: Color,
    val circular: Boolean = false,
    val capsule: Boolean = false,
    val horizontalInset: Dp = 0.dp,
    val verticalInset: Dp = 0.dp,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = CupertinoHighlightNode(interactionSource, color, circular, capsule, horizontalInset, verticalInset)
}

@Composable
internal fun ProvidePlatformInteractionFeedback(content: @Composable () -> Unit) {
    val materialIndication = LocalIndication.current
    val materialRippleConfiguration = LocalRippleConfiguration.current
    val stateColor = MaterialTheme.colorScheme.onSurface
    val indication = remember(stateColor) { CupertinoHighlightIndication(stateColor) }

    @Suppress("DEPRECATION")
    val disabledMaterialRipple =
        remember(stateColor) {
            RippleConfiguration(
                color = stateColor,
                rippleAlpha = RippleAlpha(0f, 0f, 0f, 0f),
            )
        }
    val isAndroid = getPlatform() == Platform.ANDROID
    CompositionLocalProvider(
        LocalIndication provides if (isAndroid) materialIndication else indication,
        LocalRippleConfiguration provides if (isAndroid) materialRippleConfiguration else disabledMaterialRipple,
        content = content,
    )
}

@Composable
internal fun Modifier.cupertinoHighlight(
    interactionSource: InteractionSource,
    shape: Shape,
    horizontalInset: Dp = 0.dp,
    verticalInset: Dp = 0.dp,
    circular: Boolean = false,
    capsule: Boolean = false,
): Modifier {
    if (getPlatform() == Platform.ANDROID) return this
    val color = MaterialTheme.colorScheme.onSurface
    val indication =
        remember(color, circular, capsule, horizontalInset, verticalInset) {
            CupertinoHighlightIndication(color, circular, capsule, horizontalInset, verticalInset)
        }
    return clip(shape).indication(interactionSource, indication)
}

private class CupertinoHighlightNode(
    private val interactionSource: InteractionSource,
    private val color: Color,
    private val circular: Boolean,
    private val capsule: Boolean,
    private val horizontalInset: Dp,
    private val verticalInset: Dp,
) : Modifier.Node(),
    DrawModifierNode {
    private val alpha = Animatable(0f)
    private var animationJob: Job? = null

    override fun onAttach() {
        coroutineScope.launch {
            var activePresses = 0
            var activeHovers = 0
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> activePresses++

                    is PressInteraction.Release,
                    is PressInteraction.Cancel,
                    -> activePresses = (activePresses - 1).coerceAtLeast(0)

                    is HoverInteraction.Enter -> activeHovers++

                    is HoverInteraction.Exit -> activeHovers = (activeHovers - 1).coerceAtLeast(0)
                }
                val target =
                    if (activePresses > 0) {
                        0.1f
                    } else if (activeHovers > 0) {
                        0.055f
                    } else {
                        0f
                    }
                animationJob?.cancel()
                animationJob =
                    coroutineScope.launch {
                        alpha.animateTo(target, tween(if (target > alpha.value) 70 else 160))
                    }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (alpha.value > 0f) {
            val horizontalInsetPx = horizontalInset.toPx()
            val verticalInsetPx = verticalInset.toPx()
            if (circular) {
                drawCircle(
                    color = color.copy(alpha = alpha.value),
                    radius = (size.minDimension / 2f - maxOf(horizontalInsetPx, verticalInsetPx)).coerceAtLeast(0f),
                )
            } else if (capsule) {
                val indicatorSize =
                    Size(
                        width = (size.width - horizontalInsetPx * 2).coerceAtLeast(0f),
                        height = (size.height - verticalInsetPx * 2).coerceAtLeast(0f),
                    )
                drawRoundRect(
                    color = color.copy(alpha = alpha.value),
                    topLeft =
                        Offset(horizontalInsetPx, verticalInsetPx),
                    size = indicatorSize,
                    cornerRadius =
                        CornerRadius(indicatorSize.height / 2f),
                )
            } else {
                drawRect(color.copy(alpha = alpha.value))
            }
        }
    }
}
