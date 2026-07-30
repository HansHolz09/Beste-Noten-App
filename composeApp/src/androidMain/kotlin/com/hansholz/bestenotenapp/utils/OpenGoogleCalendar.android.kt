package com.hansholz.bestenotenapp.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.context

private const val CALENDAR_PACKAGE = "com.google.android.calendar"

actual fun openGoogleCalendar() {
    val context = FileKit.context
    val launchIntent = context.packageManager.getLaunchIntentForPackage(CALENDAR_PACKAGE)
    if (launchIntent != null) {
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
    } else {
        openPlayStore(context, CALENDAR_PACKAGE)
    }
}

@Suppress("SameParameterValue")
private fun openPlayStore(
    context: Context,
    packageName: String,
) {
    val marketIntent =
        Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(marketIntent)
    } catch (_: ActivityNotFoundException) {
        val webIntent =
            Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri(),
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(webIntent)
    }
}
