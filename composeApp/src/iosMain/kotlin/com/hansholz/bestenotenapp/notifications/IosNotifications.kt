package com.hansholz.bestenotenapp.notifications

import com.mmk.kmpnotifier.notification.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.mattramotar.meeseeks.runtime.AppContext
import kotlin.native.concurrent.ThreadLocal

private class IosAppContext : AppContext()

@ThreadLocal
internal object IosNotifications {
    private val appContext = IosAppContext()
    private var initialized = false

    fun ensureInitialized() {
        if (initialized) return

        NotifierManager.initialize(
            NotificationPlatformConfiguration.Ios(
                askNotificationPermissionOnStart = false
            )
        )
        GradeNotifications.initialize(appContext)
        initialized = true
    }
}

internal fun ensureIosNotificationsInitialized() {
    IosNotifications.ensureInitialized()
}
