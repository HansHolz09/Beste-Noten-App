package com.hansholz.bestenotenapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.uikit.LocalUIViewController
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.getExactPlatform
import com.hansholz.bestenotenapp.main.getPlatformVersion
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@Composable
fun isInWindowMode(): Boolean {
    val viewController = LocalUIViewController.current
    val windowInfo = LocalWindowInfo.current
    return remember(windowInfo.containerSize) {
        val isInWindowMode = viewController.view.window?.frame != UIScreen.mainScreen.bounds
        // Switch to official Implementation if possibel
        isInWindowMode && getExactPlatform() == ExactPlatform.IPADOS && (getPlatformVersion()?.substringBefore('.')?.toIntOrNull() ?: 0) >= 26
    }
}
