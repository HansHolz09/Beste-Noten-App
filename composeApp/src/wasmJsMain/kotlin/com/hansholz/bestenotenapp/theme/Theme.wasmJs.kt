package com.hansholz.bestenotenapp.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
internal actual fun SystemAppearance(
    isDark: Boolean,
    customColorScheme: @Composable ((ColorScheme?) -> Unit)
) {
}