package com.hansholz.bestenotenapp.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

@Composable
internal actual fun SystemAppearance(
    isDark: Boolean,
    customColorScheme: @Composable (ColorScheme?) -> Unit,
) {
    LaunchedEffect(isDark) {
        UIApplication.sharedApplication.setStatusBarStyle(
            if (!isDark) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent,
        )
    }
}