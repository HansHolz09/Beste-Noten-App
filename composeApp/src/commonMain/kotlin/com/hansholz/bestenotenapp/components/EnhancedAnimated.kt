package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.nomanr.animate.compose.animated.Animated
import com.nomanr.animate.compose.animated.AnimatedState
import com.nomanr.animate.compose.animated.rememberAnimatedState
import com.nomanr.animate.compose.core.AnimationPreset

@Composable
fun EnhancedAnimated(
    modifier: Modifier = Modifier,
    preset: AnimationPreset,
    durationMillis: Int = 1000,
    enabled: Boolean = true,
    repeat: Boolean = false,
    animateOnEnter: Boolean = true,
    state: AnimatedState = rememberAnimatedState(),
    content: @Composable () -> Unit
) {
    if (LocalAnimationsEnabled.current.value) {
        Animated(
            modifier = modifier,
            preset = preset,
            durationMillis = durationMillis,
            enabled = enabled,
            repeat = repeat,
            animateOnEnter = animateOnEnter,
            state = state,
            content = content
        )
    } else {
        content()
    }
}