package com.hansholz.bestenotenapp.notifications

internal data class GradeNotificationPayload(
    val id: String,
    val title: String,
    val body: String
)

internal expect object GradeNotificationNotifier {
    fun ensureInitialized(platformContext: Any?)
    fun notifyNewGrades(notifications: List<GradeNotificationPayload>)
}
