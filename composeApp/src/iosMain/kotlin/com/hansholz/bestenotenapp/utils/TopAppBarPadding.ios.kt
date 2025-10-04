@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
actual fun topAppBarStartPadding(sideMenuExpanded: Boolean): Dp {
    val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val startPadding = animateDpAsState(if ((!sideMenuExpanded || windowWidthSizeClass == WindowWidthSizeClass.COMPACT) && isInWindowMode()) 70.dp else 0.dp)
    return startPadding.value
}

@Composable
actual fun topAppBarEndPadding(): Dp = 0.dp
