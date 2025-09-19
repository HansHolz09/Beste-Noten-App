package com.hansholz.bestenotenapp.notifications

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.lang.ref.WeakReference
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
    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

    private var applicationContext: Context? = null
    private var activityRef: WeakReference<Activity?> = WeakReference(null)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    actual fun initialize(platformContext: Any?) {
        when (platformContext) {
            is Activity -> {
                activityRef = WeakReference(platformContext)
                applicationContext = platformContext.applicationContext
            }
            is Context -> {
                applicationContext = platformContext.applicationContext
            }
        }
        GradeNotificationNotifier.ensureInitialized(platformContext ?: applicationContext)
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

    actual fun requestPermission(onResult: () -> Unit) {
        val activity = activityRef.get()
        val context = applicationContext ?: activity?.applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (activity != null && ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                activity.runOnUiThread {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                }
                onResult()
                return
            }
            if (context != null && activity == null && !NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                openNotificationSettings(context)
            }
            onResult()
            return
        }

        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                openNotificationSettings(context)
            }
        }
        onResult()
    }
}

private fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
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
