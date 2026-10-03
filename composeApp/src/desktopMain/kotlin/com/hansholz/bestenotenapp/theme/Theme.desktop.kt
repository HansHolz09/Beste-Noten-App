package com.hansholz.bestenotenapp.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.materialkolor.Contrast
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import dev.nucleusframework.systemcolor.isSystemInHighContrast
import dev.nucleusframework.systemcolor.systemAccentColor

@Composable
internal actual fun SystemAppearance(
    isDark: Boolean,
    customColorScheme: @Composable (ColorScheme?) -> Unit,
) {
    val color = systemAccentColor()
    val highContrast = isSystemInHighContrast()
    customColorScheme(
        if (color != null) {
            rememberDynamicColorScheme(
                seedColor = color,
                isDark = isDark,
                isAmoled = false,
                contrastLevel = if (highContrast) Contrast.High.value else Contrast.Default.value,
                specVersion = ColorSpec.SpecVersion.SPEC_2025,
            )
        } else {
            null
        },
    )
}
