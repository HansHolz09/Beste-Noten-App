package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Text_snippet
import com.hansholz.bestenotenapp.api.models.Absence
import com.hansholz.bestenotenapp.api.models.JournalLesson
import com.hansholz.bestenotenapp.components.cupertinoHighlight
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedBounds
import com.hansholz.bestenotenapp.theme.LocalThemeIsDark
import com.hansholz.bestenotenapp.utils.SimpleTime
import com.hansholz.bestenotenapp.utils.TimetableLessonBlock
import kotlinx.datetime.LocalDate
import kotlin.math.min

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun DailyScheduleLayout(
    lessonBlocks: List<TimetableLessonBlock>,
    absences: List<Absence>,
    date: LocalDate,
    modifier: Modifier = Modifier,
    captureOnly: Boolean,
    minTime: SimpleTime,
    maxTime: SimpleTime,
    sharedTransitionScope: SharedTransitionScope,
    selectedLesson: JournalLesson?,
    popupTransition: Transition<Boolean>,
    homeworkBlockKeys: Set<String> = emptySet(),
    doneHomeworkBlockKeys: Set<String> = emptySet(),
    onLessonPopupOpened: (TimetableLessonBlock) -> Unit,
) {
    val preparedLessons =
        remember(lessonBlocks) {
            lessonBlocks
                .mapIndexed { index, block ->
                    val lesson = block.lesson
                    val fallbackStart = SimpleTime.parse("07:30").plus(50 * index)
                    val normalized =
                        if (lesson.time?.from == null || lesson.time.to == null) {
                            lesson.copy(
                                time =
                                    lesson.time?.copy(
                                        from = fallbackStart.toString(),
                                        to = fallbackStart.plus(45).toString(),
                                    ),
                            )
                        } else {
                            lesson
                        }

                    PreparedLesson(
                        block = block,
                        lesson = normalized,
                        start = SimpleTime.parse(normalized.time?.from ?: "00:00"),
                        end = SimpleTime.parse(normalized.time?.to ?: "00:00"),
                    )
                }
        }

    val parsedAbsences =
        remember(absences) {
            absences.mapNotNull { absence ->
                runCatching {
                    ParsedAbsence(
                        fromDate = LocalDate.parse(absence.from.take(10)),
                        toDate = LocalDate.parse(absence.to.take(10)),
                        fromTime = SimpleTime.parse(absence.from.takeLast(8)),
                        toTime = SimpleTime.parse(absence.to.takeLast(8)),
                    )
                }.getOrNull()
            }
        }

    val absentLessonIndices =
        remember(preparedLessons, parsedAbsences, date, captureOnly) {
            if (captureOnly) {
                emptySet()
            } else {
                preparedLessons
                    .mapIndexedNotNull { index, prepared ->
                        index.takeIf {
                            parsedAbsences.any { absence ->
                                absence.fromDate <= date &&
                                    absence.toDate >= date &&
                                    absence.fromTime <= prepared.start &&
                                    absence.toTime >= prepared.end
                            }
                        }
                    }.toSet()
            }
        }

    val textMeasurer = rememberTextMeasurer(cacheSize = 64)
    val referenceTextSizes =
        remember(preparedLessons, textMeasurer) {
            preparedLessons.map { prepared ->
                textMeasurer
                    .measure(
                        text = prepared.lesson.subject?.localId ?: "?",
                        style = TextStyle(fontSize = 100.sp, fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        softWrap = false,
                    ).size
            }
        }

    val isDark = LocalThemeIsDark.current

    with(sharedTransitionScope) {
        SubcomposeLayout(modifier = modifier) { constraints ->
            val width = constraints.maxWidth.coerceAtLeast(0)
            val height = constraints.maxHeight.coerceAtLeast(0)
            val totalMinutes = minTime.minutesUntil(maxTime)

            if (width == 0 || height == 0 || totalMinutes <= 0 || preparedLessons.isEmpty()) {
                return@SubcomposeLayout layout(width, height) {}
            }

            val placements =
                calculateLessonPlacements(
                    lessons = preparedLessons,
                    minTime = minTime,
                    totalMinutes = totalMinutes,
                    width = width,
                    height = height,
                )

            val items =
                preparedLessons.mapIndexedNotNull { index, prepared ->
                    val placement = placements[index] ?: return@mapIndexedNotNull null
                    if (placement.width <= 0 || placement.height <= 0) return@mapIndexedNotNull null

                    val lesson = prepared.lesson
                    val subject = lesson.subject?.localId ?: "?"
                    val contentWidth =
                        (placement.width - 4.dp.roundToPx() * 2 - 5.dp.roundToPx() * 2)
                            .coerceAtLeast(1)
                    val contentHeight = (placement.height - 4.dp.roundToPx() * 2).coerceAtLeast(1)
                    val rotate = contentHeight >= contentWidth * 1.35f && placement.width <= 100 && lesson.subject?.localId != null
                    val widthScale = (if (rotate) contentHeight else contentWidth).toFloat() / referenceTextSizes[index].width.coerceAtLeast(1)
                    val heightScale = (if (rotate) contentWidth else contentHeight).toFloat() / referenceTextSizes[index].height.coerceAtLeast(1)
                    val fontSize =
                        (100.sp.toPx() * min(widthScale, heightScale) * 0.9f)
                            .coerceIn(8.sp.toPx(), 38.sp.toPx())
                            .toSp()
                    val homeworkDone = prepared.block.stableKey in doneHomeworkBlockKeys
                    val hasHomework = prepared.block.stableKey in homeworkBlockKeys && !captureOnly
                    val isAbsent = index in absentLessonIndices

                    val measurable =
                        subcompose(LessonSlotKey(index, prepared.stableKey)) {
                            Box {
                                popupTransition.AnimatedVisibility(
                                    visible = { popupShown -> selectedLesson != lesson || !popupShown },
                                    enter = EnterTransition.None,
                                    exit = ExitTransition.None,
                                ) {
                                    val cardShape = RoundedCornerShape(18.dp)
                                    val interactionSource = remember(lesson) { MutableInteractionSource() }
                                    OutlinedCard(
                                        onClick = { onLessonPopupOpened(prepared.block.copy(lesson = lesson)) },
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 4.dp)
                                                .cupertinoHighlight(interactionSource, cardShape)
                                                .enhancedSharedBounds(
                                                    sharedTransitionScope = sharedTransitionScope,
                                                    sharedContentState = rememberSharedContentState(lesson),
                                                    animatedVisibilityScope = this@AnimatedVisibility,
                                                    enter = fadeIn(initialAlpha = if (selectedLesson == lesson) 0f else 1f),
                                                    exit = fadeOut(targetAlpha = if (selectedLesson == lesson) 0f else 1f),
                                                    boundsTransform = { _, _ ->
                                                        spring(0.8f, Spring.StiffnessMediumLow)
                                                    },
                                                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.Fit),
                                                    renderInOverlayDuringTransition = selectedLesson == lesson,
                                                ),
                                        shape = cardShape,
                                        colors =
                                            CardDefaults.outlinedCardColors(
                                                containerColor =
                                                    when (lesson.status) {
                                                        "hold" -> if (isDark) Color(48, 99, 57) else Color(226, 251, 232)
                                                        "canceled" -> colorScheme.errorContainer
                                                        "initial" -> if (isDark) Color.DarkGray else Color.LightGray
                                                        "planned" -> if (isDark) Color(38, 63, 168) else Color(160, 182, 238)
                                                        else -> colorScheme.surface
                                                    }.copy(0.7f),
                                            ),
                                        border =
                                            BorderStroke(
                                                width = 2.dp,
                                                color =
                                                    if (lesson.notes.isNullOrEmpty()) {
                                                        colorScheme.outline
                                                    } else {
                                                        lesson.notes
                                                            .firstOrNull()
                                                            ?.type
                                                            ?.color
                                                            ?.let { Color(it.removePrefix("#").toLong(16) or 0x00000000FF000000) }
                                                            ?: if (!isDark) Color(38, 63, 168) else Color(222, 233, 252)
                                                    },
                                            ),
                                        interactionSource = interactionSource,
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (hasHomework) {
                                                Icon(
                                                    imageVector = MaterialSymbols.Rounded.Text_snippet,
                                                    contentDescription = null,
                                                    modifier =
                                                        Modifier
                                                            .graphicsLayer { scaleX = -1f }
                                                            .padding(3.dp)
                                                            .align(Alignment.TopStart)
                                                            .size(25.dp)
                                                            .alpha(if (homeworkDone) 0.18f else 0.55f),
                                                    tint = if (homeworkDone) colorScheme.onSurfaceVariant else colorScheme.error,
                                                )
                                            }

                                            Layout(
                                                content = {
                                                    Text(
                                                        text = subject,
                                                        fontSize = fontSize,
                                                        maxLines = 1,
                                                        softWrap = false,
                                                        color = if (isAbsent) colorScheme.error else Color.Unspecified,
                                                    )
                                                },
                                                modifier = Modifier.fillMaxSize().padding(3.dp, 6.dp),
                                            ) { measurables, textConstraints ->
                                                val childConstraints =
                                                    if (rotate) {
                                                        Constraints(
                                                            maxWidth = textConstraints.maxHeight,
                                                            maxHeight = textConstraints.maxWidth,
                                                        )
                                                    } else {
                                                        Constraints(
                                                            maxWidth = textConstraints.maxWidth,
                                                            maxHeight = textConstraints.maxHeight,
                                                        )
                                                    }
                                                val placeable = measurables.single().measure(childConstraints)

                                                layout(textConstraints.maxWidth, textConstraints.maxHeight) {
                                                    placeable.placeWithLayer(
                                                        x = (textConstraints.maxWidth - placeable.width) / 2,
                                                        y = (textConstraints.maxHeight - placeable.height) / 2,
                                                    ) {
                                                        rotationZ = if (rotate) -90f else 0f
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }.single()

                    MeasuredLesson(
                        placement = placement,
                        placeable = measurable.measure(Constraints.fixed(placement.width, placement.height)),
                    )
                }

            layout(width, height) {
                items.forEach { item ->
                    item.placeable.placeRelative(item.placement.x, item.placement.y)
                }
            }
        }
    }
}

private fun calculateLessonPlacements(
    lessons: List<PreparedLesson>,
    minTime: SimpleTime,
    totalMinutes: Long,
    width: Int,
    height: Int,
): Map<Int, LessonPlacementInfo> {
    val result = HashMap<Int, LessonPlacementInfo>(lessons.size)
    val indexed = lessons.indices.sortedWith(compareBy({ lessons[it].start }, { lessons[it].end }))
    var groupStart = 0

    while (groupStart < indexed.size) {
        var groupEnd = groupStart + 1
        var latestEnd = lessons[indexed[groupStart]].end

        while (groupEnd < indexed.size && lessons[indexed[groupEnd]].start < latestEnd) {
            if (lessons[indexed[groupEnd]].end > latestEnd) latestEnd = lessons[indexed[groupEnd]].end
            groupEnd++
        }

        val group = indexed.subList(groupStart, groupEnd)
        val columnEnds = mutableListOf<SimpleTime>()
        val columns = HashMap<Int, Int>(group.size)

        group.forEach { lessonIndex ->
            val lesson = lessons[lessonIndex]
            val freeColumn = columnEnds.indexOfFirst { it <= lesson.start }
            val column =
                if (freeColumn >= 0) {
                    columnEnds[freeColumn] = lesson.end
                    freeColumn
                } else {
                    columnEnds += lesson.end
                    columnEnds.lastIndex
                }
            columns[lessonIndex] = column
        }

        val columnCount = columnEnds.size.coerceAtLeast(1)
        group.forEach { lessonIndex ->
            val lesson = lessons[lessonIndex]
            val column = columns.getValue(lessonIndex)
            val left = (column.toLong() * width / columnCount).toInt()
            val right = ((column + 1L) * width / columnCount).toInt()
            val top = ((minTime.minutesUntil(lesson.start) * height) / totalMinutes).toInt()
            val bottom = ((minTime.minutesUntil(lesson.end) * height) / totalMinutes).toInt()

            result[lessonIndex] =
                LessonPlacementInfo(
                    x = left,
                    y = top.coerceIn(0, height),
                    width = (right - left).coerceAtLeast(1),
                    height = (bottom - top).coerceAtLeast(1),
                )
        }

        groupStart = groupEnd
    }

    return result
}

private data class PreparedLesson(
    val block: TimetableLessonBlock,
    val lesson: JournalLesson,
    val start: SimpleTime,
    val end: SimpleTime,
) {
    val stableKey: String = block.stableKey
}

private data class ParsedAbsence(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val fromTime: SimpleTime,
    val toTime: SimpleTime,
)

private data class LessonSlotKey(
    val index: Int,
    val lessonKey: String,
)

private data class LessonPlacementInfo(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
)

private data class MeasuredLesson(
    val placement: LessonPlacementInfo,
    val placeable: Placeable,
)

fun JournalLesson.homeworkLessonId(): String? = time?.id ?: id ?: ids
