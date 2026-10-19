package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.uikit.LocalUIView
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIColor
import platform.UIKit.UIColorPickerViewController
import platform.UIKit.UIColorPickerViewControllerDelegateProtocol
import platform.UIKit.UIModalPresentationPageSheet
import platform.UIKit.UIModalPresentationPopover
import platform.UIKit.UIPopoverPresentationControllerDelegateProtocol
import platform.UIKit.UIPresentationController
import platform.UIKit.UIUserInterfaceSizeClassCompact
import platform.UIKit.popoverPresentationController
import platform.UIKit.presentationController
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun NativeColorPicker(
    color: Color,
    anchorBounds: Rect,
    onColorChanged: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    val view = LocalUIView.current
    val controller = LocalUIViewController.current
    val density = LocalDensity.current.density
    val target = remember { ColorPickerTarget(onColorChanged, onDismiss) }
    target.onColorChanged = onColorChanged
    target.onDismiss = onDismiss
    val picker = remember { UIColorPickerViewController() }
    val anchorRect =
        view.convertRect(
            CGRectMake(
                (anchorBounds.left / density).toDouble(),
                (anchorBounds.top / density).toDouble(),
                (anchorBounds.width / density).toDouble(),
                (anchorBounds.height / density).toDouble(),
            ),
            fromView = view.window,
        )

    DisposableEffect(controller, picker) {
        picker.selectedColor = UIColor(red = color.red.toDouble(), green = color.green.toDouble(), blue = color.blue.toDouble(), alpha = 1.0)
        picker.supportsAlpha = false
        picker.delegate = target
        picker.modalPresentationStyle =
            if (controller.traitCollection.horizontalSizeClass == UIUserInterfaceSizeClassCompact) {
                UIModalPresentationPageSheet
            } else {
                UIModalPresentationPopover
            }
        picker.popoverPresentationController?.apply {
            sourceView = view
            sourceRect = anchorRect
            delegate = target
        }
        picker.presentationController?.delegate = target
        controller.presentViewController(picker, animated = true, completion = null)
        onDispose {
            picker.delegate = null
            picker.presentationController?.delegate = null
            picker.dismissViewControllerAnimated(true, null)
        }
    }
    SideEffect {
        picker.popoverPresentationController?.sourceRect = anchorRect
    }
}

@OptIn(ExperimentalForeignApi::class)
private class ColorPickerTarget(
    var onColorChanged: (Color) -> Unit,
    var onDismiss: () -> Unit,
) : NSObject(),
    UIColorPickerViewControllerDelegateProtocol,
    UIPopoverPresentationControllerDelegateProtocol {
    override fun colorPickerViewControllerDidSelectColor(viewController: UIColorPickerViewController) {
        memScoped {
            val red = alloc<DoubleVar>()
            val green = alloc<DoubleVar>()
            val blue = alloc<DoubleVar>()
            val alpha = alloc<DoubleVar>()
            if (viewController.selectedColor.getRed(red.ptr, green.ptr, blue.ptr, alpha.ptr)) {
                onColorChanged(Color(red.value.toFloat(), green.value.toFloat(), blue.value.toFloat(), 1f))
            }
        }
    }

    override fun colorPickerViewControllerDidFinish(viewController: UIColorPickerViewController) {
        onDismiss()
    }

    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        onDismiss()
    }
}
