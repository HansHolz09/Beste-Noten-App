package com.hansholz.bestenotenapp.reviews

internal expect object StoreReview {
    val isSupported: Boolean

    fun requestReview(): Boolean

    fun openReviewPage()
}
