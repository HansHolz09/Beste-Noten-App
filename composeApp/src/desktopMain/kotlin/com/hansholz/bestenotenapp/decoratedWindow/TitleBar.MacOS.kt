package com.hansholz.bestenotenapp.decoratedWindow

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jetbrains.JBR

@Composable
internal fun DecoratedWindowScope.TitleBarOnMacOs(
    modifier: Modifier = Modifier,
    titleBarHeight: MutableState<Dp>,
) {
    val titleBar = remember { JBR.getWindowDecorations().createCustomTitleBar() }
    CustomTitleBarObject.customTitleBar = titleBar

    TitleBarImpl(
        modifier = modifier,
        applyTitleBar = { height, state ->
            titleBar?.height = height.value
            JBR.getWindowDecorations().setCustomTitleBar(window, titleBar)

            if (state.isFullscreen) {
                PaddingValues(start = 80.dp)
            } else {
                PaddingValues(start = titleBar?.leftInset?.dp ?: 0.dp, end = titleBar?.rightInset?.dp ?: 0.dp)
            }
        },
        titleBarHeight = titleBarHeight,
    )
}
