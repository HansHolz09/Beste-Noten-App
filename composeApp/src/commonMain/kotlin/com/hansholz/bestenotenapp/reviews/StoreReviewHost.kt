package com.hansholz.bestenotenapp.reviews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hansholz.bestenotenapp.components.enhanced.hasActiveEnhancedDialogs
import com.hansholz.bestenotenapp.main.ViewModel

internal val LocalStoreReviewManager = compositionLocalOf<StoreReviewManager?> { null }

@Composable
internal fun StoreReviewHost(
    viewModel: ViewModel,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val eligible = rememberUpdatedState(viewModel.user.value != null && !viewModel.isDemoAccount.value)
    val keyboardVisible = rememberUpdatedState(WindowInsets.ime.getBottom(LocalDensity.current) > 0)
    val manager =
        remember(viewModel, lifecycle) {
            StoreReviewManager(viewModel.kSafe, scope) {
                eligible.value && lifecycle.currentState == Lifecycle.State.RESUMED && !hasActiveEnhancedDialogs && !keyboardVisible.value
            }
        }
    LaunchedEffect(manager, eligible.value) {
        if (eligible.value) manager.recordUsage() else manager.cancelPending()
    }
    LaunchedEffect(hasActiveEnhancedDialogs) {
        if (hasActiveEnhancedDialogs) manager.cancelPending()
    }
    DisposableEffect(manager, lifecycle) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) manager.cancelPending()
            }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            manager.cancelPending()
        }
    }
    CompositionLocalProvider(LocalStoreReviewManager provides manager) {
        Box(
            Modifier.fillMaxSize().pointerInput(manager) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        if (event.changes.any { it.pressed || it.previousPressed || it.scrollDelta != androidx.compose.ui.geometry.Offset.Zero }) {
                            manager.cancelPending()
                        }
                    }
                }
            },
        ) {
            content()
        }
    }
}

@Composable
internal fun ReviewAfterDismiss(
    visible: Boolean,
    ready: Boolean = true,
) {
    val manager = LocalStoreReviewManager.current
    var wasVisible by remember { mutableStateOf(false) }
    var wasReady by remember { mutableStateOf(false) }
    LaunchedEffect(visible, ready) {
        if (visible || wasVisible || !ready) manager?.cancelPending()
        if (wasVisible && !visible && wasReady && ready) manager?.completedAction()
        wasVisible = visible
        wasReady = ready
    }
    DisposableEffect(manager) {
        onDispose { manager?.cancelPending() }
    }
}
