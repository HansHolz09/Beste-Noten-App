package com.hansholz.bestenotenapp.utils

import com.hansholz.bestenotenapp.api.models.Group
import com.hansholz.bestenotenapp.api.models.JournalDay
import com.hansholz.bestenotenapp.api.models.JournalLesson
import com.hansholz.bestenotenapp.api.models.JournalWeek

fun JournalWeek.withRelevantLessons(groupsByYear: Map<Int, List<Group>>): JournalWeek = copy(days = days?.map { it.withRelevantLessons(groupsByYear) })

fun JournalDay.withRelevantLessons(groupsByYear: Map<Int, List<Group>>): JournalDay {
    val relevantLessons = lessons?.filter { it.group.isRelevant(groupsByYear) } ?: return this
    val lessonsByTimeRange =
        relevantLessons
            .mapNotNull { lesson -> lesson.timeRange()?.let { it to lesson } }
            .groupBy({ it.first }, { it.second })

    return copy(
        lessons =
            relevantLessons.filterNot { lesson ->
                val simultaneousLessons = lesson.timeRange()?.let(lessonsByTimeRange::get).orEmpty()
                lesson.status == "canceled" && simultaneousLessons.any { it.status != "canceled" }
            },
    )
}

private fun Group?.isRelevant(groupsByYear: Map<Int, List<Group>>): Boolean = this == null || groupsByYear[yearId]?.any { it.id == id } == true

private fun JournalLesson.timeRange(): Pair<String, String>? {
    val lessonTime = time ?: times?.singleOrNull() ?: return null
    return lessonTime.from?.let { from -> lessonTime.to?.let { to -> from to to } }
}
