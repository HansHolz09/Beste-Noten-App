package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.uikit.LocalUIView
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGPointMake
import platform.Foundation.NSProcessInfo
import platform.UIKit.UICanvasFeedbackGenerator
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
internal class NativeAlignmentHaptics(
    view: UIView,
) {
    private val generator =
        if (NSProcessInfo.processInfo.operatingSystemVersion.useContents {
                majorVersion > 17 || (majorVersion == 17L && minorVersion >= 5)
            }
        ) {
            UICanvasFeedbackGenerator.feedbackGeneratorForView(view)
        } else {
            null
        }

    fun alignmentOccurred(location: CValue<CGPoint>) {
        generator?.alignmentOccurredAtLocation(location)
    }

    fun prepare() {
        generator?.prepare()
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun rememberNativeAlignmentHaptic(): (Offset) -> Unit {
    val view = LocalUIView.current
    val density = LocalDensity.current.density
    val feedback = remember(view) { NativeAlignmentHaptics(view) }
    return remember(feedback, density) {
        { position -> feedback.alignmentOccurred(CGPointMake((position.x / density).toDouble(), (position.y / density).toDouble())) }
    }
}
