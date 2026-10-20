package com.hansholz.bestenotenapp.reviews

import com.hansholz.bestenotenapp.security.KSafeProvider
import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

internal class StoreReviewManager(
    kSafe: KSafe,
    private val scope: CoroutineScope,
    private val canRequest: () -> Boolean,
) {
    private val storage = KSafeProvider(kSafe)
    private var pendingRequest: Job? = null

    fun recordUsage() {
        if (StoreReview.isSupported && storage.get("storeReviewNextRequestAt", 0L) == 0L) {
            storage.put("storeReviewNextRequestAt", Clock.System.now().toEpochMilliseconds() + 1.days.inWholeMilliseconds)
        }
    }

    fun cancelPending() {
        pendingRequest?.cancel()
        pendingRequest = null
    }

    fun completedAction() {
        cancelPending()
        if (!StoreReview.isSupported || !isDue()) return
        pendingRequest =
            scope.launch {
                delay(1500.milliseconds)
                if (canRequest() && isDue() && StoreReview.requestReview()) {
                    val count = storage.get("storeReviewRequestCount", 0) + 1
                    storage.put("storeReviewRequestCount", count)
                    storage.put(
                        "storeReviewNextRequestAt",
                        Clock.System.now().toEpochMilliseconds() + (if (count == 1) 7.days else 365.days).inWholeMilliseconds,
                    )
                }
            }
    }

    private fun isDue(): Boolean {
        val nextRequestAt = storage.get("storeReviewNextRequestAt", 0L)
        return storage.get("storeReviewRequestCount", 0) < 3 && nextRequestAt > 0L &&
            Clock.System.now().toEpochMilliseconds() >= nextRequestAt
    }

    fun openReviewPage() {
        cancelPending()
        if (!StoreReview.isSupported) return
        StoreReview.openReviewPage()
        storage.put("storeReviewRequestCount", 3)
    }
}
