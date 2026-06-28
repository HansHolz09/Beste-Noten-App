package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.BlurVisualEffect
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect

@OptIn(ExperimentalHazeApi::class)
@Composable
fun Modifier.enhancedHazeEffect(
    hazeState: HazeState? = null,
    color: Color? = null,
    blurRadius: Dp? = null,
    fallbackAlpha: Float = 1f,
    block: (BlurVisualEffect.() -> Unit)? = null,
): Modifier {
    val blurEnabled = LocalBlurEnabled.current.value
    return when {
        hazeState != null && blurEnabled -> {
            this.hazeEffect(hazeState) {
                blurEffect {
                    this.blurRadius = blurRadius ?: 20.dp
                    color?.let {
                        backgroundColor = it
                        fallbackTint = HazeColorEffect.tint(it.copy(fallbackAlpha), HazeColorEffect.DefaultBlendMode)
                    }
                    inputScale = HazeInputScale.Auto
                    noiseFactor = 0f
                    block?.invoke(this)
                }
            }
        }

        hazeState != null && color != null -> {
            this.background(color.copy(fallbackAlpha))
        }

        hazeState != null -> {
            this
        }

        blurEnabled && (blurRadius ?: 10.dp) > 0.dp -> {
            this.blur((blurRadius ?: 10.dp) * 2)
        }

        else -> {
            this
        }
    }
}
