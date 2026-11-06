package com.hansholz.bestenotenapp.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventInput
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import platform.QuartzCore.CACurrentMediaTime

private const val DECISION_DELAY_SECONDS = 0.025

internal class TrackpadBackInput : NavigationEventInput() {
    var handlersEnabled = false
        private set

    override fun onHasEnabledHandlersChanged(hasEnabledHandlers: Boolean) {
        handlersEnabled = hasEnabledHandlers
    }

    fun started() = dispatchOnBackStarted(NavigationEvent(swipeEdge = NavigationEvent.EDGE_LEFT))

    fun progressed(progress: Float) = dispatchOnBackProgressed(NavigationEvent(swipeEdge = NavigationEvent.EDGE_LEFT, progress = progress))

    fun cancelled() = dispatchOnBackCancelled()

    fun completed() = dispatchOnBackCompleted()
}

internal class TrackpadBackController(
    private val input: TrackpadBackInput,
) {
    private enum class Phase { Idle, Pending, Active, Rejected }

    private var phase = Phase.Idle
    private var pendingSince = 0.0
    private var fraction = 0f

    val isActive get() = phase == Phase.Active

    fun shouldBegin() = phase == Phase.Idle && input.handlersEnabled && !isNativeDateTimePickerVisible()

    fun changed(fraction: Double) {
        this.fraction = fraction.toFloat().coerceIn(0f, 1f)
        when (phase) {
            Phase.Idle -> {
                phase = Phase.Pending
                pendingSince = CACurrentMediaTime()
            }

            Phase.Pending -> {
                if (CACurrentMediaTime() - pendingSince >= DECISION_DELAY_SECONDS) {
                    phase = Phase.Active
                    TrackpadBackTransition.active = true
                    TrackpadBackTransition.drivesPageTransition = false
                    input.started()
                    input.progressed(progress(this.fraction))
                }
            }

            Phase.Active -> {
                input.progressed(progress(this.fraction))
            }

            Phase.Rejected -> {}
        }
    }

    fun horizontalScroll() {
        if (phase == Phase.Pending) phase = Phase.Rejected
    }

    fun ended(complete: Boolean) {
        val wasActive = phase == Phase.Active
        phase = Phase.Idle
        if (!wasActive) return
        if (complete) input.completed() else input.cancelled()
        TrackpadBackTransition.active = false
    }

    private fun progress(fraction: Float): Float {
        if (!TrackpadBackTransition.drivesPageTransition) return fraction
        var low = 0f
        var high = 1f
        repeat(16) {
            val mid = (low + high) / 2
            if (FastOutSlowInEasing.transform(mid) < fraction) low = mid else high = mid
        }
        return (low + high) / 2
    }
}

@Composable
internal fun rememberTrackpadBackController(): TrackpadBackController {
    val dispatcher = LocalNavigationEventDispatcherOwner.current?.navigationEventDispatcher
    val input = remember { TrackpadBackInput() }
    DisposableEffect(dispatcher, input) {
        dispatcher?.addInput(input)
        onDispose { dispatcher?.removeInput(input) }
    }
    return remember(input) { TrackpadBackController(input) }
}

internal fun Modifier.trackpadBackContent(controller: TrackpadBackController): Modifier =
    nestedScroll(
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (source == NestedScrollSource.UserInput && available.x != 0f) controller.horizontalScroll()
                return Offset.Zero
            }
        },
    ).pointerInput(controller) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (controller.isActive && event.type == PointerEventType.PanMove) {
                    event.changes.forEach { it.consume() }
                }
            }
        }
    }
