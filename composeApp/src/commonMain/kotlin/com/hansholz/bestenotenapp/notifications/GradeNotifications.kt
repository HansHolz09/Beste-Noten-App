package com.hansholz.bestenotenapp.notifications

expect object GradeNotifications {
    val KEY_ENABLED: String
    val KEY_INTERVAL_MINUTES: String
    val KEY_WIFI_ONLY: String
    val DEFAULT_INTERVAL_MINUTES: Long
    val isSupported: Boolean

    fun initialize(platformContext: Any?)
    fun refreshScheduling()
    fun onSettingsUpdated()
    fun onLogin()
    fun onLogout()
    fun requestPermission(onResult: () -> Unit)
}
