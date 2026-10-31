package com.hansholz.bestenotenapp.updates

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.hansholz.bestenotenapp.main.isStoreDistribution
import com.hansholz.bestenotenapp.reviews.StoreReviewActivity
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.context

internal actual object StoreUpdate {
    private val manager by lazy { AppUpdateManagerFactory.create(FileKit.context) }
    private var updateInfo: AppUpdateInfo? = null
    private var listener: InstallStateUpdatedListener? = null

    actual suspend fun checkForUpdate(onDownloaded: () -> Unit): StoreUpdateStatus {
        if (!isStoreDistribution()) return StoreUpdateStatus.None
        val info = manager.requestAppUpdateInfo()
        if (info.installStatus() == InstallStatus.DOWNLOADED) return StoreUpdateStatus.Downloaded
        val downloading = info.installStatus() == InstallStatus.PENDING || info.installStatus() == InstallStatus.DOWNLOADING
        val available = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
        if (!downloading && !available) return StoreUpdateStatus.None

        listener?.let(manager::unregisterListener)
        listener =
            InstallStateUpdatedListener { state ->
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    listener?.let(manager::unregisterListener)
                    listener = null
                    onDownloaded()
                }
            }.also(manager::registerListener)
        updateInfo = info
        return if (available) StoreUpdateStatus.Available else StoreUpdateStatus.None
    }

    actual fun startUpdate() {
        val info = updateInfo ?: return
        val activity = StoreReviewActivity.activityRef.get() ?: return
        if (activity.isFinishing || activity.isDestroyed) return
        updateInfo = null
        manager.startUpdateFlow(info, activity, AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE))
    }

    actual fun completeUpdate() {
        manager.completeUpdate()
    }
}
