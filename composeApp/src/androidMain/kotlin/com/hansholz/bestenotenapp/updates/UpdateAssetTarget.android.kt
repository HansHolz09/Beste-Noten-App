package com.hansholz.bestenotenapp.updates

internal actual fun updateAssetTarget(): UpdateAssetTarget? =
    UpdateAssetTarget(
        platform = "android",
        extension = ".apk",
    )
