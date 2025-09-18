package com.hansholz.bestenotenapp.notifications

expect abstract class GradeNotificationsPlatformContext

expect object GradeNotifications {
    const val KEY_ENABLED: String
    const val KEY_INTERVAL_MINUTES: String
    const val DEFAULT_INTERVAL_MINUTES: Long
    val isSupported: Boolean

    fun initialize(appContext: GradeNotificationsPlatformContext)
    fun refreshScheduling()
    fun onSettingsUpdated()
    fun onLogin()
    fun onLogout()
}
