package com.hansholz.bestenotenapp.notifications

import kotlin.coroutines.resume
import kotlin.native.concurrent.ThreadLocal
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTask
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.dateByAddingTimeInterval
import platform.Network.nw_interface_type_wifi
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_t
import platform.Network.nw_path_uses_interface_type
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.DISPATCH_QUEUE_PRIORITY_BACKGROUND
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_queue_t

private const val TASK_IDENTIFIER = "com.hansholz.bestenotenapp.notifications.refresh"

@ThreadLocal
actual object GradeNotifications {
    actual const val KEY_ENABLED: String = "gradeNotificationsEnabled"
    actual const val KEY_INTERVAL_MINUTES: String = "gradeNotificationsIntervalMinutes"
    actual const val KEY_WIFI_ONLY: String = "gradeNotificationsWifiOnly"
    actual const val DEFAULT_INTERVAL_MINUTES: Long = 60L
    actual val isSupported: Boolean = true

    private var initialized = false
    private var taskRegistered = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    actual fun initialize(platformContext: Any?) {
        GradeNotificationNotifier.ensureInitialized(platformContext)
        if (!initialized) {
            initialized = true
            registerTaskHandler()
        }
        refreshScheduling()
    }

    actual fun refreshScheduling() {
        if (!initialized) return
        if (!GradeNotificationEngine.shouldSchedule()) {
            cancelScheduledTasks()
            return
        }
        scheduleTask()
    }

    actual fun onSettingsUpdated() {
        refreshScheduling()
    }

    actual fun onLogin() {
        refreshScheduling()
    }

    actual fun onLogout() {
        cancelScheduledTasks()
        GradeNotificationEngine.clearKnownGrades()
    }

    actual fun requestPermission(onResult: () -> Unit) {
        val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
        notificationCenter.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { _, _ ->
            onResult()
        }
    }

    private fun registerTaskHandler() {
        val success = BGTaskScheduler.sharedScheduler().registerForTaskWithIdentifier(
            identifier = TASK_IDENTIFIER,
            usingQueue = null
        ) { task ->
            task?.let {
                handleTask(it)
            }
        }
        taskRegistered = success
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun scheduleTask() {
        val scheduler = BGTaskScheduler.sharedScheduler()
        scheduler.cancelTaskRequestWithIdentifier(TASK_IDENTIFIER)
        val intervalMinutes = GradeNotificationEngine.getIntervalMinutes()
            .coerceAtLeast(GradeNotificationEngine.minimumIntervalMinutes())
        val request = BGAppRefreshTaskRequest(identifier = TASK_IDENTIFIER).apply {
            earliestBeginDate = NSDate().dateByAddingTimeInterval(intervalMinutes.toDouble() * 60.0)
        }
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            scheduler.submitTaskRequest(request, error = errorPtr.ptr)
        }
    }

    private fun cancelScheduledTasks() {
        BGTaskScheduler.sharedScheduler().cancelTaskRequestWithIdentifier(TASK_IDENTIFIER)
    }

    private fun handleTask(task: BGTask) {
        scheduleTask()
        val job = scope.launch {
            val success = runCheckIfPermitted()
            task.setTaskCompletedWithSuccess(success)
        }
        task.expirationHandler = {
            job.cancel()
        }
    }

    private suspend fun runCheckIfPermitted(): Boolean {
        if (GradeNotificationEngine.isWifiOnlyEnabled() && !isOnWifi()) {
            return true
        }
        return when (GradeNotificationEngine.runCheck()) {
            GradeNotificationOutcome.Success -> true
            GradeNotificationOutcome.Retry -> false
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun isOnWifi(): Boolean = suspendCancellableCoroutine { continuation ->
        val monitor = nw_path_monitor_create()
        val queue: dispatch_queue_t = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND.toLong(), 0uL)
        nw_path_monitor_set_queue(monitor, queue)
        nw_path_monitor_set_update_handler(monitor) { path: nw_path_t? ->
            val wifiAvailable = path != null && nw_path_uses_interface_type(path, nw_interface_type_wifi)
            nw_path_monitor_cancel(monitor)
            if (continuation.isActive) {
                continuation.resume(wifiAvailable)
            }
        }
        continuation.invokeOnCancellation { nw_path_monitor_cancel(monitor) }
        nw_path_monitor_start(monitor)
    }
}

fun ensureIosNotificationsInitialized() {
    GradeNotifications.initialize(null)
}
