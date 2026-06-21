package com.hansholz.bestenotenapp.components.enhanced

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
    return if (hazeState != null) {
        this.hazeEffect(hazeState) {
            blurEffect {
                this.blurEnabled = blurEnabled
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
    } else if (blurEnabled) {
        this.blur((blurRadius ?: 10.dp) * 2)
    } else {
        this
    }
}
