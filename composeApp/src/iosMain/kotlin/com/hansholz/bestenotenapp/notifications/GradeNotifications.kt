package com.hansholz.bestenotenapp.notifications

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTask
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.NSLog
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
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

private const val TASK_IDENTIFIER = "com.hansholz.bestenotenapp.notifications.refresh"
private var notificationErrorLogger: (String) -> Unit = { NSLog("%@", it) }

internal fun logGradeNotificationError(message: String) = notificationErrorLogger(message)

actual object GradeNotifications {
    actual val isSupported: Boolean = true

    private var initialized = false
    private var taskRegistered = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val checkMutex = Mutex()
    private val schedulingMutex = Mutex()

    actual fun initialize(platformContext: Any?) {
        GradeNotificationNotifier.ensureInitialized(platformContext)
        if (!initialized) {
            initialized = true
            registerTaskHandler()
            refreshScheduling()
        }
    }

    actual fun refreshScheduling() {
        scope.launch {
            ensureScheduled()
        }
    }

    private suspend fun ensureScheduled(
        replace: Boolean = false,
        delayMillis: Long = GradeNotificationEngine.getNextCheckDelayMillis(),
    ) = schedulingMutex.withLock {
        try {
            updateScheduling(replace, delayMillis)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logGradeNotificationError("Background notification scheduling failed: ${e::class.simpleName}")
        }
    }

    private suspend fun updateScheduling(
        replace: Boolean,
        delayMillis: Long,
    ) {
        if (!initialized || !taskRegistered) {
            return
        }
        if (!GradeNotificationEngine.shouldSchedule()) {
            cancelScheduledTasks()
            return
        }
        if (replace) {
            scheduleTask(delayMillis)
            return
        }
        val pending =
            withTimeoutOrNull(2.seconds) {
                suspendCancellableCoroutine { continuation ->
                    BGTaskScheduler.sharedScheduler().getPendingTaskRequestsWithCompletionHandler { requests ->
                        val scheduled = requests?.filterIsInstance<platform.BackgroundTasks.BGTaskRequest>()?.any { it.identifier == TASK_IDENTIFIER } == true
                        if (continuation.isActive) continuation.resume(scheduled)
                    }
                }
            }
        if (pending != true) scheduleTask(delayMillis)
    }

    actual fun onSettingsUpdated() {
        scope.launch { ensureScheduled(replace = true) }
        checkNowIfEnabled()
    }

    actual fun onLogin() {
        refreshScheduling()
        if (!GradeNotificationEngine.hasBaseline()) checkNowIfEnabled()
    }

    actual fun onLogout() {
        scope.coroutineContext.cancelChildren()
        cancelScheduledTasks()
        GradeNotificationEngine.clearKnownGrades()
    }

    actual suspend fun requestPermission(): Boolean =
        suspendCancellableCoroutine { continuation ->
            UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound,
            ) { granted, error ->
                if (error != null) logGradeNotificationError("Notification permission request failed: ${error.domain}/${error.code}")
                if (continuation.isActive) continuation.resume(granted)
            }
        }

    private fun registerTaskHandler() {
        val success =
            BGTaskScheduler.sharedScheduler().registerForTaskWithIdentifier(
                identifier = TASK_IDENTIFIER,
                usingQueue = null,
            ) { task ->
                task?.let {
                    handleTask(it)
                }
            }
        taskRegistered = success
        if (!success) logGradeNotificationError("Background notification task registration failed")
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun scheduleTask(delayMillis: Long) {
        val scheduler = BGTaskScheduler.sharedScheduler()
        val request =
            BGAppRefreshTaskRequest(identifier = TASK_IDENTIFIER).apply {
                earliestBeginDate = NSDate().dateByAddingTimeInterval(delayMillis / 1000.0)
            }
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            errorPtr.value = null
            if (!scheduler.submitTaskRequest(request, error = errorPtr.ptr)) {
                logGradeNotificationError("Background notification scheduling failed: ${errorPtr.value?.domain}/${errorPtr.value?.code}")
            }
        }
    }

    private fun cancelScheduledTasks() {
        BGTaskScheduler.sharedScheduler().cancelTaskRequestWithIdentifier(TASK_IDENTIFIER)
    }

    private fun handleTask(task: BGTask) {
        var success = false
        val job =
            scope.launch(start = CoroutineStart.LAZY) {
                try {
                    withTimeout(25.seconds) {
                        // Keep a retry pending even if iOS expires this task before the check finishes.
                        ensureScheduled(replace = true, delayMillis = 15 * 60_000L)
                        success = runCheckIfPermitted("background")
                        if (success) ensureScheduled(replace = true)
                    }
                } catch (_: TimeoutCancellationException) {
                    success = false
                }
            }
        task.expirationHandler = {
            job.cancel()
        }
        job.invokeOnCompletion { cause ->
            val completed = success && cause == null
            task.setTaskCompletedWithSuccess(completed)
        }
        job.start()
    }

    private fun checkNowIfEnabled() {
        scope.launch { runCheckIfPermitted("foreground") }
    }

    private suspend fun runCheckIfPermitted(source: String): Boolean {
        return try {
            withTimeout(25.seconds) {
                if (source == "foreground" && checkMutex.isLocked) {
                    return@withTimeout true
                }
                checkMutex.withLock {
                    if (!GradeNotificationEngine.shouldSchedule()) {
                        return@withLock true
                    }
                    if (GradeNotificationEngine.isWifiOnlyEnabled() && withTimeoutOrNull(3.seconds) { isOnWifi() } != true) {
                        return@withLock false
                    }
                    val outcome = GradeNotificationEngine.runCheck()
                    outcome == GradeNotificationOutcome.Success
                }
            }
        } catch (_: TimeoutCancellationException) {
            false
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logGradeNotificationError("Background grade check failed: ${e::class.simpleName}")
            false
        }
    }

    internal fun onForeground() {
        refreshScheduling()
    }

    private suspend fun isOnWifi(): Boolean =
        suspendCancellableCoroutine { continuation ->
            val monitor = nw_path_monitor_create()
            val queue: dispatch_queue_t = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND.toLong(), 0uL)
            nw_path_monitor_set_queue(monitor, queue)
            nw_path_monitor_set_update_handler(monitor) { path: nw_path_t ->
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

fun ensureIosNotificationsInitialized(logError: (String) -> Unit) {
    notificationErrorLogger = logError
    GradeNotifications.initialize(null)
}

fun onIosNotificationsForegrounded() {
    GradeNotifications.onForeground()
}

fun onIosNotificationsBackgrounded() {
    GradeNotifications.refreshScheduling()
}
