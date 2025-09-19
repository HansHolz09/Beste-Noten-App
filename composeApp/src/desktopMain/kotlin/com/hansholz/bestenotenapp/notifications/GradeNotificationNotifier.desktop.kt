package com.hansholz.bestenotenapp.notifications

internal actual object GradeNotificationNotifier {
    actual fun ensureInitialized(platformContext: Any?) {}
    actual fun notifyNewGrades(notifications: List<GradeNotificationPayload>) {}
}
