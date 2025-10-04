package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop

@Composable
fun rememberEnhancedPagerState(
    pageCount: Int,
    initialPage: Int = 0,
): PagerState {
    val hapticFeedback = LocalHapticFeedback.current

    val pagerState = rememberPagerState(initialPage) { pageCount }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .drop(1)
            .distinctUntilChanged()
            .collect {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
            }
    }

    return pagerState
}
