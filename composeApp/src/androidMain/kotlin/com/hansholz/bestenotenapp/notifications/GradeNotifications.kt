package com.hansholz.bestenotenapp.notifications

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

actual object GradeNotifications {
    actual const val KEY_ENABLED: String = "gradeNotificationsEnabled"
    actual const val KEY_INTERVAL_MINUTES: String = "gradeNotificationsIntervalMinutes"
    actual const val KEY_WIFI_ONLY: String = "gradeNotificationsWifiOnly"
    actual const val DEFAULT_INTERVAL_MINUTES: Long = 60L
    actual val isSupported: Boolean = true

    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    private const val ALARM_REQUEST_CODE = 1002

    private var applicationContext: Context? = null
    private var activityRef: WeakReference<Activity?> = WeakReference(null)
    private var alarmManager: AlarmManager? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    actual fun initialize(platformContext: Any?) {
        when (platformContext) {
            is Activity -> {
                activityRef = WeakReference(platformContext)
                setApplicationContext(platformContext.applicationContext)
            }
            is Context -> setApplicationContext(platformContext.applicationContext)
        }
        ensureNotificationServiceInitialized(platformContext ?: applicationContext)
        refreshScheduling()
    }

    actual fun refreshScheduling() {
        val context = applicationContext ?: return
        if (!GradeNotificationEngine.shouldSchedule()) {
            cancelScheduledAlarms()
            return
        }
        ensureNotificationServiceInitialized(context)
        scheduleNextAlarm(context)
        scope.launch { runCheckIfPermitted(context) }
    }

    actual fun onSettingsUpdated() {
        refreshScheduling()
    }

    actual fun onLogin() {
        refreshScheduling()
    }

    actual fun onLogout() {
        cancelScheduledAlarms()
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
            requestExactAlarmPermissionIfNeeded(activity)
            onResult()
            return
        }

        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                openNotificationSettings(context)
            }
        }
        requestExactAlarmPermissionIfNeeded(activity)
        onResult()
    }

    internal fun onAlarmFired(context: Context, pendingResult: BroadcastReceiver.PendingResult) {
        val appContext = applicationContext ?: context.applicationContext.also { setApplicationContext(it) }
        if (!GradeNotificationEngine.shouldSchedule()) {
            cancelScheduledAlarms()
            pendingResult.finish()
            return
        }
        ensureNotificationServiceInitialized(appContext)
        scheduleNextAlarm(appContext)
        scope.launch {
            try {
                runCheckIfPermitted(appContext)
            } finally {
                pendingResult.finish()
            }
        }
    }

    internal fun handleBootCompleted(context: Context) {
        setApplicationContext(context.applicationContext)
        ensureNotificationServiceInitialized(applicationContext)
        refreshScheduling()
    }

    private fun setApplicationContext(context: Context) {
        applicationContext = context
        if (alarmManager == null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        }
    }

    private fun scheduleNextAlarm(context: Context) {
        val intervalMinutes = GradeNotificationEngine.getIntervalMinutes()
            .coerceAtLeast(GradeNotificationEngine.minimumIntervalMinutes())
        val triggerAtMillis = System.currentTimeMillis() + intervalMinutes * 60_000
        val manager = alarmManager ?: (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.also {
            alarmManager = it
        }
        val pendingIntent = createPendingIntent(context)
        manager?.cancel(pendingIntent)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                manager?.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            manager?.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun cancelScheduledAlarms() {
        val context = applicationContext ?: return
        val existingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            Intent(context, GradeNotificationReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (existingIntent != null) {
            val manager = alarmManager ?: (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.also {
                alarmManager = it
            }
            manager?.cancel(existingIntent)
            existingIntent.cancel()
        }
    }

    private suspend fun runCheckIfPermitted(context: Context) {
        if (isWifiRequiredAndUnavailable(context)) {
            return
        }
        GradeNotificationEngine.runCheck()
    }

    private fun isWifiRequiredAndUnavailable(context: Context): Boolean {
        if (!GradeNotificationEngine.isWifiOnlyEnabled()) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return true
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return true
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return true
            !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            @Suppress("DEPRECATION")
            val info = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            info?.type != ConnectivityManager.TYPE_WIFI
        }
    }

    private fun ensureNotificationServiceInitialized(platformContext: Any?) {
        GradeNotificationNotifier.ensureInitialized(platformContext ?: applicationContext)
    }

    private fun requestExactAlarmPermissionIfNeeded(activity: Activity?) {
        if (activity == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val manager = activity.getSystemService(AlarmManager::class.java) ?: return
        if (!manager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${'$'}{activity.packageName}")
            }
            activity.startActivity(intent)
        }
    }

    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GradeNotificationReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

private fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
