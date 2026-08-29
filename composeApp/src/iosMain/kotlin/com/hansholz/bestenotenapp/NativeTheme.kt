package com.hansholz.bestenotenapp

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.hansholz.bestenotenapp.components.ProvidePlatformInteractionFeedback
import com.hansholz.bestenotenapp.theme.AppTypography
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalThemeIsDark

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun NativeTheme(
    nativeBridge: NativeComponentBridge,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalThemeIsDark provides nativeBridge.themeIsDarkState.value,
        LocalBlurEnabled provides remember { mutableStateOf(true) },
    ) {
        MaterialExpressiveTheme(
            colorScheme = nativeBridge.themeColorSchemeState.value ?: MaterialTheme.colorScheme,
            typography = AppTypography,
        ) {
            ProvidePlatformInteractionFeedback(content)
        }
    }
}
