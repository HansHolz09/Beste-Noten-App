package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled

@Composable
fun EnhancedAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = shrinkOut() + fadeOut(),
    animationEnabled: Boolean = true,
    label: String = "AnimatedVisibility",
    content: @Composable () -> Unit,
) {
    if (LocalAnimationsEnabled.current.value && animationEnabled) {
        AnimatedVisibility(
            visible = visible,
            modifier = modifier,
            enter = enter,
            exit = exit,
            label = label,
        ) {
            content()
        }
    } else {
        if (visible) {
            Box(modifier) {
                content()
            }
        }
    }
}

@Composable
fun RowScope.EnhancedAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandHorizontally(),
    exit: ExitTransition = fadeOut() + shrinkHorizontally(),
    animationEnabled: Boolean = true,
    label: String = "AnimatedVisibility",
    content: @Composable () -> Unit,
) {
    if (LocalAnimationsEnabled.current.value && animationEnabled) {
        AnimatedVisibility(
            visible = visible,
            modifier = modifier,
            enter = enter,
            exit = exit,
            label = label,
        ) {
            content()
        }
    } else {
        if (visible) {
            Box(modifier) {
                content()
            }
        }
    }
}

@Composable
fun ColumnScope.EnhancedAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandVertically(),
    exit: ExitTransition = fadeOut() + shrinkVertically(),
    animationEnabled: Boolean = true,
    label: String = "AnimatedVisibility",
    content: @Composable () -> Unit,
) {
    if (LocalAnimationsEnabled.current.value && animationEnabled) {
        AnimatedVisibility(
            visible = visible,
            modifier = modifier,
            enter = enter,
            exit = exit,
            label = label,
        ) {
            content()
        }
    } else {
        if (visible) {
            Box(modifier) {
                content()
            }
        }
    }
}
