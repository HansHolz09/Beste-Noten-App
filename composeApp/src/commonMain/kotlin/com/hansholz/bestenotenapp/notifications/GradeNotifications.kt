package com.hansholz.bestenotenapp.notifications

expect object GradeNotifications {
    const val KEY_ENABLED: String
    const val KEY_INTERVAL_MINUTES: String
    const val KEY_WIFI_ONLY: String
    const val DEFAULT_INTERVAL_MINUTES: Long
    val isSupported: Boolean

    fun initialize(platformContext: Any?)
    fun refreshScheduling()
    fun onSettingsUpdated()
    fun onLogin()
    fun onLogout()
    fun requestPermission(onResult: () -> Unit)
}
