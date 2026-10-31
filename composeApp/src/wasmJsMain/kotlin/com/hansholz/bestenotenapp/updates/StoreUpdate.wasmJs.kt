package com.hansholz.bestenotenapp.updates

internal actual object StoreUpdate {
    actual suspend fun checkForUpdate(onDownloaded: () -> Unit) = StoreUpdateStatus.None

    actual fun startUpdate() = Unit

    actual fun completeUpdate() = Unit
}
