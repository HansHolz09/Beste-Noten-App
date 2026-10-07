package com.hansholz.bestenotenapp.notifications

internal actual object GradeNotificationNotifier {
    actual fun ensureInitialized(platformContext: Any?) {}

    actual suspend fun notifyNewGrades(notifications: List<GradeNotificationPayload>): Boolean = true
}
