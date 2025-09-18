package com.hansholz.bestenotenapp.notifications

import com.mmk.kmpnotifier.notification.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.mattramotar.meeseeks.runtime.AppContext
import java.util.concurrent.atomic.AtomicBoolean

private class DesktopAppContext : AppContext()

private val desktopAppContext = DesktopAppContext()
private val desktopInitialized = AtomicBoolean(false)

internal fun ensureDesktopNotificationsInitialized() {
    if (desktopInitialized.get()) return
    synchronized(desktopInitialized) {
        if (desktopInitialized.get()) return

        NotifierManager.initialize(
            NotificationPlatformConfiguration.Desktop(
                notificationIconPath = null
            )
        )
        GradeNotifications.initialize(desktopAppContext)
        desktopInitialized.set(true)
    }
}
