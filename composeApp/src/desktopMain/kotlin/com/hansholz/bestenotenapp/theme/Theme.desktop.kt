package com.hansholz.bestenotenapp.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import dev.zwander.compose.rememberThemeInfo

@Composable
internal actual fun SystemAppearance(
    isDark: Boolean,
    customColorScheme: @Composable (ColorScheme?) -> Unit,
) = customColorScheme(
    rememberDynamicColorScheme(
        seedColor = rememberThemeInfo(isDark).seedColor,
        isDark = isDark,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
    ),
)
