package com.hansholz.bestenotenapp.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.hansholz.bestenotenapp.NativeComponentBridge
import kotlin.random.Random

@Composable
internal fun NativePrimaryTabRow(
    bridge: NativeComponentBridge,
    labels: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope?,
) {
    val owner = remember { "primary-tabs-${Random.nextLong()}" }

    val transition = animatedVisibilityScope?.transition
    val visibility =
        transition?.animateFloat(
            transitionSpec = { tween() },
            label = "native-primary-tabs-visibility",
        ) { state -> if (state == EnterExitState.PostExit) 0f else 1f }
    val exiting = transition?.targetState == EnterExitState.PostExit

    SideEffect {
        bridge.showPrimaryTabs(owner, labels, selectedTabIndex, onTabSelected, replaceOwner = !exiting)
    }
    LaunchedEffect(transition, visibility, owner) {
        if (transition == null || visibility == null) return@LaunchedEffect
        snapshotFlow { visibility.value }.collect { progress ->
            bridge.primaryTabsVisibilityChanged(owner, progress.toDouble())
        }
    }
    DisposableEffect(owner) {
        onDispose { bridge.hidePrimaryTabs(owner) }
    }

    Spacer(modifier)
}
