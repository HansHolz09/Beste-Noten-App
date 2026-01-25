package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import top.ltfan.multihaptic.compose.rememberVibrator

@Composable
fun rememberEnhancedPagerState(
    pageCount: Int,
    initialPage: Int = 0,
): PagerState {
    val vibrator = rememberVibrator()

    val pagerState = rememberPagerState(initialPage) { pageCount }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .drop(1)
            .distinctUntilChanged()
            .collect {
                vibrator.enhancedVibrate(EnhancedVibrations.SPIN)
            }
    }

    return pagerState
}
