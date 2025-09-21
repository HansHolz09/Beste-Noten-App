package com.hansholz.bestenotenapp.notifications

import androidx.compose.runtime.mutableStateOf
import com.hansholz.bestenotenapp.api.BesteSchuleApi
import com.hansholz.bestenotenapp.api.createHttpClient
import com.hansholz.bestenotenapp.api.models.Grade
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.security.AuthTokenManager
import com.russhwolf.settings.Settings
import io.ktor.client.plugins.ClientRequestException
import kotlin.random.Random
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal enum class GradeNotificationOutcome {
    Success,
    Retry
}

internal object GradeNotificationEngine {
    private const val KEY_KNOWN_GRADE_IDS = "gradeNotificationsKnownGradeIds"
    private const val MIN_INTERVAL_MINUTES = 15L

    private val json = Json { ignoreUnknownKeys = true }
    private val settings = Settings()
    private val authTokenManager = AuthTokenManager()

    fun minimumIntervalMinutes(): Long = MIN_INTERVAL_MINUTES

    fun isEnabled(): Boolean = settings.getBoolean(GradeNotifications.KEY_ENABLED, false)

    fun getIntervalMinutes(): Long =
        settings.getLong(GradeNotifications.KEY_INTERVAL_MINUTES, GradeNotifications.DEFAULT_INTERVAL_MINUTES)

    fun isWifiOnlyEnabled(): Boolean = settings.getBoolean(GradeNotifications.KEY_WIFI_ONLY, false)

    fun shouldSchedule(): Boolean = isEnabled() && hasCredentials()

    fun clearKnownGrades() {
        settings.remove(KEY_KNOWN_GRADE_IDS)
    }

    suspend fun runCheck(): GradeNotificationOutcome {

        // TODO: Remove
        GradeNotificationNotifier.notifyNewGrades(
            listOf(
                GradeNotificationPayload(
                    id = Random.nextInt(10000).toString(),
                    title = "Überprüfung gestartet...",
                    body = "Beschreibung"
                )
            )
        )

        if (!shouldSchedule()) return GradeNotificationOutcome.Success

        val studentId = settings.getStringOrNull("studentId") ?: return GradeNotificationOutcome.Success
        val token = authTokenManager.getToken()
        if (token.isNullOrBlank()) return GradeNotificationOutcome.Success

        val httpClient = createHttpClient()
        return try {
            val authState = mutableStateOf<String?>(token)
            val studentState = mutableStateOf<String?>(studentId)
            val api = BesteSchuleApi(httpClient, authState, studentState)
            val includes = listOf("grades", "subject")
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
            GradeNotificationOutcome.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 401) {
                GradeNotificationOutcome.Success
            } else {
                GradeNotificationOutcome.Retry
            }
        } catch (_: Exception) {
            GradeNotificationOutcome.Retry
        } finally {
            httpClient.close()
        }
    }

    private fun hasCredentials(): Boolean {
        val studentId = settings.getStringOrNull("studentId")
        val token = authTokenManager.getToken()
        return !studentId.isNullOrBlank() && !token.isNullOrBlank()
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
        if (entries.isEmpty()) return

        val notifications = entries.map { (grade, collection) ->
            val title = collection.subject?.name?.let { "Neue Note in $it" } ?: "Neue Note"
            val body = "Bei " + (collection.name ?: "einer unbekannten Leistung") + " hast du die Note " + grade.value + " erreicht"
            GradeNotificationPayload(
                id = grade.id.toString(),
                title = title,
                body = body
            )
        }

        GradeNotificationNotifier.notifyNewGrades(notifications)
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
}
