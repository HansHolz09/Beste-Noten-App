package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.util.fastFirstOrNull

@Stable
class ScrollSpeedState {
    var pxPerSecond by mutableFloatStateOf(0f)
        internal set
}

@Composable
fun rememberLazyListScrollSpeedState(state: LazyListState): ScrollSpeedState {
    val speedState = remember { ScrollSpeedState() }

    LaunchedEffect(state) {
        var lastFrameNanos = 0L
        var lastIndex = state.firstVisibleItemIndex
        var lastOffset = state.firstVisibleItemScrollOffset
        var lastFirstSize = 0

        while (true) {
            withFrameNanos { now ->
                if (!state.isScrollInProgress) {
                    speedState.pxPerSecond = 0f
                    lastFrameNanos = 0L
                    lastIndex = state.firstVisibleItemIndex
                    lastOffset = state.firstVisibleItemScrollOffset
                    lastFirstSize = state.layoutInfo.visibleItemsInfo
                        .fastFirstOrNull { it.index == lastIndex }
                        ?.size ?: lastFirstSize
                    return@withFrameNanos
                }

                val curIndex = state.firstVisibleItemIndex
                val curOffset = state.firstVisibleItemScrollOffset
                val curFirstSize =
                    state.layoutInfo.visibleItemsInfo
                        .fastFirstOrNull { it.index == curIndex }
                        ?.size ?: lastFirstSize

                if (lastFrameNanos != 0L) {
                    val dtSec = (now - lastFrameNanos) / 1_000_000_000f

                    val deltaPx =
                        when {
                            curIndex == lastIndex -> (curOffset - lastOffset)

                            curIndex > lastIndex -> {
                                val fromLastItem = (lastFirstSize - lastOffset).coerceAtLeast(0)
                                val skipped = (curIndex - lastIndex - 1).coerceAtLeast(0)
                                fromLastItem + (skipped * curFirstSize) + curOffset
                            }

                            else -> {
                                val toCurrentTop = (curFirstSize - curOffset).coerceAtLeast(0)
                                val skipped = (lastIndex - curIndex - 1).coerceAtLeast(0)
                                -(lastOffset + (skipped * curFirstSize) + toCurrentTop)
                            }
                        }.toFloat()

                    val v = deltaPx / dtSec

                    val clamped = v.coerceIn(-120_000f, 120_000f)
                    speedState.pxPerSecond = speedState.pxPerSecond * 0.85f + clamped * 0.15f
                }

                lastFrameNanos = now
                lastIndex = curIndex
                lastOffset = curOffset
                lastFirstSize = curFirstSize
            }
        }
    }

    return speedState
}
