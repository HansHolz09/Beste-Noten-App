package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSProcessInfo
import platform.UIKit.UIBlurEffect
import platform.UIKit.UIBlurEffectStyle
import platform.UIKit.UIColor
import platform.UIKit.UIGlassEffect
import platform.UIKit.UIView
import platform.UIKit.UIVisualEffectView

private const val DIALOG_CORNER_RADIUS = 28.0

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
private class NativeDialogBackdropView : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    private val glassViews = mutableListOf<UIVisualEffectView>()

    init {
        backgroundColor = UIColor.clearColor
        opaque = false
        userInteractionEnabled = false
    }

    private fun createGlassView() =
        UIVisualEffectView(
            effect =
                if (NSProcessInfo.processInfo.operatingSystemVersion.useContents { majorVersion >= 26 }) {
                    UIGlassEffect()
                } else {
                    UIBlurEffect.effectWithStyle(UIBlurEffectStyle.UIBlurEffectStyleSystemMaterial)
                },
        ).apply {
            backgroundColor = UIColor.clearColor
            opaque = false
            clipsToBounds = true
            layer.cornerRadius = DIALOG_CORNER_RADIUS
            userInteractionEnabled = false
        }

    fun updateGlassFrames(glassFrames: List<Rect>) {
        while (glassViews.size < glassFrames.size) {
            createGlassView().also {
                glassViews.add(it)
                addSubview(it)
            }
        }
        while (glassViews.size > glassFrames.size) {
            glassViews.removeLast().removeFromSuperview()
        }
        glassFrames.forEachIndexed { index, glassFrame ->
            glassViews[index].setFrame(
                CGRectMake(
                    glassFrame.left.toDouble(),
                    glassFrame.top.toDouble(),
                    glassFrame.width.toDouble(),
                    glassFrame.height.toDouble(),
                ),
            )
        }
    }
}

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NativeDialogBackdrop(
    modifier: Modifier,
    glassFrames: List<Rect>,
) {
    UIKitView(
        factory = { NativeDialogBackdropView() },
        update = { it.updateGlassFrames(glassFrames) },
        modifier = modifier,
        properties =
            UIKitInteropProperties(
                interactionMode = null,
                placedAsOverlay = false,
            ),
    )
}
