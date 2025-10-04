package com.hansholz.bestenotenapp.decoratedWindow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectableValue
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.NoInspectorInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.getExactPlatform

@Composable
fun DecoratedWindowScope.TitleBar(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    titleBarHeight: MutableState<Dp>,
) {
    when (getExactPlatform()) {
        ExactPlatform.LINUX -> TitleBarOnLinux(modifier, isDark, titleBarHeight)
        ExactPlatform.WINDOWS -> TitleBarOnWindows(modifier, isDark, titleBarHeight)
        ExactPlatform.MACOS -> TitleBarOnMacOs(modifier, titleBarHeight)
        else -> error("TitleBar is not supported on this platform(${System.getProperty("os.name")})")
    }
}

@Composable
internal fun DecoratedWindowScope.TitleBarImpl(
    modifier: Modifier = Modifier,
    applyTitleBar: (Dp, DecoratedWindowState) -> PaddingValues,
    titleBarHeight: MutableState<Dp>,
    content: @Composable () -> Unit = {},
) {
    val density = LocalDensity.current

    Box(
        modifier =
            modifier
                .focusProperties { canFocus = false }
                .height(titleBarHeight.value)
                .onSizeChanged { with(density) { applyTitleBar(it.height.toDp(), state) } }
                .fillMaxWidth(),
    ) {
        content()
    }
}

private class TitleBarChildDataElement(
    val horizontalAlignment: Alignment.Horizontal,
    val inspectorInfo: InspectorInfo.() -> Unit = NoInspectorInfo,
) : ModifierNodeElement<TitleBarChildDataNode>(),
    InspectableValue {
    override fun create(): TitleBarChildDataNode = TitleBarChildDataNode(horizontalAlignment)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? TitleBarChildDataElement ?: return false
        return horizontalAlignment == otherModifier.horizontalAlignment
    }

    override fun hashCode(): Int = horizontalAlignment.hashCode()

    override fun update(node: TitleBarChildDataNode) {
        node.horizontalAlignment = horizontalAlignment
    }

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }
}

private class TitleBarChildDataNode(
    var horizontalAlignment: Alignment.Horizontal,
) : Modifier.Node(),
    ParentDataModifierNode {
    override fun Density.modifyParentData(parentData: Any?) = this@TitleBarChildDataNode
}
