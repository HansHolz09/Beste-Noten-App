package com.hansholz.bestenotenapp.homework

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class HomeworkEntry(
    val localId: String,
    val title: String,
    val description: String?,
    val type: HomeworkType,
    val status: HomeworkStatus,
    val placement: HomeworkPlacement,
    val dueDate: LocalDate,
    val dueDateTime: LocalDateTime?,
    val subjectId: String?,
    val subjectName: String?,
    val teacherId: String?,
    val roomId: String?,
    val timetableId: String?,
    val timetableTimeId: String?,
    val timetableTimeLessonId: String?,
    val lessonNumber: Int?,
    val weekday: Int?,
    val sourceDate: LocalDate?,
    val source: HomeworkSource,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?,
    val reminderAt: LocalDateTime? = null,
)

@Serializable
enum class HomeworkType {
    HOMEWORK,
    TEST,
    APPOINTMENT,
    NOTE,
}

@Serializable
enum class HomeworkStatus {
    OPEN,
    DONE,
    DELETED,
}

@Serializable
enum class HomeworkPlacement {
    DAY,
    LESSON,
}

@Serializable
enum class HomeworkSource {
    USER,
}

@Serializable
data class CalendarSyncMapping(
    val localId: String,
    val googleCalendarId: String,
    val googleEventId: String?,
    val googleEtag: String?,
    val googleUpdatedAt: Instant?,
    val syncState: SyncState,
    val lastSyncedHash: String?,
)

@Serializable
data class SyncOutboxItem(
    val id: String,
    val localId: String,
    val operation: SyncOperation,
    val createdAt: Instant,
    val retryCount: Int,
)

@Serializable
enum class SyncOperation {
    CREATE,
    UPDATE,
    DELETE,
}

@Serializable
enum class SyncState {
    SYNCED,
    ERROR,
}

fun newHomeworkId(): String = "hw-${Clock.System.now().toEpochMilliseconds()}-${Random.nextInt(100_000, 999_999)}"

fun newOutboxId(): String = "outbox-${Clock.System.now().toEpochMilliseconds()}-${Random.nextInt(100_000, 999_999)}"

fun HomeworkEntry.hasManualOrigin(): Boolean = source == HomeworkSource.USER && status != HomeworkStatus.DELETED

fun HomeworkEntry.stableSyncHash(): String =
    listOf(
        localId,
        title,
        description.orEmpty(),
        type.name,
        status.name,
        placement.name,
        dueDate.toString(),
        dueDateTime?.toString().orEmpty(),
        subjectId.orEmpty(),
        subjectName.orEmpty(),
        teacherId.orEmpty(),
        roomId.orEmpty(),
        timetableId.orEmpty(),
        timetableTimeId.orEmpty(),
        timetableTimeLessonId.orEmpty(),
        lessonNumber?.toString().orEmpty(),
        weekday?.toString().orEmpty(),
        sourceDate?.toString().orEmpty(),
        source.name,
        updatedAt.toString(),
        deletedAt?.toString().orEmpty(),
        reminderAt?.toString().orEmpty(),
    ).joinToString("|")
