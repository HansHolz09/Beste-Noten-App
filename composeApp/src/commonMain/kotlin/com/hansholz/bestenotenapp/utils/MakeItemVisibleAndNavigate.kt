package com.hansholz.bestenotenapp.utils

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState

suspend fun makeItemVisibleAndNavigate(
    listState: LazyStaggeredGridState,
    index: Int,
    onNavigate: () -> Unit,
) {
    val firstVisibleIndex = listState.firstVisibleItemIndex
    val firstVisibleOffset = listState.firstVisibleItemScrollOffset

    val needsScroll = index == firstVisibleIndex && firstVisibleOffset > 0
    if (needsScroll) {
        listState.animateScrollToItem(index)
    }

    onNavigate()
}
