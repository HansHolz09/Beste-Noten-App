package com.hansholz.bestenotenapp.theme

import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.animateColorScheme
import com.materialkolor.rememberDynamicColorScheme
import com.russhwolf.settings.Settings
import dev.chrisbanes.haze.HazeDefaults
import io.github.kdroidfilter.platformtools.darkmodedetector.isSystemInDarkMode

internal val LocalUseSystemIsDark  = compositionLocalOf { mutableStateOf(true) }
internal val LocalIsDark = compositionLocalOf { mutableStateOf(true) }
internal val LocalThemeIsDark = compositionLocalOf { true }
internal val LocalUseCustomColorScheme = compositionLocalOf { mutableStateOf(false) }
internal val LocalSupportsCustomColorScheme = compositionLocalOf { mutableStateOf(false) }
internal val LocalAnimationsEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalBlurEnabled = compositionLocalOf { mutableStateOf(false) }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AppTheme(content: @Composable () -> Unit) {
    val settings = Settings()

    val useSystemIsDark = remember { mutableStateOf(settings.getBoolean("useSystemIsDark", true)) }
    val isDark = remember { mutableStateOf(settings.getBoolean("isDark", false)) }
    val isDarkState = if (useSystemIsDark.value) isSystemInDarkMode() else isDark.value
    val useCustomColorSchemeState = remember { mutableStateOf(settings.getBoolean("useCustomColorScheme", false)) }
    val supportsCustomColorSchemeState = remember { mutableStateOf(false) }
    val animationsEnabledState = remember { mutableStateOf(settings.getBoolean("animationsEnabled", true)) }
    val blurEnabledState = remember { mutableStateOf(settings.getBoolean("blurEnabled", HazeDefaults.blurEnabled())) }
    CompositionLocalProvider(
        LocalUseSystemIsDark provides useSystemIsDark,
        LocalIsDark provides isDark,
        LocalThemeIsDark provides isDarkState,
        LocalUseCustomColorScheme provides useCustomColorSchemeState,
        LocalSupportsCustomColorScheme provides supportsCustomColorSchemeState,
        LocalAnimationsEnabled provides animationsEnabledState,
        LocalBlurEnabled provides blurEnabledState
    ) {
        val isDark = isDarkState
        val useCustomColorScheme by useCustomColorSchemeState
        var customColorScheme: ColorScheme? = null
        SystemAppearance(isDark) {
            customColorScheme = it
            if (it != null) LocalSupportsCustomColorScheme.current.value = true
        }
        val colorScheme = (if (useCustomColorScheme) customColorScheme else null) ?: rememberDynamicColorScheme(
            seedColor = Color.Yellow,
            isDark = isDark,
            specVersion = ColorSpec.SpecVersion.SPEC_2025,
        )
        MaterialExpressiveTheme(
            colorScheme = animateColorScheme(colorScheme, { tween(750) }),
            typography = AppTypography(),
            content = { Surface(content = content) },
        )
    }
}

@Composable
internal expect fun SystemAppearance(
    isDark: Boolean,
    customColorScheme: @Composable (ColorScheme?) -> Unit = {},
)
