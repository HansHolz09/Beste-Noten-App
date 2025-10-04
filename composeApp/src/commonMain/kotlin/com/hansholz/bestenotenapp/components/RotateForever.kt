package com.hansholz.bestenotenapp.components

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.rotateForever(
    durationMillis: Int = 3000,
    clockwise: Boolean = true,
    enabled: Boolean = true,
    easing: Easing = LinearEasing,
): Modifier =
    composed {
        if (enabled) {
            val infiniteTransition = rememberInfiniteTransition(label = "RotateForeverTransition")

            val angle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = if (clockwise) 360f else -360f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis, easing = easing),
                        repeatMode = RepeatMode.Restart,
                    ),
                label = "RotationAngle",
            )

            this.graphicsLayer {
                rotationZ = angle
            }
        } else {
            this
        }
    }
