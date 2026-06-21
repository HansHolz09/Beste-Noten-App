package com.hansholz.bestenotenapp

import android.app.Application
import com.hansholz.bestenotenapp.notifications.GradeNotifications

class BesteNotenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GradeNotifications.initialize(this)
    }
}
