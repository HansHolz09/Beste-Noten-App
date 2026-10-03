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
import platform.Foundation.dateByAddingTimeInterval
import platform.Network.nw_interface_type_wifi
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_t
import platform.Network.nw_path_uses_interface_type
import platform.darwin.DISPATCH_QUEUE_PRIORITY_BACKGROUND
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_queue_t
import tech.kotlinlang.permission.HelperHolder
import tech.kotlinlang.permission.Permission
import tech.kotlinlang.permission.result.NotificationPermissionResult
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

private const val TASK_IDENTIFIER = "com.hansholz.bestenotenapp.notifications.refresh"

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

    private suspend fun ensureScheduled(replace: Boolean = false) =
        schedulingMutex.withLock {
            try {
                updateScheduling(replace)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private suspend fun updateScheduling(replace: Boolean) {
        if (!initialized || !taskRegistered) {
            return
        }
        if (!GradeNotificationEngine.shouldSchedule()) {
            cancelScheduledTasks()
            return
        }
        val pending =
            suspendCancellableCoroutine { continuation ->
                BGTaskScheduler.sharedScheduler().getPendingTaskRequestsWithCompletionHandler { requests ->
                    val scheduled = requests?.filterIsInstance<platform.BackgroundTasks.BGTaskRequest>()?.any { it.identifier == TASK_IDENTIFIER } == true
                    if (continuation.isActive) continuation.resume(scheduled)
                }
            }
        if (!pending || replace) scheduleTask()
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

    actual suspend fun requestPermission(): Boolean {
        val permissionHelper = HelperHolder.getPermissionHelperInstance()
        val permission = Permission.Notification

        val checkPermissionResult = permissionHelper.checkIsPermissionGranted(permission)
        var granted = checkPermissionResult == NotificationPermissionResult.Granted
        if (granted) return true

        val requestPermissionResult = permissionHelper.requestForPermission(permission)
        granted = requestPermissionResult == NotificationPermissionResult.Granted
        return granted
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
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun scheduleTask() {
        val scheduler = BGTaskScheduler.sharedScheduler()
        val request =
            BGAppRefreshTaskRequest(identifier = TASK_IDENTIFIER).apply {
                earliestBeginDate = NSDate().dateByAddingTimeInterval(GradeNotificationEngine.getNextCheckDelayMillis() / 1000.0)
            }
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            errorPtr.value = null
            if (!scheduler.submitTaskRequest(request, error = errorPtr.ptr)) {
                println("Background notification scheduling failed: ${errorPtr.value?.domain}/${errorPtr.value?.code}")
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
                ensureScheduled()
                success = runCheckIfPermitted("background")
                ensureScheduled(replace = true)
            }
        task.expirationHandler = {
            job.cancel()
        }
        job.invokeOnCompletion {
            task.setTaskCompletedWithSuccess(success)
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
                        return@withLock true
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
            e.printStackTrace()
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

fun ensureIosNotificationsInitialized() {
    GradeNotifications.initialize(null)
}

fun onIosNotificationsForegrounded() {
    GradeNotifications.onForeground()
}

fun onIosNotificationsBackgrounded() {
    GradeNotifications.refreshScheduling()
}
