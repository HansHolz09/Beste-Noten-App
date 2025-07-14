package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@OptIn(ExperimentalHazeApi::class)
@Composable
fun Modifier.enhancedHazeEffect(hazeState: HazeState? = null, color: Color? = null, blurRadius: Dp? = null, block: (HazeEffectScope.() -> Unit)? = null): Modifier {
    val blurEnabled = LocalBlurEnabled.current.value
    val blurScale = if (getPlatform() == Platform.ANDROID) 3 else 1
    return this.hazeEffect(hazeState) {
        this.blurEnabled = blurEnabled
        this.blurRadius = (blurRadius ?: 10.dp) * blurScale
        color?.let {
            backgroundColor = it
            fallbackTint = HazeTint(it)
        }
        inputScale = HazeInputScale.Auto
        noiseFactor = 0f
        block?.invoke(this)
    }
}