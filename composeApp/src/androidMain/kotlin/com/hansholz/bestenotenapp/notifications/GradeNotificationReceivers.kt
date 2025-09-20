package com.hansholz.bestenotenapp.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

internal class GradeNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val result = goAsync()
        GradeNotifications.onAlarmFired(context.applicationContext, result)
    }
}

internal class GradeNotificationBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> GradeNotifications.handleBootCompleted(context.applicationContext)
        }
    }
}
