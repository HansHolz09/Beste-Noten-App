package com.hansholz.bestenotenapp.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.getExactPlatform
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import dev.nucleusframework.window.LocalWindowChromeInsets

@Composable
actual fun topAppBarStartPadding(sideMenuExpanded: Boolean): Dp {
    val animationsEnabled by LocalAnimationsEnabled.current
    val startPadding =
        if (getExactPlatform() == ExactPlatform.MACOS && !sideMenuExpanded) {
            LocalWindowChromeInsets.current.controlsInsets.calculateStartPadding(LocalLayoutDirection.current)
        } else {
            0.dp
        }
    return if (animationsEnabled) animateDpAsState(startPadding, tween(400)).value else startPadding
}

@Composable
actual fun topAppBarEndPadding(): Dp = LocalWindowChromeInsets.current.controlsInsets.calculateEndPadding(LocalLayoutDirection.current)
