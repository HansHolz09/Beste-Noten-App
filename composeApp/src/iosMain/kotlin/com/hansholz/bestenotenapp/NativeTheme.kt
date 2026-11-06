package com.hansholz.bestenotenapp

import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.hansholz.bestenotenapp.components.ProvidePlatformInteractionFeedback
import com.hansholz.bestenotenapp.theme.AppTypography
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalSystemAccessibility
import com.hansholz.bestenotenapp.theme.LocalThemeIsDark
import com.hansholz.bestenotenapp.theme.rememberSystemAccessibility
import dev.chrisbanes.haze.glass.GlassAccessibilitySettings
import dev.chrisbanes.haze.glass.LocalGlassAccessibilitySettings

@Composable
internal fun NativeTheme(
    nativeBridge: NativeComponentBridge,
    content: @Composable () -> Unit,
) {
    val systemAccessibility = rememberSystemAccessibility()
    CompositionLocalProvider(
        LocalThemeIsDark provides nativeBridge.themeIsDarkState.value,
        LocalAnimationsEnabled provides nativeBridge.animationsEnabledState,
        LocalSystemAccessibility provides systemAccessibility,
        LocalBlurEnabled provides remember(systemAccessibility.reduceTransparency) { mutableStateOf(!systemAccessibility.reduceTransparency) },
        LocalGlassAccessibilitySettings provides
            GlassAccessibilitySettings(
                reduceTransparency = systemAccessibility.reduceTransparency,
                increaseContrast = systemAccessibility.increaseContrast,
                showBorders = systemAccessibility.showBorders,
            ),
    ) {
        MaterialExpressiveTheme(
            colorScheme = nativeBridge.themeColorSchemeState.value ?: MaterialTheme.colorScheme,
            typography = AppTypography,
        ) {
            ProvidePlatformInteractionFeedback(content)
        }
    }
}
