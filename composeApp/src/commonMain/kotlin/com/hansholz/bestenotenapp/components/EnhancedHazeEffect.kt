package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import dev.chrisbanes.haze.*

@Composable
fun Modifier.enhancedHazeEffect(hazeState: HazeState? = null, color: Color? = null, block: (HazeEffectScope.() -> Unit)? = null): Modifier {
    val blurEnabled = LocalBlurEnabled.current.value
    return this.hazeEffect(hazeState) {
        this.blurEnabled = blurEnabled
        color?.let {
            backgroundColor = it
            fallbackTint = HazeTint(it)
        }
        noiseFactor = 0f
        progressive = HazeProgressive.verticalGradient(startIntensity = 1f)
        block?.invoke(this)
    }
}