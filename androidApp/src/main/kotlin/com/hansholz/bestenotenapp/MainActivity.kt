package com.hansholz.bestenotenapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.hansholz.bestenotenapp.api.androidCodeAuthFlowFactory
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.main.LocalBiometricAuthenticationAvailable
import com.hansholz.bestenotenapp.notifications.GradeNotifications
import eu.anifantakis.lib.ksafe.biometrics.BiometricHelper
import eu.anifantakis.lib.ksafe.biometrics.KSafeBiometrics
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kotlinx.coroutines.runBlocking
import tech.kotlinlang.permission.PermissionInitiation

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        GradeNotifications.initialize(this)
        PermissionInitiation.setActivity(this)
        FileKit.init(this)
        androidCodeAuthFlowFactory.registerActivity(this)

        BiometricHelper.confirmationRequired = false

        setContent {
            CompositionLocalProvider(
                LocalBiometricAuthenticationAvailable provides runBlocking { KSafeBiometrics.biometricsAvailable() },
            ) {
                App()
            }
        }
    }
}
