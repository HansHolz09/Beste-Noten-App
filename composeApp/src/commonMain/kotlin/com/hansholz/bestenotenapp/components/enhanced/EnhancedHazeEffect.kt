package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeBlurStyleScope
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.glass.GlassOptics
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass

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
    val useLiquidGlass = LocalNativeComponentsEnabled.current.value
    return when {
        hazeState != null && blurEnabled -> {
            if (useLiquidGlass && block == null) {
                this.hazeGlass(
                    input = HazeInput.Sources(hazeState),
                    style =
                        GlassStyle {
                            color?.let {
                                backgroundColor(it)
                            }
                            optics(GlassOptics.Adaptive)
                            specularIntensity(0f)
                            shape(RoundedCornerShape(28.dp))
                        },
                    performanceMode = HazePerformanceMode.Balanced,
                )
            } else {
                this.hazeBlur(
                    input = HazeInput.Sources(hazeState),
                    style =
                        HazeBlurStyle {
                            blurRadius(blurRadius ?: 20.dp)
                            color?.let {
                                backgroundColor(it)
                                fallbackColorEffect(HazeColorEffect.tint(it.copy(fallbackAlpha)))
                            }
                            noiseFactor(0f)
                            block?.invoke(this)
                        },
                    performanceMode = HazePerformanceMode.Balanced,
                )
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
