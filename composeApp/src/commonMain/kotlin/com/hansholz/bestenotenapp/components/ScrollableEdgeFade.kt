package com.hansholz.bestenotenapp.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.scrollableEdgeFade(
    canScrollBackward: Boolean,
    canScrollForward: Boolean,
    orientation: Orientation,
    edgeSize: Dp = 20.dp,
): Modifier {
    val backwardFade by animateFloatAsState(if (canScrollBackward) 1f else 0f)
    val forwardFade by animateFloatAsState(if (canScrollForward) 1f else 0f)

    return graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val availableSize = if (orientation == Orientation.Horizontal) size.width else size.height
            if (availableSize <= 0f) return@drawWithContent
            val edgeFraction = (edgeSize.toPx() / availableSize).coerceAtMost(0.25f)
            val colorStops =
                arrayOf(
                    0f to Color.White.copy(alpha = 1f - backwardFade),
                    edgeFraction to Color.White,
                    (1f - edgeFraction) to Color.White,
                    1f to Color.White.copy(alpha = 1f - forwardFade),
                )
            drawRect(
                brush =
                    if (orientation == Orientation.Horizontal) {
                        Brush.horizontalGradient(colorStops = colorStops)
                    } else {
                        Brush.verticalGradient(colorStops = colorStops)
                    },
                blendMode = BlendMode.DstIn,
            )
        }
}
