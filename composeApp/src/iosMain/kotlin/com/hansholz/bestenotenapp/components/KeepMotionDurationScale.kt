@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.platform.MotionDurationScaleImpl

/**
 * Compose iOS sets the window-wide [MotionDurationScale] to 0 while "Reduce Motion" is enabled,
 * which makes every Compose animation (including the overscroll bounce and simple fades) jump to its end.
 * The app already maps "Reduce Motion" to its own "animations disabled" state, so keep the scale at 1.
 */
@Composable
internal fun KeepMotionDurationScale() {
    LaunchedEffect(Unit) {
        val scale = coroutineContext[MotionDurationScale] as? MotionDurationScaleImpl ?: return@LaunchedEffect
        snapshotFlow { scale.scaleFactor }.collect { if (it == 0f) scale.scaleFactor = 1f }
    }
}
