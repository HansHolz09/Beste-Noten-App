package com.hansholz.bestenotenapp.theme

import android.app.Activity
import android.os.Build
import android.os.Build.VERSION_CODES
import android.view.Window
import android.view.WindowInsetsController.APPEARANCE_LIGHT_CAPTION_BARS
import android.view.WindowInsetsController.APPEARANCE_TRANSPARENT_CAPTION_BAR_BACKGROUND
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

@Composable
internal actual fun SystemAppearance(
    isDark: Boolean,
    customColorScheme: @Composable (ColorScheme?) -> Unit,
) {
    val view = LocalView.current
    val context = LocalContext.current
    LaunchedEffect(isDark) {
        val window = (view.context as Activity).window

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }

        if (window is Window && Build.VERSION.SDK_INT >= VERSION_CODES.VANILLA_ICE_CREAM) {
            window.insetsController?.setSystemBarsAppearance(
                APPEARANCE_TRANSPARENT_CAPTION_BAR_BACKGROUND,
                APPEARANCE_TRANSPARENT_CAPTION_BAR_BACKGROUND
            )
            val lightIcons = if (isDark) 0 else APPEARANCE_LIGHT_CAPTION_BARS
            window.insetsController?.setSystemBarsAppearance(
                lightIcons,
                APPEARANCE_LIGHT_CAPTION_BARS
            )
        }
    }
    if (Build.VERSION.SDK_INT >= VERSION_CODES.S) {
        customColorScheme(
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context),
        )
    }
}
