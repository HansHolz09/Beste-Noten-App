package com.hansholz.bestenotenapp.notifications

expect object GradeNotifications {
    val isSupported: Boolean

    fun initialize(platformContext: Any?)

    fun refreshScheduling()

    fun onSettingsUpdated()

    fun onLogin()

    fun onLogout()

    suspend fun requestPermission(): Boolean
}
