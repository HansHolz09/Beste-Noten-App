package com.hansholz.bestenotenapp.utils

import com.hansholz.bestenotenapp.api.models.JournalLesson
import com.hansholz.bestenotenapp.screens.timetable.homeworkLessonId

internal data class TimetableLessonBlock(
    val lesson: JournalLesson,
    val sourceLessons: List<JournalLesson>,
) {
    val stableKey: String = sourceLessons.joinToString("|") { it.homeworkLessonId() ?: "${it.nr}:${it.time?.from}-${it.time?.to}" }
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
        notes.sameElementsAs(next.notes)
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
