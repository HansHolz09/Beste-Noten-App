package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled

@Composable
fun <S> EnhancedAnimatedContent(
    targetState: S,
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<S>.() -> ContentTransform = {
        (
            fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90))
        ).togetherWith(fadeOut(animationSpec = tween(90)))
    },
    contentAlignment: Alignment = Alignment.TopStart,
    animationEnabled: Boolean = true,
    label: String = "AnimatedContent",
    contentKey: (targetState: S) -> Any? = { it },
    content:
        @Composable()
        (targetState: S) -> Unit,
) {
    if (LocalAnimationsEnabled.current.value && animationEnabled) {
        AnimatedContent(
            targetState = targetState,
            modifier = modifier,
            transitionSpec = transitionSpec,
            contentAlignment = contentAlignment,
            label = label,
            contentKey = contentKey,
        ) {
            content(it)
        }
    } else {
        content(targetState)
    }
}
