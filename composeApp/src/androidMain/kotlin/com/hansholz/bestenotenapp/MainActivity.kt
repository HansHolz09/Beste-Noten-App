package com.hansholz.bestenotenapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.hansholz.bestenotenapp.api.androidCodeAuthFlowFactory
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.notifications.GradeNotifications
import com.hansholz.bestenotenapp.utils.AndroidContext
import eu.anifantakis.lib.ksafe.BiometricHelper
import tech.kotlinlang.permission.PermissionInitiation

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AndroidContext.init(this)
        GradeNotifications.initialize(this)
        PermissionInitiation.setActivity(this)
        androidCodeAuthFlowFactory.registerActivity(this)
        BiometricHelper.promptTitle = "Authentifizieren"
        setContent {
            App()
        }
    }
}
