package com.hansholz.bestenotenapp.notifications

actual class GradeNotificationsPlatformContext

actual object GradeNotifications {
    actual const val KEY_ENABLED: String = "gradeNotificationsEnabled"
    actual const val KEY_INTERVAL_MINUTES: String = "gradeNotificationsIntervalMinutes"
    actual const val DEFAULT_INTERVAL_MINUTES: Long = 60L
    actual val isSupported: Boolean = false

    actual fun initialize(appContext: GradeNotificationsPlatformContext) {}
    actual fun refreshScheduling() {}
    actual fun onSettingsUpdated() {}
    actual fun onLogin() {}
    actual fun onLogout() {}
}
