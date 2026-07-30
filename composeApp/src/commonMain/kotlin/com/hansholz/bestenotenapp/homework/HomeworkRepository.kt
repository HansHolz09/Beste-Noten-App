package com.hansholz.bestenotenapp.homework

import com.hansholz.bestenotenapp.security.kSafeProvider
import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface HomeworkRepository {
    suspend fun getHomeworkForDate(date: LocalDate): List<HomeworkEntry>

    suspend fun getHomeworkForLesson(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ): List<HomeworkEntry>

    suspend fun getOpenHomework(): List<HomeworkEntry>

    suspend fun hasUserDayNotes(date: LocalDate): Boolean

    suspend fun hasHomeworkForLesson(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ): Boolean

    suspend fun createHomework(entry: HomeworkEntry)

    suspend fun updateHomework(entry: HomeworkEntry)

    suspend fun markHomeworkDone(
        localId: String,
        done: Boolean,
    )

    suspend fun deleteHomework(localId: String)

    suspend fun syncNow()
}

interface HomeworkSyncSettings {
    var homeworkEnabled: Boolean
    var googleSyncEnabled: Boolean
    var googleCalendarId: String?
    var googleCalendarResolved: Boolean
    var nextSyncToken: String?
    var lastSuccessfulSyncAt: Instant?
    var lastSyncError: String?
}

private interface LocalHomeworkDataSource {
    suspend fun getById(localId: String): HomeworkEntry?

    suspend fun getHomeworkForDate(date: LocalDate): List<HomeworkEntry>

    suspend fun getHomeworkForSubject(subjectId: String): List<HomeworkEntry>

    suspend fun getHomeworkForLesson(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ): List<HomeworkEntry>

    suspend fun getOpenHomework(): List<HomeworkEntry>

    suspend fun getDayNotes(date: LocalDate): List<HomeworkEntry>

    suspend fun getLessonNotes(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ): List<HomeworkEntry>

    suspend fun hasUserDayNotes(date: LocalDate): Boolean

    suspend fun hasHomeworkForLesson(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ): Boolean

    suspend fun getAllHomework(): List<HomeworkEntry>

    suspend fun insert(entry: HomeworkEntry)

    suspend fun upsert(entry: HomeworkEntry)

    suspend fun update(entry: HomeworkEntry)

    suspend fun markStatus(
        localId: String,
        status: HomeworkStatus,
        updatedAt: Instant,
        deletedAt: Instant?,
    )

    suspend fun deletePermanently(localId: String)

    suspend fun getSyncMapping(localId: String): CalendarSyncMapping?

    suspend fun getSyncMappings(): List<CalendarSyncMapping>

    suspend fun upsertSyncMapping(mapping: CalendarSyncMapping)

    suspend fun deleteSyncMapping(localId: String)

    suspend fun enqueueOutbox(
        localId: String,
        operation: SyncOperation,
        createdAt: Instant,
    )

    suspend fun getOutboxItems(): List<SyncOutboxItem>

    suspend fun deleteOutboxItem(id: String)

    suspend fun incrementOutboxRetry(id: String)
}

class KSafeHomeworkSyncSettings(
    private val kSafe: KSafe,
) : HomeworkSyncSettings {
    override var homeworkEnabled: Boolean
        get() = kSafeProvider(kSafe) { get("homeworkEnabled", true) }
        set(value) = kSafeProvider(kSafe) { put("homeworkEnabled", value) }

    override var googleSyncEnabled: Boolean
        get() = kSafeProvider(kSafe) { get("homeworkGoogleSyncEnabled", false) }
        set(value) = kSafeProvider(kSafe) { put("homeworkGoogleSyncEnabled", value) }

    override var googleCalendarId: String?
        get() = kSafeProvider(kSafe) { get("homeworkGoogleCalendarId", "") }.takeIf(String::isNotBlank)
        set(value) = kSafeProvider(kSafe) { put("homeworkGoogleCalendarId", value.orEmpty()) }

    override var googleCalendarResolved: Boolean
        get() = kSafeProvider(kSafe) { get("homeworkGoogleCalendarResolved", false) }
        set(value) = kSafeProvider(kSafe) { put("homeworkGoogleCalendarResolved", value) }

    override var nextSyncToken: String?
        get() = kSafeProvider(kSafe) { get("homeworkGoogleNextSyncToken", "") }.takeIf(String::isNotBlank)
        set(value) = kSafeProvider(kSafe) { put("homeworkGoogleNextSyncToken", value.orEmpty()) }

    @OptIn(ExperimentalTime::class)
    override var lastSuccessfulSyncAt: Instant?
        get() =
            kSafeProvider(kSafe) { get("homeworkLastSuccessfulSyncAt", "") }
                .takeIf(String::isNotBlank)
                ?.let(Instant::parse)
        set(value) = kSafeProvider(kSafe) { put("homeworkLastSuccessfulSyncAt", value?.toString().orEmpty()) }

    override var lastSyncError: String?
        get() = kSafeProvider(kSafe) { get("homeworkLastSyncError", "") }.takeIf(String::isNotBlank)
        set(value) = kSafeProvider(kSafe) { put("homeworkLastSyncError", value.orEmpty()) }
}

class KSafeHomeworkRepository(
    kSafe: KSafe,
    googleDataSource: GoogleCalendarHomeworkSyncDataSource,
    settings: HomeworkSyncSettings,
) : HomeworkRepository {
    private val local = KSafeHomeworkDataSource(kSafe)
    private val sync = HomeworkSyncManager(local, googleDataSource, settings)

    override suspend fun getHomeworkForDate(date: LocalDate) = local.getHomeworkForDate(date)

    override suspend fun getHomeworkForLesson(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ) = local.getHomeworkForLesson(timetableTimeLessonId, sourceDate)

    override suspend fun getOpenHomework() = local.getOpenHomework()

    override suspend fun hasUserDayNotes(date: LocalDate) = local.hasUserDayNotes(date)

    override suspend fun hasHomeworkForLesson(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ) = local.hasHomeworkForLesson(timetableTimeLessonId, sourceDate)

    override suspend fun createHomework(entry: HomeworkEntry) {
        local.insert(entry)
        local.enqueueOutbox(entry.localId, SyncOperation.CREATE, entry.createdAt)
        sync.syncNow()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateHomework(entry: HomeworkEntry) {
        val updated = entry.copy(updatedAt = Clock.System.now())
        local.update(updated)
        local.enqueueOutbox(updated.localId, SyncOperation.UPDATE, updated.updatedAt)
        sync.syncNow()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun markHomeworkDone(
        localId: String,
        done: Boolean,
    ) {
        val now = Clock.System.now()
        local.markStatus(localId, if (done) HomeworkStatus.DONE else HomeworkStatus.OPEN, now, null)
        local.enqueueOutbox(localId, SyncOperation.UPDATE, now)
        sync.syncNow()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun deleteHomework(localId: String) {
        val now = Clock.System.now()
        local.markStatus(localId, HomeworkStatus.DELETED, now, now)
        local.enqueueOutbox(localId, SyncOperation.DELETE, now)
        sync.syncNow()
    }

    override suspend fun syncNow() = sync.syncNow()
}

private class HomeworkSyncManager(
    private val local: LocalHomeworkDataSource,
    private val google: GoogleCalendarHomeworkSyncDataSource,
    private val settings: HomeworkSyncSettings,
) {
    @OptIn(ExperimentalTime::class)
    suspend fun syncNow() {
        if (!settings.homeworkEnabled || !settings.googleSyncEnabled) return
        try {
            val calendarId =
                if (settings.googleCalendarResolved) {
                    settings.googleCalendarId
                } else {
                    google.getOrCreateCalendar(settings.googleCalendarId).id
                } ?: google.getOrCreateCalendar(null).id
            if (settings.googleCalendarId != calendarId) {
                settings.googleCalendarId = calendarId
                settings.nextSyncToken = null
            }
            settings.googleCalendarResolved = true
            enqueueUnsynced(calendarId)
            push(calendarId)
            pull(calendarId)
            settings.lastSuccessfulSyncAt = Clock.System.now()
            settings.lastSyncError = null
        } catch (e: InvalidGoogleCalendarSyncTokenException) {
            if (settings.nextSyncToken != null) {
                settings.nextSyncToken = null
                syncNow()
            } else {
                settings.lastSyncError = e.message
                e.printStackTrace()
            }
        } catch (e: Exception) {
            settings.lastSyncError = e.message ?: "Google-Kalender konnte nicht synchronisiert werden"
            e.printStackTrace()
        }
    }

    private suspend fun enqueueUnsynced(calendarId: String) {
        local
            .getAllHomework()
            .filter { it.status != HomeworkStatus.DELETED }
            .forEach { entry ->
                val mapping = local.getSyncMapping(entry.localId)
                val needsSync =
                    mapping == null ||
                        mapping.googleCalendarId != calendarId ||
                        mapping.googleEventId == null ||
                        mapping.syncState == SyncState.ERROR ||
                        mapping.lastSyncedHash != entry.stableSyncHash()
                if (needsSync) {
                    local.enqueueOutbox(
                        entry.localId,
                        if (mapping?.googleEventId == null || mapping.googleCalendarId != calendarId) {
                            SyncOperation.CREATE
                        } else {
                            SyncOperation.UPDATE
                        },
                        entry.updatedAt,
                    )
                }
            }
    }

    private suspend fun push(calendarId: String) {
        local.getOutboxItems().forEach { item ->
            try {
                when (item.operation) {
                    SyncOperation.CREATE,
                    SyncOperation.UPDATE,
                    -> {
                        val entry = local.getById(item.localId)
                        if (entry == null || entry.status == HomeworkStatus.DELETED) {
                            local.deleteOutboxItem(item.id)
                            return@forEach
                        }
                        val mapping = local.getSyncMapping(item.localId)?.takeIf { it.googleCalendarId == calendarId }
                        local.upsertSyncMapping(google.pushEntry(calendarId, entry, mapping))
                        local.deleteOutboxItem(item.id)
                    }

                    SyncOperation.DELETE -> {
                        google.deleteEntry(calendarId, local.getSyncMapping(item.localId))
                        local.deleteSyncMapping(item.localId)
                        local.deletePermanently(item.localId)
                        local.deleteOutboxItem(item.id)
                    }
                }
            } catch (e: Exception) {
                local.incrementOutboxRetry(item.id)
                local.getSyncMapping(item.localId)?.let {
                    local.upsertSyncMapping(it.copy(syncState = SyncState.ERROR))
                }
                throw e
            }
        }
    }

    private suspend fun pull(calendarId: String) {
        val remote = google.loadEvents(calendarId, settings.nextSyncToken)
        remote.events.forEach { event ->
            val remoteEntry = event.toHomeworkEntryOrNull() ?: return@forEach
            val localEntry = local.getById(remoteEntry.localId)
            when {
                localEntry == null -> {
                    local.upsert(remoteEntry)
                }

                remoteEntry.status == HomeworkStatus.DELETED -> {
                    local.markStatus(remoteEntry.localId, HomeworkStatus.DELETED, remoteEntry.updatedAt, remoteEntry.deletedAt)
                }

                newestWins(localEntry, event) == ConflictWinner.REMOTE -> {
                    local.upsert(remoteEntry)
                }
            }
            local.upsertSyncMapping(
                CalendarSyncMapping(
                    localId = remoteEntry.localId,
                    googleCalendarId = calendarId,
                    googleEventId = event.id,
                    googleEtag = event.etag,
                    googleUpdatedAt = remoteEntry.updatedAt,
                    syncState = SyncState.SYNCED,
                    lastSyncedHash = remoteEntry.stableSyncHash(),
                ),
            )
        }
        settings.nextSyncToken = remote.nextSyncToken ?: settings.nextSyncToken
    }
}

private class KSafeHomeworkDataSource(
    private val kSafe: KSafe,
) : LocalHomeworkDataSource {
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getById(localId: String) = read().entries.firstOrNull { it.localId == localId }

    override suspend fun getHomeworkForDate(date: LocalDate) = read().entries.filter { it.dueDate == date && it.status != HomeworkStatus.DELETED }

    override suspend fun getHomeworkForSubject(subjectId: String) = read().entries.filter { it.subjectId == subjectId && it.status != HomeworkStatus.DELETED }

    override suspend fun getHomeworkForLesson(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ) = read().entries.filter {
        it.timetableTimeLessonId == timetableTimeLessonId &&
            it.sourceDate == sourceDate &&
            it.status != HomeworkStatus.DELETED
    }

    override suspend fun getOpenHomework() = read().entries.filter { it.status == HomeworkStatus.OPEN }

    override suspend fun getDayNotes(date: LocalDate) = getHomeworkForDate(date).filter { it.hasManualOrigin() }

    override suspend fun getLessonNotes(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ) = getHomeworkForLesson(timetableTimeLessonId, sourceDate).filter { it.hasManualOrigin() }

    override suspend fun hasUserDayNotes(date: LocalDate) = getDayNotes(date).isNotEmpty()

    override suspend fun hasHomeworkForLesson(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ) = getLessonNotes(timetableTimeLessonId, sourceDate).isNotEmpty()

    override suspend fun getAllHomework() = read().entries

    override suspend fun insert(entry: HomeworkEntry) =
        change {
            copy(entries = entries.filterNot { it.localId == entry.localId } + entry)
        }

    override suspend fun upsert(entry: HomeworkEntry) = insert(entry)

    override suspend fun update(entry: HomeworkEntry) = insert(entry)

    override suspend fun markStatus(
        localId: String,
        status: HomeworkStatus,
        updatedAt: Instant,
        deletedAt: Instant?,
    ) = change {
        copy(
            entries =
                entries.map {
                    if (it.localId == localId) {
                        it.copy(status = status, updatedAt = updatedAt, deletedAt = deletedAt)
                    } else {
                        it
                    }
                },
        )
    }

    override suspend fun deletePermanently(localId: String) =
        change {
            copy(entries = entries.filterNot { it.localId == localId })
        }

    override suspend fun getSyncMapping(localId: String) = read().mappings.firstOrNull { it.localId == localId }

    override suspend fun getSyncMappings() = read().mappings

    override suspend fun upsertSyncMapping(mapping: CalendarSyncMapping) =
        change {
            copy(mappings = mappings.filterNot { it.localId == mapping.localId } + mapping)
        }

    override suspend fun deleteSyncMapping(localId: String) =
        change {
            copy(mappings = mappings.filterNot { it.localId == localId })
        }

    override suspend fun enqueueOutbox(
        localId: String,
        operation: SyncOperation,
        createdAt: Instant,
    ) = change {
        val existing = outbox.firstOrNull { it.localId == localId }
        val effectiveOperation =
            when {
                operation == SyncOperation.DELETE -> SyncOperation.DELETE
                existing?.operation == SyncOperation.CREATE -> SyncOperation.CREATE
                else -> operation
            }
        copy(
            outbox =
                outbox.filterNot { it.localId == localId } +
                    SyncOutboxItem(newOutboxId(), localId, effectiveOperation, createdAt, existing?.retryCount ?: 0),
        )
    }

    override suspend fun getOutboxItems() = read().outbox.sortedBy { it.createdAt }

    override suspend fun deleteOutboxItem(id: String) =
        change {
            copy(outbox = outbox.filterNot { it.id == id })
        }

    override suspend fun incrementOutboxRetry(id: String) =
        change {
            copy(outbox = outbox.map { if (it.id == id) it.copy(retryCount = it.retryCount + 1) else it })
        }

    private suspend fun read(): HomeworkStore =
        mutex.withLock {
            val raw = kSafeProvider(kSafe) { get(STORAGE_KEY, "") }
            if (raw.isBlank()) HomeworkStore() else runCatching { json.decodeFromString<HomeworkStore>(raw) }.getOrElse { HomeworkStore() }
        }

    private suspend fun change(block: HomeworkStore.() -> HomeworkStore) {
        mutex.withLock {
            val raw = kSafeProvider(kSafe) { get(STORAGE_KEY, "") }
            val current = if (raw.isBlank()) HomeworkStore() else runCatching { json.decodeFromString<HomeworkStore>(raw) }.getOrElse { HomeworkStore() }
            kSafeProvider(kSafe) { put(STORAGE_KEY, json.encodeToString(current.block())) }
        }
    }

    @Serializable
    private data class HomeworkStore(
        val entries: List<HomeworkEntry> = emptyList(),
        val mappings: List<CalendarSyncMapping> = emptyList(),
        val outbox: List<SyncOutboxItem> = emptyList(),
    )

    private companion object {
        const val STORAGE_KEY = "homeworkData"
    }
}
