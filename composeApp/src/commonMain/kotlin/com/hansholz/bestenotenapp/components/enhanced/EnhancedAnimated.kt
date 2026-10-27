package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.nomanr.animate.compose.animated.Animated
import com.nomanr.animate.compose.animated.AnimatedState
import com.nomanr.animate.compose.animated.rememberAnimatedState
import com.nomanr.animate.compose.core.AnimationPreset
import com.nomanr.animate.compose.presets.zoomingextrances.ZoomIn

@Composable
fun EnhancedAnimated(
    modifier: Modifier = Modifier,
    preset: AnimationPreset,
    durationMillis: Int = 1000,
    enabled: Boolean = true,
    repeat: Boolean = false,
    animateOnEnter: Boolean = true,
    state: AnimatedState = rememberAnimatedState(),
    content: @Composable (isAnimated: Boolean) -> Unit,
) {
    if (LocalAnimationsEnabled.current.value && enabled) {
        if (preset is ZoomIn && !repeat && animateOnEnter) {
            ZoomInAnimated(
                modifier = modifier,
                durationMillis = durationMillis,
            ) {
                content(true)
            }
        } else {
            Animated(
                modifier = modifier,
                preset = preset,
                durationMillis = durationMillis,
                enabled = enabled,
                repeat = repeat,
                animateOnEnter = animateOnEnter,
                state = state,
            ) {
                content(true)
            }
        }
    } else {
        Box(modifier) {
            content(false)
        }
    }
}

private val zoomInEasing = CubicBezierEasing(0.25f, 0.10f, 0.25f, 1f)

@Composable
private fun ZoomInAnimated(
    modifier: Modifier,
    durationMillis: Int,
    content: @Composable () -> Unit,
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(durationMillis, easing = LinearEasing))
    }
    Box(
        Modifier
            .graphicsLayer {
                val value = progress.value
                val scale = lerp(0.33f, 1f, zoomInEasing.transform(value))
                scaleX = scale
                scaleY = scale
                alpha = if (value <= 0.5f) lerp(0.3f, 1f, zoomInEasing.transform(value / 0.5f)) else 1f
            }.then(modifier),
    ) {
        content()
    }
}
