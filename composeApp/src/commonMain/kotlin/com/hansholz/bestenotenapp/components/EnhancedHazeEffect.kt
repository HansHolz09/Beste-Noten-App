package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import dev.chrisbanes.haze.*

@OptIn(ExperimentalHazeApi::class)
@Composable
fun Modifier.enhancedHazeEffect(hazeState: HazeState? = null, color: Color? = null, blurRadius: Dp? = null, block: (HazeEffectScope.() -> Unit)? = null): Modifier {
    val blurEnabled = LocalBlurEnabled.current.value
    return this.hazeEffect(hazeState) {
        this.blurEnabled = blurEnabled
        this.blurRadius = blurRadius ?: 10.dp
        color?.let {
            backgroundColor = it
            fallbackTint = HazeTint(it)
        }
        inputScale = HazeInputScale.Auto
        noiseFactor = 0f
        block?.invoke(this)
    }
}