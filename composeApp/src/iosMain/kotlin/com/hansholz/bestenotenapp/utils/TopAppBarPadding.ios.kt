package com.hansholz.bestenotenapp.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

@Composable
actual fun topAppBarStartPadding(sideMenuExpanded: Boolean): Dp {
    val isCompactWindow =
        !currentWindowAdaptiveInfoV2()
            .windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val startPadding = animateDpAsState(if ((!sideMenuExpanded || isCompactWindow) && isInWindowMode()) 70.dp else 0.dp)
    return startPadding.value
}

@Composable
actual fun topAppBarEndPadding(): Dp = 0.dp
