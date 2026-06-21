package com.hansholz.bestenotenapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.uikit.LocalUIViewController
import com.hansholz.bestenotenapp.main.getPlatformVersion
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import platform.UIKit.UIEdgeInsets
import platform.UIKit.UIEdgeInsetsMake
import platform.UIKit.UIEdgeInsetsZero
import platform.UIKit.UIViewController
import platform.UIKit.UIViewLayoutRegion
import platform.UIKit.additionalSafeAreaInsets

@Composable
fun LegacySafeArea(content: @Composable () -> Unit) {
    val controller = LocalUIViewController.current
    val containerSize = LocalWindowInfo.current.containerSize

    LaunchedEffect(controller, containerSize) {
        reapplyLegacySafeArea(controller)
    }

    content()
}

private data class RawInsets(val top: Double, val left: Double, val bottom: Double, val right: Double)

@OptIn(ExperimentalForeignApi::class)
private fun CValue<UIEdgeInsets>.toRaw(): RawInsets = useContents {
    RawInsets(top, left, bottom, right)
}

@OptIn(ExperimentalForeignApi::class)
private fun reapplyLegacySafeArea(controller: UIViewController) {
    if ((getPlatformVersion()?.substringBefore('.')?.toIntOrNull() ?: 0) >= 26) return

    val view = controller.view

    controller.additionalSafeAreaInsets = UIEdgeInsetsZero.readValue()
    view.layoutIfNeeded()

    val legacy = view.safeAreaInsets.toRaw()

    val region = UIViewLayoutRegion.safeAreaLayoutRegionWithCornerAdaptation(
        UIViewLayoutRegionAdaptivityAxisVertical
    )
    val current = view.edgeInsetsForLayoutRegion(region).toRaw()

    controller.additionalSafeAreaInsets = UIEdgeInsetsMake(
        legacy.top - current.top,
        legacy.left - current.left,
        legacy.bottom - current.bottom,
        legacy.right - current.right
    )
}
