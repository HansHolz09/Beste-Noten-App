@file:OptIn(ExperimentalComposeUiApi::class)

package com.hansholz.bestenotenapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.hansholz.bestenotenapp.components.ConfettiPresets
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import io.github.vinceglb.confettikit.compose.ConfettiKit
import platform.UIKit.UIColor
import platform.UIKit.UIViewController
import top.ltfan.multihaptic.compose.rememberVibrator

fun easterEggOverlayViewController(nativeBridge: NativeComponentBridge): UIViewController =
    ComposeUIViewController(configure = { opaque = false }) {
        NativeTheme(nativeBridge) {
            val event = nativeBridge.easterEggState.value
            if (event != null) {
                val vibrator = rememberVibrator()
                ConfettiKit(
                    modifier = Modifier.fillMaxSize(),
                    parties = if (event == "logo") ConfettiPresets.logos() else ConfettiPresets.randomFirework(20),
                    onParticleSystemStarted = { _, _ ->
                        if (event != "logo") vibrator.enhancedVibrateN(EnhancedVibrations.EXPLOSION)
                    },
                    onParticleSystemEnded = { _, activeSystems ->
                        if (activeSystems == 0) nativeBridge.easterEggState.value = null
                    },
                )
            }
        }
    }.also { controller ->
        controller.view.backgroundColor = UIColor.clearColor
        controller.view.opaque = false
        controller.view.userInteractionEnabled = false
    }
