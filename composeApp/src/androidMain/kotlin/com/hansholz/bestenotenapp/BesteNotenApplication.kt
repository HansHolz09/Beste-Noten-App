package com.hansholz.bestenotenapp

import android.app.Application
import com.hansholz.bestenotenapp.notifications.GradeNotifications
import com.hansholz.bestenotenapp.utils.AndroidContext

class BesteNotenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidContext.init(this)
        GradeNotifications.initialize(this)
    }
}
