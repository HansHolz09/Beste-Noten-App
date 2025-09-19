package com.hansholz.bestenotenapp.notifications

import com.mmk.kmpnotifier.notification.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.notification.NotifierManager
import kotlin.native.concurrent.ThreadLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import platform.darwin.DISPATCH_QUEUE_PRIORITY_BACKGROUND
import platform.darwin.DISPATCH_SOURCE_TYPE_TIMER
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_resume
import platform.darwin.dispatch_source_cancel
import platform.darwin.dispatch_source_create
import platform.darwin.dispatch_source_set_event_handler
import platform.darwin.dispatch_source_set_timer
import platform.darwin.dispatch_source_t
import platform.darwin.dispatch_time
import kotlin.time.Duration.Companion.minutes

@ThreadLocal
actual object GradeNotifications {
    actual const val KEY_ENABLED: String = "gradeNotificationsEnabled"
    actual const val KEY_INTERVAL_MINUTES: String = "gradeNotificationsIntervalMinutes"
    actual const val DEFAULT_INTERVAL_MINUTES: Long = 60L
    actual val isSupported: Boolean = true

    private var initialized = false
    private var timer: dispatch_source_t? = null
    private var currentIntervalMinutes: Long? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    actual fun initialize(platformContext: Any?) {
        if (!initialized) {
            NotifierManager.initialize(
                NotificationPlatformConfiguration.Ios(
                    askNotificationPermissionOnStart = false
                )
            )
            initialized = true
        }
        refreshScheduling()
    }

    actual fun refreshScheduling() {
        if (!initialized) return

        if (!GradeNotificationEngine.shouldSchedule()) {
            stopTimer()
            return
        }

        val interval = GradeNotificationEngine.getIntervalMinutes()
            .coerceAtLeast(GradeNotificationEngine.minimumIntervalMinutes())

        if (timer != null && currentIntervalMinutes == interval) {
            return
        }

        startTimer(interval)
        scope.launch { GradeNotificationEngine.runCheck() }
    }

    actual fun onSettingsUpdated() {
        refreshScheduling()
    }

    actual fun onLogin() {
        refreshScheduling()
    }

    actual fun onLogout() {
        stopTimer()
        GradeNotificationEngine.clearKnownGrades()
    }

    private fun startTimer(intervalMinutes: Long) {
        stopTimer()

        val intervalNanos = intervalMinutes.minutes.inWholeNanoseconds
        val queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND.toLong(), 0uL)
        val timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0uL, 0uL, queue)
        dispatch_source_set_timer(
            timer,
            dispatch_time(DISPATCH_TIME_NOW, intervalNanos),
            intervalNanos.toULong(),
            (intervalNanos / 10).coerceAtLeast(1).toULong()
        )
        dispatch_source_set_event_handler(timer) {
            scope.launch { GradeNotificationEngine.runCheck() }
        }
        dispatch_resume(timer)
        this.timer = timer
        currentIntervalMinutes = intervalMinutes
    }

    private fun stopTimer() {
        timer?.let { dispatch_source_cancel(it) }
        timer = null
        currentIntervalMinutes = null
    }
}

internal fun ensureIosNotificationsInitialized() {
    GradeNotifications.initialize(null)
}
