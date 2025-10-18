package com.hansholz.bestenotenapp.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import dev.zwander.compose.rememberThemeInfo

@Composable
internal actual fun SystemAppearance(
    isDark: Boolean,
    customColorScheme: @Composable (ColorScheme?) -> Unit,
) = customColorScheme(rememberThemeInfo(isDark).colors)
