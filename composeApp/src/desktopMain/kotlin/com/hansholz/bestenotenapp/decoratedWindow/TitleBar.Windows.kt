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
internal fun DecoratedWindowScope.TitleBarOnWindows(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    titleBarHeight: MutableState<Dp>,
) {
    val titleBar = remember { JBR.getWindowDecorations().createCustomTitleBar() }
    CustomTitleBarObject.customTitleBar = titleBar

    TitleBarImpl(
        modifier = modifier,
        applyTitleBar = { height, _ ->
            titleBar.height = height.value
            titleBar.putProperty("controls.dark", isDark)
            JBR.getWindowDecorations().setCustomTitleBar(window, titleBar)
            PaddingValues(start = titleBar.leftInset.dp, end = titleBar.rightInset.dp)
        },
        titleBarHeight = titleBarHeight,
    )
}
