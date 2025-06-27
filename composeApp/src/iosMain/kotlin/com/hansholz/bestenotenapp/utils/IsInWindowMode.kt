package com.hansholz.bestenotenapp.utils

import androidx.compose.runtime.Composable
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
    val isInWindowMode = viewController.view.window?.frame != UIScreen.mainScreen.bounds
    // Switch to official Implementation if possibel
    return isInWindowMode && getExactPlatform() == ExactPlatform.IPADOS && (getPlatformVersion()?.toFloatOrNull() ?: 0f) >= 26f
}