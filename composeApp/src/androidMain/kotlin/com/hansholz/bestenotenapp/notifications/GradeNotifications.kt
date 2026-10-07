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
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

actual object GradeNotifications {
    actual val isSupported: Boolean = true

    private const val ALARM_REQUEST_CODE = 1002

    private var applicationContext: Context? = null
    private var activityRef: WeakReference<Activity?> = WeakReference(null)
    private var alarmManager: AlarmManager? = null
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var permissionResult: CompletableDeferred<Boolean>? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    actual fun initialize(platformContext: Any?) {
        when (platformContext) {
            is Activity -> {
                activityRef = WeakReference(platformContext)
                setApplicationContext(platformContext.applicationContext)
                if (platformContext is ComponentActivity) {
                    permissionLauncher =
                        platformContext.activityResultRegistry.register(
                            "gradeNotificationPermission",
                            platformContext,
                            ActivityResultContracts.RequestPermission(),
                        ) { granted ->
                            permissionResult?.complete(granted)
                        }
                }
            }

            is Context -> {
                setApplicationContext(platformContext.applicationContext)
            }
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
    }

    actual fun onSettingsUpdated() {
        refreshScheduling()
    }

    actual fun onLogin() {
        refreshScheduling()
    }

    actual fun onLogout() {
        scope.coroutineContext.cancelChildren()
        cancelScheduledAlarms()
        GradeNotificationEngine.clearKnownGrades()
    }

    actual suspend fun requestPermission(): Boolean =
        withContext(Dispatchers.Main) {
            val activity = activityRef.get() ?: return@withContext false
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ) {
                requestExactAlarmPermissionIfNeeded(activity)
                return@withContext true
            }

            val launcher = permissionLauncher ?: return@withContext false
            val result =
                permissionResult ?: CompletableDeferred<Boolean>().also {
                    permissionResult = it
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            try {
                val granted = result.await()
                if (granted) requestExactAlarmPermissionIfNeeded(activity)
                granted
            } finally {
                if (permissionResult === result) permissionResult = null
            }
        }

    internal fun onAlarmFired(
        context: Context,
        pendingResult: BroadcastReceiver.PendingResult,
    ) {
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
                if (GradeNotificationEngine.shouldSchedule()) scheduleNextAlarm(appContext)
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
        val triggerAtMillis = System.currentTimeMillis() + GradeNotificationEngine.getNextCheckDelayMillis()
        val manager =
            alarmManager ?: (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.also {
                alarmManager = it
            }
        val pendingIntent = createPendingIntent(context)
        manager?.cancel(pendingIntent)
        try {
            manager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } catch (_: SecurityException) {
            manager?.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun cancelScheduledAlarms() {
        val context = applicationContext ?: return
        val existingIntent =
            PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                Intent(context, GradeNotificationReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            )
        if (existingIntent != null) {
            val manager =
                alarmManager ?: (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.also {
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
        val network = connectivityManager.activeNetwork ?: return true
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return true
        return !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun ensureNotificationServiceInitialized(platformContext: Any?) {
        GradeNotificationNotifier.ensureInitialized(platformContext ?: applicationContext)
    }

    private fun requestExactAlarmPermissionIfNeeded(activity: Activity?) {
        if (activity == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val manager = activity.getSystemService(AlarmManager::class.java) ?: return
        if (!manager.canScheduleExactAlarms()) {
            val intent =
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:${'$'}{activity.packageName}".toUri()
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
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
