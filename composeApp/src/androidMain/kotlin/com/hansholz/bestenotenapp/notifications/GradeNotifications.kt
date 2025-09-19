package com.hansholz.bestenotenapp.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

actual object GradeNotifications {
    actual const val KEY_ENABLED: String = "gradeNotificationsEnabled"
    actual const val KEY_INTERVAL_MINUTES: String = "gradeNotificationsIntervalMinutes"
    actual const val DEFAULT_INTERVAL_MINUTES: Long = 60L
    actual val isSupported: Boolean = true

    private const val WORK_NAME = "com.hansholz.bestenotenapp.notifications.gradeCheck"

    private var applicationContext: Context? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    actual fun initialize(platformContext: Any?) {
        val context = (platformContext as? Context)?.applicationContext ?: return
        if (applicationContext == null) {
            applicationContext = context
        }
        refreshScheduling()
    }

    actual fun refreshScheduling() {
        val context = applicationContext ?: return
        val workManager = WorkManager.getInstance(context)
        if (!GradeNotificationEngine.shouldSchedule()) {
            workManager.cancelUniqueWork(WORK_NAME)
            return
        }

        val intervalMinutes = GradeNotificationEngine.getIntervalMinutes()
            .coerceAtLeast(GradeNotificationEngine.minimumIntervalMinutes())

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<GradeNotificationWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )

        scope.launch { GradeNotificationEngine.runCheck() }
    }

    actual fun onSettingsUpdated() {
        refreshScheduling()
    }

    actual fun onLogin() {
        refreshScheduling()
    }

    actual fun onLogout() {
        applicationContext?.let { WorkManager.getInstance(it).cancelUniqueWork(WORK_NAME) }
        GradeNotificationEngine.clearKnownGrades()
    }
}

internal class GradeNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = when (GradeNotificationEngine.runCheck()) {
        GradeNotificationOutcome.Success -> Result.success()
        GradeNotificationOutcome.Retry -> Result.retry()
    }
}
