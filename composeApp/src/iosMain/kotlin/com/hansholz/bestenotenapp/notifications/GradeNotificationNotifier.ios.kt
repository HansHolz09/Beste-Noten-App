package com.hansholz.bestenotenapp.notifications

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

internal actual object GradeNotificationNotifier {
    actual fun ensureInitialized(platformContext: Any?) = Unit

    actual suspend fun notifyNewGrades(notifications: List<GradeNotificationPayload>): Boolean {
        if (notifications.isEmpty()) return true
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val authorized =
            suspendCancellableCoroutine { continuation ->
                center.getNotificationSettingsWithCompletionHandler { settings ->
                    val granted =
                        settings?.authorizationStatus in
                            listOf(UNAuthorizationStatusAuthorized, UNAuthorizationStatusProvisional, UNAuthorizationStatusEphemeral)
                    if (continuation.isActive) continuation.resume(granted)
                }
            }
        if (!authorized) return false

        notifications.forEach { payload ->
            val content =
                UNMutableNotificationContent().apply {
                    setTitle(payload.title)
                    setBody(payload.body)
                    setSound(UNNotificationSound.defaultSound())
                }
            val request = UNNotificationRequest.requestWithIdentifier(payload.id, content, trigger = null)
            val submitted =
                suspendCancellableCoroutine { continuation ->
                    center.addNotificationRequest(request) { error ->
                        if (error != null) logGradeNotificationError("Grade notification submission failed: ${error.domain}/${error.code}")
                        if (continuation.isActive) continuation.resume(error == null)
                    }
                }
            if (!submitted) return false
        }
        return true
    }
}
