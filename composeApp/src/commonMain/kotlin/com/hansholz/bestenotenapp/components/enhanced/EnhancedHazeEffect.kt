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
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeSampling
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeBlurStyleScope
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

@OptIn(ExperimentalHazeApi::class)
@Composable
fun Modifier.enhancedHazeEffect(
    hazeState: HazeState? = null,
    color: Color? = null,
    blurRadius: Dp? = null,
    fallbackAlpha: Float = 1f,
    block: (HazeBlurStyleScope.() -> Unit)? = null,
): Modifier {
    val blurEnabled = LocalBlurEnabled.current.value
    return when {
        hazeState != null && blurEnabled -> {
            this.hazeBlur(
                input = HazeInput.Sources(hazeState),
                style =
                    HazeBlurStyle {
                        blurRadius(blurRadius ?: 20.dp)
                        color?.let {
                            backgroundColor(it)
                            fallbackColorEffect(HazeColorEffect.tint(it.copy(fallbackAlpha), HazeColorEffect.DefaultBlendMode))
                        }
                        noiseFactor(0f)
                        block?.invoke(this)
                    },
                sampling = HazeSampling.Adaptive,
            )
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
