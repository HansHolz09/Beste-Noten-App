package com.hansholz.bestenotenapp.reviews

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.core.net.toUri
import com.google.android.play.core.review.ReviewManagerFactory
import com.hansholz.bestenotenapp.main.isStoreDistribution
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.context
import java.lang.ref.WeakReference

object StoreReviewActivity {
    internal var activityRef: WeakReference<Activity?> = WeakReference(null)

    fun register(activity: Activity) {
        activityRef = WeakReference(activity)
    }
}

internal actual object StoreReview {
    actual val isSupported get() = isStoreDistribution()

    actual fun requestReview(): Boolean {
        val activity = StoreReviewActivity.activityRef.get() ?: return false
        if (!isSupported || activity.isFinishing || activity.isDestroyed) return false
        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow().addOnCompleteListener { request ->
            if (request.isSuccessful && !activity.isFinishing && !activity.isDestroyed) manager.launchReviewFlow(activity, request.result)
        }
        return true
    }

    actual fun openReviewPage() {
        val context = FileKit.context
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, "market://details?id=${context.packageName}".toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }
}
