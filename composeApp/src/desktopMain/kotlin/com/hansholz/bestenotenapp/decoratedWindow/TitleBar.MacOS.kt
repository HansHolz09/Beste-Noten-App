package com.hansholz.bestenotenapp.decoratedWindow

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.decoratedWindow.utils.macos.MacUtil
import com.jetbrains.JBR

private class NewFullscreenControlsElement(
    val newControls: Boolean,
    val inspectorInfo: InspectorInfo.() -> Unit,
) : ModifierNodeElement<NewFullscreenControlsNode>() {
    override fun create(): NewFullscreenControlsNode = NewFullscreenControlsNode(newControls)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? NewFullscreenControlsElement ?: return false
        return newControls == otherModifier.newControls
    }

    override fun hashCode(): Int = newControls.hashCode()

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }

    override fun update(node: NewFullscreenControlsNode) {
        node.newControls = newControls
    }
}

private class NewFullscreenControlsNode(
    var newControls: Boolean,
) : Modifier.Node()

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
            if (state.isFullscreen) {
                MacUtil.updateFullScreenButtons(window)
            }
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
