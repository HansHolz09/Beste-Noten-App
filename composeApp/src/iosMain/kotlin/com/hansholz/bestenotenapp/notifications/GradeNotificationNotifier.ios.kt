package com.hansholz.bestenotenapp.notifications

import com.tweener.alarmee.createAlarmeeService
import com.tweener.alarmee.configuration.AlarmeeIosPlatformConfiguration
import com.tweener.alarmee.model.Alarmee
import com.tweener.alarmee.model.AndroidNotificationConfiguration
import com.tweener.alarmee.model.IosNotificationConfiguration

private val service = createAlarmeeService()

internal actual object GradeNotificationNotifier {
    private var initialized = false

    actual fun ensureInitialized(platformContext: Any?) {
        if (initialized) return
        service.initialize(AlarmeeIosPlatformConfiguration)
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
                    androidNotificationConfiguration = AndroidNotificationConfiguration(),
                    iosNotificationConfiguration = IosNotificationConfiguration()
                )
            )
        }
    }
}
