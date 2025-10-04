package com.hansholz.bestenotenapp.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.decoratedWindow.CustomTitleBarObject
import com.hansholz.bestenotenapp.decoratedWindow.LocalDecoratedWindowScope
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.getExactPlatform
import org.jetbrains.skiko.hostOs

@Composable
actual fun topAppBarStartPadding(sideMenuExpanded: Boolean): Dp {
    val startPadding =
        animateDpAsState(
            if (getExactPlatform() == ExactPlatform.MACOS && !sideMenuExpanded && !LocalDecoratedWindowScope.current.state.isFullscreen) {
                CustomTitleBarObject.customTitleBar
                    ?.leftInset
                    ?.dp
                    ?.minus(15.dp) ?: 90.dp
            } else {
                0.dp
            },
            tween(400),
        )
    return startPadding.value
}

@Composable
actual fun topAppBarEndPadding(): Dp =
    if (hostOs.isWindows) {
        CustomTitleBarObject.customTitleBar?.rightInset?.dp ?: 80.dp
    } else if (hostOs.isLinux) {
        80.dp
    } else {
        0.dp
    }
