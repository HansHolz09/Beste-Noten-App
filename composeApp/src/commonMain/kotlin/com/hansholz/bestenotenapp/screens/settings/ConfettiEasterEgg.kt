package com.hansholz.bestenotenapp.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.hansholz.bestenotenapp.components.ConfettiPresets
import io.github.vinceglb.confettikit.compose.ConfettiKit

@Composable
fun ConfettiEasterEgg(settingsViewModel: SettingsViewModel) {
    val hapticFeedback = LocalHapticFeedback.current
    if (settingsViewModel.showConfetti) {
        ConfettiKit(
            modifier = Modifier.fillMaxSize(),
            parties = ConfettiPresets.randomFirework(20),
            onParticleSystemStarted = { _, _ ->
                hapticFeedback.performHapticFeedback(HapticFeedbackType.VirtualKey)
            },
            onParticleSystemEnded = { _, activeSystems ->
                if (activeSystems == 0) settingsViewModel.showConfetti = false
            },
        )
    }
}
