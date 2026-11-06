package com.hansholz.bestenotenapp.components

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.util.VelocityTracker1D
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import kotlin.math.abs

object TrackpadBackTransition {
    internal var active = false
    internal var drivesPageTransition = false

    fun <T> spec(): FiniteAnimationSpec<T> {
        if (active) drivesPageTransition = true
        return tween()
    }
}

fun Modifier.trackpadSwipeToDismiss(
    enabled: Boolean,
    onDrag: (offset: Float) -> Unit,
    onRelease: (offset: Float, velocity: Float) -> Unit,
): Modifier = if (enabled) this then TrackpadSwipeToDismissElement(onDrag, onRelease) else this

private data class TrackpadSwipeToDismissElement(
    val onDrag: (Float) -> Unit,
    val onRelease: (Float, Float) -> Unit,
) : ModifierNodeElement<TrackpadSwipeToDismissNode>() {
    override fun create() = TrackpadSwipeToDismissNode(onDrag, onRelease)

    override fun update(node: TrackpadSwipeToDismissNode) {
        node.onDrag = onDrag
        node.onRelease = onRelease
    }
}

private class TrackpadSwipeToDismissNode(
    var onDrag: (Float) -> Unit,
    var onRelease: (Float, Float) -> Unit,
) : Modifier.Node(),
    PointerInputModifierNode {
    private enum class Phase { Idle, Undecided, Dragging, Ignored }

    private var phase = Phase.Idle
    private var accumulated = Offset.Zero
    private var offset = 0f
    private var lastTime = 0L
    private val velocityTracker = VelocityTracker1D(isDataDifferential = true)

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        val type = pointerEvent.type
        if (type != PointerEventType.PanStart && type != PointerEventType.PanMove && type != PointerEventType.PanEnd) return
        if (type == PointerEventType.PanStart && pass == PointerEventPass.Initial) {
            if (phase == Phase.Dragging) release(0f)
            phase = Phase.Undecided
            accumulated = Offset.Zero
        }
        when {
            phase == Phase.Dragging && pass == PointerEventPass.Initial -> drag(pointerEvent)
            phase == Phase.Undecided && pass == PointerEventPass.Final -> decide(pointerEvent)
        }
        if (type == PointerEventType.PanEnd && pass == PointerEventPass.Final && phase != Phase.Dragging) phase = Phase.Idle
    }

    private fun decide(pointerEvent: PointerEvent) {
        if (pointerEvent.changes.any { it.isConsumed }) {
            phase = Phase.Ignored
            return
        }
        accumulated += pointerEvent.panDelta()
        if (accumulated.getDistance() < with(requireDensity()) { 8.dp.toPx() }) return
        if (accumulated.y > abs(accumulated.x)) {
            phase = Phase.Dragging
            offset = 0f
            velocityTracker.resetTracking()
            lastTime = pointerEvent.changes.first().uptimeMillis
            pointerEvent.changes.fastForEach { it.consume() }
            if (pointerEvent.type == PointerEventType.PanEnd) release(0f)
        } else {
            phase = Phase.Ignored
        }
    }

    private fun drag(pointerEvent: PointerEvent) {
        pointerEvent.changes.fastForEach { it.consume() }
        val delta = pointerEvent.panDelta().y
        val time = pointerEvent.changes.first().uptimeMillis
        if (time > lastTime) {
            velocityTracker.addDataPoint(time, delta)
            lastTime = time
        }
        offset += delta
        onDrag(offset)
        if (pointerEvent.type == PointerEventType.PanEnd) release(velocityTracker.calculateVelocity())
    }

    private fun release(velocity: Float) {
        phase = Phase.Idle
        onRelease(offset, velocity)
        offset = 0f
    }

    override fun onCancelPointerInput() {
        if (phase == Phase.Dragging) release(0f) else phase = Phase.Idle
    }

    private fun PointerEvent.panDelta(): Offset {
        val change = changes.first()
        var delta = -change.panOffset
        change.historical.fastForEach { delta -= it.panOffset }
        return delta
    }
}
