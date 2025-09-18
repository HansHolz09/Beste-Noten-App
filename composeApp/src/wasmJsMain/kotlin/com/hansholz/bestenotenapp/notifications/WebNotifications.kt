package com.hansholz.bestenotenapp.notifications

import com.mmk.kmpnotifier.notification.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.mattramotar.meeseeks.runtime.AppContext

private class WebAppContext : AppContext()

private val webAppContext = WebAppContext()
private var webInitialized = false

internal fun ensureWebNotificationsInitialized() {
    if (webInitialized) return

    NotifierManager.initialize(
        NotificationPlatformConfiguration.Web(
            askNotificationPermissionOnStart = false,
            notificationIconPath = null
        )
    )
    GradeNotifications.initialize(webAppContext)
    webInitialized = true
}
