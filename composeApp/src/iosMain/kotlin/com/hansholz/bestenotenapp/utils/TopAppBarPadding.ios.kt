package com.hansholz.bestenotenapp.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.cinterop.ExperimentalForeignApi

@Suppress("DEPRECATION")
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Modifier.topAppBarPadding(sideMenuExpanded: Boolean): Modifier {
    val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val startPadding = animateDpAsState(if ((!sideMenuExpanded || windowWidthSizeClass == WindowWidthSizeClass.COMPACT) && isInWindowMode()) 70.dp else 0.dp)
    return this.padding(start = startPadding.value)
}