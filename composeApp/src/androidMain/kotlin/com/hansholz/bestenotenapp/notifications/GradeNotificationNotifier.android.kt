package com.hansholz.bestenotenapp.notifications

import android.app.NotificationManager
import android.content.Context
import com.hansholz.bestenotenapp.R
import com.tweener.alarmee.createAlarmeeService
import com.tweener.alarmee.channel.AlarmeeNotificationChannel
import com.tweener.alarmee.configuration.AlarmeeAndroidPlatformConfiguration
import com.tweener.alarmee.model.Alarmee
import com.tweener.alarmee.model.AndroidNotificationConfiguration
import com.tweener.alarmee.model.AndroidNotificationPriority
import com.tweener.alarmee.model.IosNotificationConfiguration

private const val CHANNEL_ID = "grade_notifications"
private const val CHANNEL_NAME = "Neue Noten"

private val service = createAlarmeeService()

internal actual object GradeNotificationNotifier {
    private var initialized = false

    actual fun ensureInitialized(platformContext: Any?) {
        if (initialized) return
        val context = (platformContext as? Context)?.applicationContext ?: return
        val channelName = context.packageManager.applicationLabel(context.applicationInfo)?.toString()
            ?: CHANNEL_NAME
        val configuration = AlarmeeAndroidPlatformConfiguration(
            notificationIconResId = R.drawable.ic_launcher_monochrome,
            notificationChannels = listOf(
                AlarmeeNotificationChannel(
                    id = CHANNEL_ID,
                    name = channelName,
                    importance = NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        )
        service.initialize(configuration)
        initialized = true
    }

    actual fun notifyNewGrades(notifications: List<GradeNotificationPayload>) {
        if (!initialized || notifications.isEmpty()) return

        notifications.forEach { payload ->
            service.local.immediate(
                Alarmee(
                    uuid = payload.id,
                    notificationTitle = payload.title,
                    notificationBody = payload.body,
                    androidNotificationConfiguration = AndroidNotificationConfiguration(
                        priority = AndroidNotificationPriority.DEFAULT,
                        channelId = CHANNEL_ID
                    ),
                    iosNotificationConfiguration = IosNotificationConfiguration()
                )
            )
        }
    }
}
