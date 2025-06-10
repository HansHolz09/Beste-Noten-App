package com.hansholz.bestenotenapp.theme

import android.app.Activity
import android.os.Build
import android.os.Build.VERSION_CODES
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
            isAppearanceLightStatusBars = isDark
            isAppearanceLightNavigationBars = isDark
        }
    }
    if (Build.VERSION.SDK_INT >= VERSION_CODES.S) {
        customColorScheme(
            if (isDark) dynamicLightColorScheme(context) else dynamicDarkColorScheme(context),
        )
    }
}
