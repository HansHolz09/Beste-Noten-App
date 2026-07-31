package com.hansholz.bestenotenapp.homework

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

fun HomeworkEntry.toGoogleCalendarEvent(studentId: String?): GoogleCalendarEvent {
    val timeZone = TimeZone.currentSystemDefault()
    val start =
        if (dueDateTime == null) {
            GoogleCalendarEventDateTime(date = dueDate.toString())
        } else {
            GoogleCalendarEventDateTime(dateTime = dueDateTime.toString(), timeZone = timeZone.id)
        }
    val end =
        if (dueDateTime == null) {
            GoogleCalendarEventDateTime(date = dueDate.plus(DatePeriod(days = 1)).toString())
        } else {
            GoogleCalendarEventDateTime(dateTime = dueDateTime.plusOneHour(timeZone).toString(), timeZone = timeZone.id)
        }
    return GoogleCalendarEvent(
        summary = eventSummary(),
        description = description,
        start = start,
        end = end,
        transparency = "transparent",
        visibility = "private",
        reminders = toGoogleReminders(timeZone),
        extendedProperties = GoogleCalendarExtendedProperties(privateProperties = toPrivateProperties(studentId)),
    )
}

fun GoogleCalendarEvent.toHomeworkEntryOrNull(): HomeworkEntry? {
    val metadata = extendedProperties?.privateProperties ?: return null
    if (metadata["app"] != "beste-noten-app") return null

    val localId = metadata["localId"] ?: return null
    val remoteUpdatedAt = metadata["appUpdatedAt"]?.parseInstantOrNull() ?: updated?.parseInstantOrNull() ?: return null
    val dueDateTime = start?.dateTime?.parseGoogleDateTimeOrNull()
    val dueDate = start?.date?.let(LocalDate::parse) ?: dueDateTime?.date ?: metadata["dueDate"]?.let(LocalDate::parse) ?: return null
    val status =
        if (status == "cancelled") {
            HomeworkStatus.DELETED
        } else {
            metadata["status"]?.enumValueOrDefault(HomeworkStatus.OPEN) ?: HomeworkStatus.OPEN
        }
    val deletedAt = if (status == HomeworkStatus.DELETED) metadata["deletedAt"]?.parseInstantOrNull() ?: remoteUpdatedAt else null
    return HomeworkEntry(
        localId = localId,
        title = metadata["title"] ?: summary.orEmpty(),
        description = description ?: metadata["description"],
        type = metadata["type"]?.enumValueOrDefault(HomeworkType.HOMEWORK) ?: HomeworkType.HOMEWORK,
        status = status,
        placement = metadata["placement"]?.enumValueOrDefault(HomeworkPlacement.DAY) ?: HomeworkPlacement.DAY,
        dueDate = dueDate,
        dueDateTime = dueDateTime,
        subjectId = metadata["subjectId"],
        subjectName = metadata["subjectName"],
        teacherId = metadata["teacherId"],
        roomId = metadata["roomId"],
        timetableId = metadata["timetableId"],
        timetableTimeId = metadata["timetableTimeId"],
        timetableTimeLessonId = metadata["timetableTimeLessonId"],
        lessonNumber = metadata["lessonNumber"]?.toIntOrNull(),
        weekday = metadata["weekday"]?.toIntOrNull(),
        sourceDate = metadata["sourceDate"]?.let(LocalDate::parse),
        source = metadata["source"]?.enumValueOrDefault(HomeworkSource.USER) ?: HomeworkSource.USER,
        createdAt = metadata["createdAt"]?.parseInstantOrNull() ?: remoteUpdatedAt,
        updatedAt = remoteUpdatedAt,
        deletedAt = deletedAt,
        reminderAt = metadata["reminderAt"]?.let(LocalDateTime::parse),
    )
}

fun remoteUpdatedAt(event: GoogleCalendarEvent): Instant? {
    val metadata = event.extendedProperties?.privateProperties.orEmpty()
    return metadata["appUpdatedAt"]?.parseInstantOrNull() ?: event.updated?.parseInstantOrNull()
}

fun newestWins(
    local: HomeworkEntry,
    remote: GoogleCalendarEvent,
): ConflictWinner {
    val remoteUpdatedAt = remoteUpdatedAt(remote) ?: return ConflictWinner.LOCAL
    return if (remoteUpdatedAt > local.updatedAt) ConflictWinner.REMOTE else ConflictWinner.LOCAL
}

enum class ConflictWinner {
    LOCAL,
    REMOTE,
}

private fun HomeworkEntry.eventSummary(): String =
    if (subjectName.isNullOrBlank()) {
        title
    } else {
        "$subjectName: $title"
    }

private fun HomeworkEntry.toPrivateProperties(studentId: String?): Map<String, String> =
    buildMap {
        put("app", "beste-noten-app")
        put("schemaVersion", "1")
        put("localId", localId)
        studentId?.let { put("studentId", it) }
        put("title", title)
        description?.let { put("description", it) }
        put("type", type.name)
        put("status", status.name)
        put("placement", placement.name)
        put("source", source.name)
        put("dueDate", dueDate.toString())
        dueDateTime?.let { put("dueDateTime", it.toString()) }
        subjectId?.let { put("subjectId", it) }
        subjectName?.let { put("subjectName", it) }
        teacherId?.let { put("teacherId", it) }
        roomId?.let { put("roomId", it) }
        timetableId?.let { put("timetableId", it) }
        timetableTimeId?.let { put("timetableTimeId", it) }
        timetableTimeLessonId?.let { put("timetableTimeLessonId", it) }
        lessonNumber?.let { put("lessonNumber", it.toString()) }
        weekday?.let { put("weekday", it.toString()) }
        sourceDate?.let { put("sourceDate", it.toString()) }
        put("createdAt", createdAt.toString())
        put("appUpdatedAt", updatedAt.toString())
        deletedAt?.let { put("deletedAt", it.toString()) }
        reminderAt?.let { put("reminderAt", it.toString()) }
    }

private fun HomeworkEntry.toGoogleReminders(timeZone: TimeZone): GoogleCalendarReminders {
    val reminder = reminderAt ?: return GoogleCalendarReminders(useDefault = false)
    val start = dueDateTime ?: LocalDateTime(dueDate, LocalTime(0, 0))
    val minutesBefore = (start.toInstant(timeZone) - reminder.toInstant(timeZone)).inWholeMinutes
    return if (minutesBefore in 0..40_320L) {
        GoogleCalendarReminders(
            useDefault = false,
            overrides = listOf(GoogleCalendarReminderOverride(method = "popup", minutes = minutesBefore.toInt())),
        )
    } else {
        GoogleCalendarReminders(useDefault = false)
    }
}

private fun String.parseInstantOrNull(): Instant? = runCatching { Instant.parse(this) }.getOrNull()

private fun String.parseGoogleDateTimeOrNull(): LocalDateTime? {
    val currentTimeZone = TimeZone.currentSystemDefault()
    return runCatching { Instant.parse(this).toLocalDateTime(currentTimeZone) }
        .getOrElse {
            runCatching { LocalDateTime.parse(this.take(16)) }.getOrNull()
        }
}

private fun LocalDateTime.plusOneHour(timeZone: TimeZone): LocalDateTime = toInstant(timeZone).plus(1.hours).toLocalDateTime(timeZone)

private inline fun <reified T : Enum<T>> String.enumValueOrDefault(default: T): T = enumValues<T>().firstOrNull { it.name == this } ?: default
