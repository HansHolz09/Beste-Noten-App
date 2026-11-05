package com.hansholz.bestenotenapp.theme

import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hansholz.bestenotenapp.main.AppHazeState
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.security.kSafeProviderCompose
import com.materialkolor.Contrast
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.animateColorScheme
import com.materialkolor.rememberDynamicColorScheme
import dev.chrisbanes.haze.blur.HazeBlurDefaults
import dev.chrisbanes.haze.glass.GlassAccessibilitySettings
import dev.chrisbanes.haze.glass.LocalGlassAccessibilitySettings
import dev.chrisbanes.haze.hazeSource
import io.github.kdroidfilter.platformtools.darkmodedetector.isSystemInDarkMode

internal val LocalUseSystemIsDark = compositionLocalOf { mutableStateOf(true) }
internal val LocalIsDark = compositionLocalOf { mutableStateOf(true) }
internal val LocalThemeIsDark = compositionLocalOf { true }
internal val LocalUseCustomColorScheme = compositionLocalOf { mutableStateOf(false) }
internal val LocalSupportsCustomColorScheme = compositionLocalOf { mutableStateOf(false) }
internal val LocalAnimationsEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalBlurEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalNativeSystemIsDark = compositionLocalOf<Boolean?> { null }
internal val LocalSystemAccessibility = compositionLocalOf { SystemAccessibility() }

internal data class SystemAccessibility(
    val reduceMotion: Boolean = false,
    val increaseContrast: Boolean = false,
    val reduceTransparency: Boolean = false,
    val showBorders: Boolean = false,
)

@Composable
internal fun AppTheme(
    finalTheme: (ColorScheme) -> Unit = {},
    content: @Composable () -> Unit,
) = kSafeProviderCompose {
    val useSystemIsDark = remember { mutableStateOf(get("useSystemIsDark", true)) }
    val isDark = remember { mutableStateOf(get("isDark", false)) }
    val detectedSystemIsDark = isSystemInDarkMode()
    val nativeSystemIsDark = LocalNativeSystemIsDark.current
    val isDarkState =
        if (useSystemIsDark.value) {
            nativeSystemIsDark ?: detectedSystemIsDark
        } else {
            isDark.value
        }
    val useCustomColorSchemeState = remember { mutableStateOf(get("useCustomColorScheme", false)) }
    val supportsCustomColorSchemeState = remember { mutableStateOf(false) }
    val animationsEnabledState = remember { mutableStateOf(get("animationsEnabled", true)) }
    val blurEnabledState = remember { mutableStateOf(get("blurEnabled", HazeBlurDefaults.isBlurEnabledByDefault() && getPlatform() != Platform.WEB)) }
    val systemAccessibility = LocalSystemAccessibility.current
    val systemDisabledState = remember { mutableStateOf(false) }
    CompositionLocalProvider(
        LocalUseSystemIsDark provides useSystemIsDark,
        LocalIsDark provides isDark,
        LocalThemeIsDark provides isDarkState,
        LocalUseCustomColorScheme provides useCustomColorSchemeState,
        LocalSupportsCustomColorScheme provides supportsCustomColorSchemeState,
        LocalAnimationsEnabled provides if (systemAccessibility.reduceMotion) systemDisabledState else animationsEnabledState,
        LocalBlurEnabled provides if (systemAccessibility.reduceTransparency) systemDisabledState else blurEnabledState,
        LocalGlassAccessibilitySettings provides
            GlassAccessibilitySettings(
                reduceTransparency = systemAccessibility.reduceTransparency,
                increaseContrast = systemAccessibility.increaseContrast,
                showBorders = systemAccessibility.showBorders,
            ),
    ) {
        val isDark = isDarkState
        val useCustomColorScheme by useCustomColorSchemeState
        var customColorScheme: ColorScheme? = null
        SystemAppearance(isDark) {
            customColorScheme = it
            if (it != null) LocalSupportsCustomColorScheme.current.value = true
        }
        val colorScheme =
            (if (useCustomColorScheme) customColorScheme else null) ?: rememberDynamicColorScheme(
                seedColor = Color.Yellow,
                isDark = isDark,
                contrastLevel = if (systemAccessibility.increaseContrast) Contrast.High.value else Contrast.Default.value,
                specVersion = ColorSpec.SpecVersion.SPEC_2025,
            )
        LaunchedEffect(colorScheme) {
            finalTheme(colorScheme)
        }
        val animatedColorScheme =
            animateColorScheme(
                colorScheme,
                { tween(if (LocalAnimationsEnabled.current.value && !LocalNativeComponentsEnabled.current.value) 750 else 0) },
            )
        MaterialExpressiveTheme(
            colorScheme = animatedColorScheme,
            typography = AppTypography,
            content = {
                Surface(
                    modifier = Modifier.hazeSource(AppHazeState.current.value),
                    content = content,
                )
            },
        )
    }
}

@Composable
internal expect fun SystemAppearance(
    isDark: Boolean,
    customColorScheme: @Composable (ColorScheme?) -> Unit = {},
)
