@file:OptIn(ExperimentalForeignApi::class)

package com.hansholz.bestenotenapp.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.interpretObjCPointer
import kotlinx.cinterop.plus
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.useContents
import kotlinx.cinterop.value
import platform.UIKit.UIDevice
import platform.UIKit.UIEdgeInsets
import platform.UIKit.UIEdgeInsetsMake
import platform.UIKit.UIView
import platform.objc.class_getInstanceMethod
import platform.objc.class_getInstanceVariable
import platform.objc.ivar_getOffset
import platform.objc.method_setImplementation
import platform.objc.objc_getClass
import platform.objc.sel_registerName

private var installed = false
private var kindOffset = -1L

private const val KIND_MARGINS = 0L
private const val KIND_READABLE_CONTENT = 1L
private const val KIND_SAFE_AREA = 2L

@OptIn(BetaInteropApi::class)
fun installLegacyInsetsPatch() {
    if (installed) return

    val majorIosVersion =
        UIDevice.currentDevice.systemVersion
            .substringBefore(".")
            .toIntOrNull()

    if (majorIosVersion == null || majorIosVersion < 26) {
        installed = true
        return
    }

    val cls =
        objc_getClass("CMPLayoutRegion") as? ObjCClass
            ?: return

    val method =
        class_getInstanceMethod(
            cls,
            sel_registerName("edgeInsetsInView:"),
        ) ?: return

    val kindIvar =
        class_getInstanceVariable(cls, "_kind")
            ?: return

    kindOffset = ivar_getOffset(kindIvar)

    val replacement =
        staticCFunction(::legacyEdgeInsetsInView)
            .reinterpret<CFunction<() -> Unit>>()

    method_setImplementation(method, replacement)

    installed = true
}

@Suppress("UNUSED_PARAMETER")
private fun legacyEdgeInsetsInView(
    self: COpaquePointer?,
    _cmd: COpaquePointer?,
    viewPtr: COpaquePointer?,
): CValue<UIEdgeInsets> {
    val view =
        viewPtr
            ?.let { interpretObjCPointer<UIView>(it.rawValue) }
            ?: return UIEdgeInsetsMake(0.0, 0.0, 0.0, 0.0)

    return when (readKind(self)) {
        KIND_MARGINS -> view.layoutMargins
        KIND_READABLE_CONTENT -> view.readableContentInsets()
        KIND_SAFE_AREA -> view.safeAreaInsets
        else -> view.safeAreaInsets
    }
}

private fun readKind(self: COpaquePointer?): Long {
    if (self == null || kindOffset < 0) return KIND_SAFE_AREA

    val kindPtr =
        self
            .reinterpret<ByteVar>()
            .plus(kindOffset)!!
            .reinterpret<LongVar>()

    return kindPtr.pointed.value
}

private fun UIView.readableContentInsets(): CValue<UIEdgeInsets> {
    val layoutFrame = readableContentGuide.layoutFrame

    val viewWidth = frame.useContents { size.width }
    val viewHeight = frame.useContents { size.height }

    val top = layoutFrame.useContents { origin.y }
    val left = layoutFrame.useContents { origin.x }
    val bottom = layoutFrame.useContents { viewHeight - (origin.y + size.height) }
    val right = layoutFrame.useContents { viewWidth - (origin.x + size.width) }

    return UIEdgeInsetsMake(top, left, bottom, right)
}
