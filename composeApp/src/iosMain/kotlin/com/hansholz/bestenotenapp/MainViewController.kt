package com.hansholz.bestenotenapp

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.main.LocalBiometricAuthenticationAvailable
import com.hansholz.bestenotenapp.main.LocalNavigationDrawerTopPadding
import com.hansholz.bestenotenapp.notifications.ensureIosNotificationsInitialized
import com.hansholz.bestenotenapp.utils.installLegacyInsetsPatch
import com.hansholz.bestenotenapp.utils.isInWindowMode
import eu.anifantakis.lib.ksafe.biometrics.KSafeBiometrics
import kotlinx.coroutines.runBlocking
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController {
    val controller =
        ComposeUIViewController {
            ensureIosNotificationsInitialized()
            CompositionLocalProvider(
                LocalNavigationDrawerTopPadding provides if (isInWindowMode()) 50.dp else null,
                LocalBiometricAuthenticationAvailable provides runBlocking { KSafeBiometrics.biometricsAvailable() },
            ) {
                App()
            }
        }
    installLegacyInsetsPatch(controller.view)
    return controller
}
