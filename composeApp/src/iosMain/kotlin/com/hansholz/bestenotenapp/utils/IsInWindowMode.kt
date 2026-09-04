package com.hansholz.bestenotenapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.uikit.LocalUIViewController
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.getExactPlatform
import com.hansholz.bestenotenapp.main.getPlatformVersion
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen
import kotlin.math.abs

@OptIn(ExperimentalForeignApi::class)
@Composable
fun isInWindowMode(): Boolean {
    val viewController = LocalUIViewController.current
    val windowInfo = LocalWindowInfo.current
    return remember(windowInfo.containerSize) {
        val window = viewController.view.window
        val windowSize = window?.frame?.useContents { size.width to size.height }
        val screenSize = (window?.screen ?: UIScreen.mainScreen).bounds.useContents { size.width to size.height }
        val isInWindowMode =
            windowSize != null &&
                (abs(windowSize.first - screenSize.first) > 1.0 || abs(windowSize.second - screenSize.second) > 1.0)
        isInWindowMode && getExactPlatform() == ExactPlatform.IPADOS && (getPlatformVersion()?.substringBefore('.')?.toIntOrNull() ?: 0) >= 26
    }
}
