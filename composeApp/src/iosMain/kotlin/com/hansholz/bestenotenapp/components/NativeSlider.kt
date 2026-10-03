package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UIEvent
import platform.UIKit.UISlider
import platform.UIKit.UISliderTrackConfiguration
import platform.UIKit.UITouch
import platform.UIKit.UITouchTypePencil
import platform.darwin.NSObject
import kotlin.math.roundToInt

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NativeSlider(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    enabled: Boolean,
    modifier: Modifier,
) {
    val target = remember { SliderTarget(onSelected) }
    target.onSelected = onSelected
    UIKitView(
        factory = {
            PencilAwareSlider()
                .apply {
                    minimumValue = 0f
                    maximumValue = 6f
                    if (NSProcessInfo.processInfo.operatingSystemVersion.useContents { majorVersion >= 26 }) {
                        trackConfiguration = UISliderTrackConfiguration.configurationWithNumberOfTicks(7)
                    }
                    addTarget(target, NSSelectorFromString("valueChanged:"), UIControlEventValueChanged)
                }
        },
        modifier = modifier.fillMaxWidth().height(44.dp),
        update = {
            if (it.value.roundToInt() != selectedIndex) it.setValue(selectedIndex.toFloat(), animated = false)
            it.currentIndex = selectedIndex
            it.enabled = enabled
        },
        properties =
            UIKitInteropProperties(
                interactionMode = UIKitInteropInteractionMode.NonCooperative,
                isNativeAccessibilityEnabled = true,
                placedAsOverlay = true,
            ),
    )
}

@OptIn(ExperimentalForeignApi::class)
private class PencilAwareSlider : UISlider(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    private var pencilTouch: UITouch? = null
    private val feedback = NativeAlignmentHaptics(this)
    var currentIndex = 0

    override fun touchesBegan(
        touches: Set<*>,
        withEvent: UIEvent?,
    ) {
        pencilTouch = touches.firstOrNull { (it as? UITouch)?.type == UITouchTypePencil } as? UITouch
        if (pencilTouch != null) feedback.prepare()
        super.touchesBegan(touches, withEvent)
    }

    override fun beginTrackingWithTouch(
        touch: UITouch,
        withEvent: UIEvent?,
    ): Boolean {
        if (touch.type == UITouchTypePencil) {
            pencilTouch = touch
            feedback.prepare()
        }
        return super.beginTrackingWithTouch(touch, withEvent)
    }

    override fun touchesEnded(
        touches: Set<*>,
        withEvent: UIEvent?,
    ) {
        super.touchesEnded(touches, withEvent)
        pencilTouch = null
    }

    override fun touchesCancelled(
        touches: Set<*>,
        withEvent: UIEvent?,
    ) {
        super.touchesCancelled(touches, withEvent)
        pencilTouch = null
    }

    fun selectionChanged(index: Int): Boolean {
        if (index == currentIndex) return false
        currentIndex = index
        pencilTouch?.locationInView(this)?.let { feedback.alignmentOccurred(it) }
        return true
    }
}

@OptIn(BetaInteropApi::class)
private class SliderTarget(
    var onSelected: (Int) -> Unit,
) : NSObject() {
    @ObjCAction
    fun valueChanged(sender: PencilAwareSlider) {
        val index = sender.value.roundToInt().coerceIn(0, 6)
        sender.setValue(index.toFloat(), animated = false)
        if (sender.selectionChanged(index)) onSelected(index)
    }
}
