package com.hansholz.bestenotenapp.notifications

import androidx.compose.runtime.mutableStateOf
import com.hansholz.bestenotenapp.api.BesteSchuleApi
import com.hansholz.bestenotenapp.api.createHttpClient
import com.hansholz.bestenotenapp.api.models.Grade
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.security.AuthTokenManager
import com.mmk.kmpnotifier.notification.NotifierManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.getStringOrNull
import dev.mattramotar.meeseeks.runtime.AppContext
import dev.mattramotar.meeseeks.runtime.Meeseeks
import dev.mattramotar.meeseeks.runtime.RuntimeContext
import dev.mattramotar.meeseeks.runtime.TaskPayload
import dev.mattramotar.meeseeks.runtime.TaskRequest
import dev.mattramotar.meeseeks.runtime.TaskResult
import dev.mattramotar.meeseeks.runtime.TaskSchedule
import dev.mattramotar.meeseeks.runtime.Worker
import dev.mattramotar.meeseeks.runtime.dsl.TaskRequestConfigurationScope
import dev.mattramotar.meeseeks.runtime.periodic
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.minutes

actual object GradeNotifications {
    actual const val KEY_ENABLED = "gradeNotificationsEnabled"
    actual const val KEY_INTERVAL_MINUTES = "gradeNotificationsIntervalMinutes"
    actual const val DEFAULT_INTERVAL_MINUTES = 60L
    actual val isSupported: Boolean = true
    private const val KEY_KNOWN_GRADE_IDS = "gradeNotificationsKnownGradeIds"
    private const val MIN_INTERVAL_MINUTES = 15L

    private val json = Json { ignoreUnknownKeys = true }
    private val settings = Settings()
    private var isInitialized = false

    actual fun initialize(platformContext: Any?) {
        val appContext = when (platformContext) {
            is AppContext -> platformContext
            else -> platformContext as? AppContext
        } ?: return
        if (!isInitialized) {
            Meeseeks.initialize(appContext) {
                minBackoff(5.minutes)
                maxRetryCount(3)
                register<GradeCheckPayload>(GradeCheckPayload.STABLE_ID) { GradeCheckWorker(it) }
            }
            isInitialized = true
        }
        refreshScheduling()
    }

    actual fun refreshScheduling() {
        if (!isInitialized) return
        val manager = runCatching { Meeseeks.bgTaskManager() }.getOrNull() ?: return
        val enabled = settings.getBoolean(KEY_ENABLED, false)
        val interval = settings.getLong(KEY_INTERVAL_MINUTES, DEFAULT_INTERVAL_MINUTES)
            .coerceAtLeast(MIN_INTERVAL_MINUTES)
        val studentId = settings.getStringOrNull("studentId")
        val hasToken = !AuthTokenManager().getToken().isNullOrBlank()

        val tasks = manager.listTasks().filter { it.task.payload is GradeCheckPayload }
        if (!enabled || studentId.isNullOrBlank() || !hasToken) {
            tasks.forEach { manager.cancel(it.id) }
            return
        }

        val every = interval.minutes
        val configure: TaskRequestConfigurationScope<GradeCheckPayload>.() -> Unit = {
            requireNetwork()
            requireBatteryNotLow()
        }

        val existing = tasks.firstOrNull()
        if (existing == null) {
            manager.periodic(GradeCheckPayload, every = every) { configure() }
        } else {
            val schedule = existing.task.schedule as? TaskSchedule.Periodic
            if (schedule == null || schedule.interval != every) {
                val request = TaskRequest.periodic(GradeCheckPayload, every) { configure() }
                manager.reschedule(existing.id, request)
            }
            tasks.drop(1).forEach { manager.cancel(it.id) }
        }
    }

    actual fun onSettingsUpdated() {
        refreshScheduling()
    }

    actual fun onLogin() {
        refreshScheduling()
    }

    actual fun onLogout() {
        clearKnownGrades()
        refreshScheduling()
    }

    internal fun clearKnownGrades() {
        settings.remove(KEY_KNOWN_GRADE_IDS)
    }

    private fun loadKnownGradeIds(): Set<Int> {
        val raw = settings.getStringOrNull(KEY_KNOWN_GRADE_IDS) ?: return emptySet()
        return runCatching { json.decodeFromString<KnownGrades>(raw).ids.toSet() }.getOrElse { emptySet() }
    }

    private fun storeKnownGradeIds(ids: Set<Int>) {
        val payload = json.encodeToString(KnownGrades(ids.toList()))
        settings.putString(KEY_KNOWN_GRADE_IDS, payload)
    }

    @Serializable
    private data class KnownGrades(val ids: List<Int>)

    @Serializable
    data object GradeCheckPayload : TaskPayload {
        const val STABLE_ID = "com.hansholz.bestenotenapp.notifications.GradeCheckPayload:1.0.0"
    }

    class GradeCheckWorker(appContext: AppContext) : Worker<GradeCheckPayload>(appContext) {
        override suspend fun run(payload: GradeCheckPayload, context: RuntimeContext): TaskResult {
            if (!settings.getBoolean(KEY_ENABLED, false)) {
                return TaskResult.Success
            }
            val studentId = settings.getStringOrNull("studentId") ?: return TaskResult.Success
            val token = AuthTokenManager().getToken()
            if (token.isNullOrBlank()) {
                return TaskResult.Success
            }

            val httpClient = createHttpClient()
            return try {
                val authState = mutableStateOf(token)
                val studentState = mutableStateOf(studentId)
                val api = BesteSchuleApi(httpClient, authState, studentState)
                val includes = listOf("grades", "interval", "subject", "grades.histories")
                val collections = fetchAllCollections(api, includes)
                val currentIds = collections.flatMap { it.grades.orEmpty() }.map { it.id }.toSet()

                val knownIds = loadKnownGradeIds()
                val newIds = currentIds - knownIds
                if (newIds.isNotEmpty()) {
                    val newGrades = collections.flatMap { collection ->
                        collection.grades.orEmpty()
                            .filter { it.id in newIds }
                            .map { it to collection }
                    }.sortedBy { it.second.givenAt }
                    notifyNewGrades(newGrades)
                }
                storeKnownGradeIds(currentIds)
                TaskResult.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: ClientRequestException) {
                if (e.response.status.value == 401) {
                    TaskResult.Success
                } else {
                    TaskResult.Retry
                }
            } catch (e: Exception) {
                TaskResult.Retry
            } finally {
                httpClient.close()
            }
        }

        private suspend fun fetchAllCollections(
            api: BesteSchuleApi,
            includes: List<String>
        ): List<GradeCollection> {
            val result = mutableListOf<GradeCollection>()
            val firstPage = api.collectionsIndex(include = includes)
            result += firstPage.data
            val lastPage = firstPage.meta?.lastPage ?: 1
            if (lastPage > 1) {
                for (page in 2..lastPage) {
                    result += api.collectionsIndex(include = includes, page = page).data
                }
            }
            return result
        }

        private fun notifyNewGrades(entries: List<Pair<Grade, GradeCollection>>) {
            val notifier = NotifierManager.getLocalNotifier()
            entries.forEach { (grade, collection) ->
                val title = collection.name?.takeIf { it.isNotBlank() } ?: "Neue Note"
                val subject = collection.subject?.name ?: grade.subject?.name ?: "Unbekanntes Fach"
                val body = buildString {
                    append(subject)
                    if (grade.value.isNotBlank()) {
                        append(" – ")
                        append(grade.value)
                    }
                }
                notifier.notify {
                    id = grade.id
                    this.title = title
                    this.body = body
                }
            }
        }
    }
}
