package com.hansholz.bestenotenapp.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.decoratedWindow.LocalDecoratedWindowScope
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.getExactPlatform

@Composable
actual fun Modifier.topAppBarPadding(sideMenuExpanded: Boolean): Modifier {
    val startPadding = animateDpAsState(if (getExactPlatform() == ExactPlatform.MACOS && !sideMenuExpanded && !LocalDecoratedWindowScope.current.state.isFullscreen) 90.dp else 0.dp, tween(400))
    val endPadding = remember { if (getExactPlatform() == ExactPlatform.WINDOWS || getExactPlatform() == ExactPlatform.LINUX) 80.dp else 0.dp }
    return this.padding(start = startPadding.value, end = endPadding)
}