package com.hansholz.bestenotenapp.utils

import com.hansholz.bestenotenapp.api.models.JournalLesson
import com.hansholz.bestenotenapp.screens.timetable.homeworkLessonId

internal data class TimetableLessonBlock(
    val lesson: JournalLesson,
    val sourceLessons: List<JournalLesson>,
) {
    val stableKey: String = sourceLessons.joinToString("|") { it.homeworkLessonId() ?: "${it.nr}:${it.time?.from}-${it.time?.to}" }
}

internal sealed interface SchoolDayEntry {
    data class Lessons(
        val blocks: List<TimetableLessonBlock>,
        val from: SimpleTime?,
        val to: SimpleTime?,
    ) : SchoolDayEntry {
        val sourceLessons: List<JournalLesson> = blocks.flatMap(TimetableLessonBlock::sourceLessons)
        val lessonCount: Int = blocks.maxOf { it.sourceLessons.size }
    }

    data class Break(
        val from: SimpleTime,
        val to: SimpleTime,
    ) : SchoolDayEntry
}

internal fun createSchoolDayEntries(
    lessons: List<JournalLesson>,
    mergeLessons: Boolean,
    timetableBlockViewEnabled: Boolean,
): List<SchoolDayEntry> {
    val timetableBlocks = createTimetableLessonBlocks(lessons, timetableBlockViewEnabled)
    val groups =
        createTimetableLessonBlocks(lessons, mergeLessons).groupBy { it.lesson.nr }.values.map { blocks ->
            val times =
                timetableBlocks
                    .filter { block -> block.sourceLessons.any { lesson -> blocks.any { lesson in it.sourceLessons } } }
                    .mapNotNull { it.lesson.time }
            SchoolDayEntry.Lessons(
                blocks = blocks,
                from = times.mapNotNull { it.from?.let(SimpleTime::parse) }.minOrNull(),
                to = times.mapNotNull { it.to?.let(SimpleTime::parse) }.maxOrNull(),
            )
        }

    return groups.flatMapIndexed { index, group ->
        val breakFrom = groups.getOrNull(index - 1)?.to
        val breakTo = group.from
        if (breakFrom != null && breakTo != null && breakFrom.minutesUntil(breakTo) > 45) {
            listOf(SchoolDayEntry.Break(breakFrom, breakTo), group)
        } else {
            listOf(group)
        }
    }
}

internal fun createTimetableLessonBlocks(
    lessons: List<JournalLesson>,
    enabled: Boolean,
): List<TimetableLessonBlock> {
    val sortedLessons = lessons.sortedWith(compareBy({ it.time?.from }, { it.nr.toIntOrNull() }, { it.nr }))
    if (!enabled) return sortedLessons.map { TimetableLessonBlock(it, listOf(it)) }

    val groups = mutableListOf<MutableList<JournalLesson>>()
    sortedLessons.forEach { lesson ->
        val currentGroup = groups.lastOrNull()
        if (currentGroup != null && currentGroup.last().canJoinBlockWith(lesson)) {
            currentGroup += lesson
        } else {
            groups += mutableListOf(lesson)
        }
    }

    return groups.map { sourceLessons ->
        TimetableLessonBlock(
            lesson = sourceLessons.toBlockLesson(),
            sourceLessons = sourceLessons,
        )
    }
}

private fun JournalLesson.canJoinBlockWith(next: JournalLesson): Boolean {
    val end = time?.to?.let(SimpleTime::parse) ?: return false
    val nextStart = next.time?.from?.let(SimpleTime::parse) ?: return false
    if (nextStart < end) return false

    val lessonNumber = nr.toIntOrNull()
    val nextLessonNumber = next.nr.toIntOrNull()
    if (lessonNumber != null && nextLessonNumber != null && nextLessonNumber != lessonNumber + 1) return false

    return group?.id == next.group?.id &&
        teachers
            .orEmpty()
            .map { it.id?.let { id -> "id:$id" } ?: it.localId?.let { localId -> "local:$localId" } ?: "name:${it.forename}:${it.name}" }
            .sameElementsAs(
                next.teachers
                    .orEmpty()
                    .map { it.id?.let { id -> "id:$id" } ?: it.localId?.let { localId -> "local:$localId" } ?: "name:${it.forename}:${it.name}" },
            ) &&
        rooms.orEmpty().map { it.id }.sameElementsAs(next.rooms.orEmpty().map { it.id }) &&
        notes?.map { it.description }.sameElementsAs(next.notes?.map { it.description })
}

private fun List<JournalLesson>.toBlockLesson(): JournalLesson {
    val first = first()
    if (size == 1) return first

    val firstTime = first.time ?: return first
    val firstStart = SimpleTime.parse(firstTime.from ?: return first)
    val teachingMinutes =
        sumOf { lesson ->
            val lessonTime = lesson.time ?: return@sumOf 0
            val start = SimpleTime.parse(lessonTime.from ?: return@sumOf 0)
            val end = SimpleTime.parse(lessonTime.to ?: return@sumOf 0)
            start.minutesUntil(end).coerceAtLeast(0)
        }

    return first.copy(
        nr = "${first.nr}–${last().nr}",
        nrs = joinToString(",") { it.nr },
        times = flatMap { it.times ?: listOfNotNull(it.time) },
        time = firstTime.copy(to = firstStart.plusMinutes(teachingMinutes).toString()),
    )
}

private fun <T> List<T>?.sameElementsAs(other: List<T>?): Boolean = orEmpty().groupingBy { it }.eachCount() == other.orEmpty().groupingBy { it }.eachCount()
