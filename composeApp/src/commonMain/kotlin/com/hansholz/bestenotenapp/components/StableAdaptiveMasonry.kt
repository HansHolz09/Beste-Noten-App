package com.hansholz.bestenotenapp.components

import androidx.compose.animation.animateBounds
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp

@Composable
fun StableAdaptiveMasonry(
    minColumnWidth: androidx.compose.ui.unit.Dp,
    state: StableMasonryState,
    lanePolicy: MasonryLanePolicy,
    modifier: Modifier = Modifier,
    horizontalSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    verticalSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()
        val minColumnWidthPx = minColumnWidth.roundToPx().coerceAtLeast(1)
        val availableWidth = constraints.maxWidth.coerceAtLeast(1)

        val columnCount =
            ((availableWidth + horizontalSpacingPx) / (minColumnWidthPx + horizontalSpacingPx))
                .coerceAtLeast(1)

        val laneByKey = state.laneByKey
        if (state.columnCount != columnCount) {
            state.columnCount = columnCount
            laneByKey.clear()
        }

        val totalHorizontalSpacing = horizontalSpacingPx * (columnCount - 1)
        val baseColumnWidth = (availableWidth - totalHorizontalSpacing) / columnCount
        val extraPixels = (availableWidth - totalHorizontalSpacing) % columnCount
        val columnWidths = IntArray(columnCount) { index -> baseColumnWidth + if (index < extraPixels) 1 else 0 }
        val columnX = IntArray(columnCount)
        for (index in 1 until columnCount) {
            columnX[index] = columnX[index - 1] + columnWidths[index - 1] + horizontalSpacingPx
        }

        val laneHeights = IntArray(columnCount)
        val placeables = ArrayList<androidx.compose.ui.layout.Placeable>(measurables.size)
        val xPositions = IntArray(measurables.size)
        val yPositions = IntArray(measurables.size)

        for (index in measurables.indices) {
            val measurable = measurables[index]
            val key = measurable.layoutId?.toString() ?: "masonry-$index"
            val lane =
                when (lanePolicy) {
                    MasonryLanePolicy.RoundRobin -> {
                        index % columnCount
                    }

                    MasonryLanePolicy.Rebalanced -> {
                        laneHeights.indices.minBy { laneHeights[it] }
                    }

                    MasonryLanePolicy.StableBalanced -> {
                        laneByKey[key]
                            ?.takeIf { it in 0 until columnCount }
                            ?: laneHeights.indices.minBy { laneHeights[it] }.also { laneByKey[key] = it }
                    }
                }

            val placeable =
                measurable.measure(
                    constraints.copy(
                        minWidth = columnWidths[lane],
                        maxWidth = columnWidths[lane],
                        minHeight = 0,
                        maxHeight = androidx.compose.ui.unit.Constraints.Infinity,
                    ),
                )

            placeables += placeable
            xPositions[index] = columnX[lane]
            yPositions[index] = laneHeights[lane]
            laneHeights[lane] += placeable.height + verticalSpacingPx
        }

        if (!isLookingAhead && columnCount > 1) {
            state.itemHeights =
                measurables.indices.associate { index ->
                    val key = measurables[index].layoutId?.toString() ?: "masonry-$index"
                    key to (placeables[index].height + verticalSpacingPx)
                }
        }

        val contentHeight =
            laneHeights
                .maxOrNull()
                ?.let { if (it > 0) it - verticalSpacingPx else 0 }
                ?: 0

        layout(
            width = availableWidth,
            height = contentHeight.coerceAtLeast(constraints.minHeight),
        ) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(x = xPositions[index], y = yPositions[index])
            }
        }
    }
}

@Composable
fun MasonryItem(
    key: String,
    lookaheadScope: LookaheadScope,
    animationsEnabled: Boolean,
    content: @Composable MasonryItemScope.() -> Unit,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val itemScope = remember(bringIntoViewRequester) { MasonryItemScope(bringIntoViewRequester) }

    Box(
        modifier =
            Modifier
                .layoutId(key)
                .bringIntoViewRequester(bringIntoViewRequester)
                .then(
                    if (animationsEnabled) {
                        Modifier.animateBounds(lookaheadScope = lookaheadScope)
                    } else {
                        Modifier
                    },
                ),
    ) {
        itemScope.content()
    }
}

class InsetAwareBringIntoViewSpec(
    private val topInsetPx: Float,
    private val bottomInsetPx: Float,
) : BringIntoViewSpec {
    override fun calculateScrollDistance(
        offset: Float,
        size: Float,
        containerSize: Float,
    ): Float {
        val visibleStart = topInsetPx.coerceIn(0f, containerSize)
        val visibleEnd = (containerSize - bottomInsetPx).coerceIn(visibleStart, containerSize)
        val visibleSize = visibleEnd - visibleStart
        val itemEnd = offset + size

        return when {
            size > visibleSize -> offset - visibleStart
            offset < visibleStart -> offset - visibleStart
            itemEnd > visibleEnd -> itemEnd - visibleEnd
            else -> 0f
        }
    }
}

class MasonryItemScope(
    private val bringIntoViewRequester: BringIntoViewRequester,
) {
    suspend fun bringIntoView() {
        bringIntoViewRequester.bringIntoView()
    }
}

enum class MasonryLanePolicy {
    StableBalanced,
    Rebalanced,
    RoundRobin,
}

class StableMasonryState {
    var columnCount: Int = 0
    var laneByKey by mutableStateOf(mutableMapOf<String, Int>())
        private set
    internal var itemHeights by mutableStateOf(emptyMap<String, Int>())

    fun rebalance(
        fixedKeys: Set<String> = emptySet(),
        preferredKeys: Set<String> = emptySet(),
    ) {
        if (columnCount <= 1 || itemHeights.isEmpty()) return

        fun balance(preservedKeys: Set<String>): Pair<MutableMap<String, Int>, Int> {
            val laneHeights = IntArray(columnCount)
            val lanes = mutableMapOf<String, Int>()
            for ((key, height) in itemHeights) {
                val lane =
                    laneByKey[key]
                        ?.takeIf { key in preservedKeys && it in laneHeights.indices }
                        ?: laneHeights.indices.minBy { laneHeights[it] }
                lanes[key] = lane
                laneHeights[lane] += height
            }
            return lanes to laneHeights.max()
        }

        val stable = balance(fixedKeys + preferredKeys)
        val relaxed = if (preferredKeys.isEmpty()) stable else balance(fixedKeys)
        laneByKey = if (stable.second > relaxed.second * 1.2f) relaxed.first else stable.first
    }
}
