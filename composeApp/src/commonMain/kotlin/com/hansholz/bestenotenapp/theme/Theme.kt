package com.hansholz.bestenotenapp.theme

import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.materialkolor.ktx.animateColorScheme
import com.russhwolf.settings.Settings
import io.github.kdroidfilter.platformtools.darkmodedetector.isSystemInDarkMode

private val LightColorScheme =
    lightColorScheme(
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        primaryContainer = primaryContainerLight,
        onPrimaryContainer = onPrimaryContainerLight,
        secondary = secondaryLight,
        onSecondary = onSecondaryLight,
        secondaryContainer = secondaryContainerLight,
        onSecondaryContainer = onSecondaryContainerLight,
        tertiary = tertiaryLight,
        onTertiary = onTertiaryLight,
        tertiaryContainer = tertiaryContainerLight,
        onTertiaryContainer = onTertiaryContainerLight,
        error = errorLight,
        onError = onErrorLight,
        errorContainer = errorContainerLight,
        onErrorContainer = onErrorContainerLight,
        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,
        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight,
        outline = outlineLight,
        outlineVariant = outlineVariantLight,
        scrim = scrimLight,
        inverseSurface = inverseSurfaceLight,
        inverseOnSurface = inverseOnSurfaceLight,
        inversePrimary = inversePrimaryLight,
        surfaceDim = surfaceDimLight,
        surfaceBright = surfaceBrightLight,
        surfaceContainerLowest = surfaceContainerLowestLight,
        surfaceContainerLow = surfaceContainerLowLight,
        surfaceContainer = surfaceContainerLight,
        surfaceContainerHigh = surfaceContainerHighLight,
        surfaceContainerHighest = surfaceContainerHighestLight,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
        surfaceDim = surfaceDimDark,
        surfaceBright = surfaceBrightDark,
        surfaceContainerLowest = surfaceContainerLowestDark,
        surfaceContainerLow = surfaceContainerLowDark,
        surfaceContainer = surfaceContainerDark,
        surfaceContainerHigh = surfaceContainerHighDark,
        surfaceContainerHighest = surfaceContainerHighestDark,
    )

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
    val blurEnabledState = remember { mutableStateOf(settings.getBoolean("blurEnabled", true)) }
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
        SystemAppearance(!isDark) {
            customColorScheme = it
            if (it != null) LocalSupportsCustomColorScheme.current.value = true
        }
        val colorScheme = (if (useCustomColorScheme) customColorScheme else null) ?: if (isDark) DarkColorScheme else LightColorScheme
        MaterialExpressiveTheme(
            colorScheme = animateColorScheme(colorScheme, { tween(750) }),
            content = { Surface(content = content) },
        )
    }
}

@Composable
internal expect fun SystemAppearance(
    isDark: Boolean,
    customColorScheme: @Composable (ColorScheme?) -> Unit = {},
)
