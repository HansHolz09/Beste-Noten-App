package com.hansholz.bestenotenapp.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hansholz.bestenotenapp.components.ConfettiPresets
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.components.enhanced.rememberEnhancedVibrator
import io.github.vinceglb.confettikit.compose.ConfettiKit

@Composable
fun ConfettiEasterEgg(settingsViewModel: SettingsViewModel) {
    val vibrator = rememberEnhancedVibrator()
    if (settingsViewModel.showConfetti) {
        ConfettiKit(
            modifier = Modifier.fillMaxSize(),
            parties = ConfettiPresets.randomFirework(20),
            onParticleSystemStarted = { _, _ ->
                vibrator.enhancedVibrateN(EnhancedVibrations.EXPLOSION)
            },
            onParticleSystemEnded = { _, activeSystems ->
                if (activeSystems == 0) settingsViewModel.showConfetti = false
            },
        )
    }
}
