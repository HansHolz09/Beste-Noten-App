package com.hansholz.bestenotenapp

import android.app.Application
import com.hansholz.bestenotenapp.notifications.GradeNotifications
import com.hansholz.bestenotenapp.utils.AndroidContext
import com.mmk.kmpnotifier.notification.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.notification.NotifierManager

class BesteNotenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidContext.init(this)
        NotifierManager.initialize(
            NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_monochrome
            )
        )
        GradeNotifications.initialize(this)
    }
}
