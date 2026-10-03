package com.hansholz.bestenotenapp.notifications

import androidx.compose.runtime.mutableStateOf
import com.hansholz.bestenotenapp.api.BesteSchuleApi
import com.hansholz.bestenotenapp.api.BesteSchuleAuth
import com.hansholz.bestenotenapp.api.createHttpClient
import com.hansholz.bestenotenapp.api.models.Grade
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.api.models.Level
import com.hansholz.bestenotenapp.api.oidcClient
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.security.kSafe
import com.hansholz.bestenotenapp.security.kSafeProvider
import com.hansholz.bestenotenapp.utils.SecondaryStage
import com.hansholz.bestenotenapp.utils.secondaryStage
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.publicvalue.multiplatform.oidc.DefaultOpenIdConnectClient
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

internal enum class GradeNotificationOutcome {
    Success,
    Retry,
}

internal object GradeNotificationEngine {
    private const val KEY_KNOWN_GRADE_IDS = "gradeNotificationsKnownGradeIds"
    private const val KEY_SEEN_GRADE_IDS = "gradeNotificationsSeenGradeIds"
    private const val KEY_LAST_CHECK = "gradeNotificationsLastCheck"

    private val json = Json { ignoreUnknownKeys = true }
    private val kSafe = kSafe().also { it.deleteDirect("gradeNotificationDiagnostics") }
    private val checkMutex = Mutex()
    private val seenMutex = Mutex()

    fun isEnabled(): Boolean =
        kSafeProvider(kSafe) { get("gradeNotificationsEnabled", false) } &&
            listOf(
                Platform.ANDROID,
                Platform.IOS,
            ).contains(getPlatform())

    fun getIntervalMinutes(): Long = kSafeProvider(kSafe) { get("gradeNotificationsIntervalMinutes", 60L) }

    fun getNextCheckDelayMillis(): Long =
        kSafeProvider(kSafe) {
            val interval = getIntervalMinutes().coerceAtLeast(15L) * 60_000
            val lastCheck = get(KEY_LAST_CHECK, 0L)
            val remaining = lastCheck + interval - Clock.System.now().toEpochMilliseconds()
            return if (lastCheck > 0L && remaining in 1..interval) remaining else interval
        }

    fun isWifiOnlyEnabled(): Boolean = kSafeProvider(kSafe) { get("gradeNotificationsWifiOnly", false) }

    fun shouldSchedule(): Boolean = isEnabled() && hasCredentials()

    fun clearKnownGrades() {
        kSafe.deleteDirect(KEY_KNOWN_GRADE_IDS)
        kSafe.deleteDirect(KEY_SEEN_GRADE_IDS)
        kSafe.deleteDirect(KEY_LAST_CHECK)
    }

    fun hasBaseline(): Boolean = loadKnownGradeIds() != null

    suspend fun markGradesAsSeen(
        ids: Set<Int>,
        studentId: String,
    ) {
        seenMutex.withLock {
            kSafeProvider(kSafe) {
                if (!GradeNotifications.isSupported || get<String?>("studentId", null) != studentId) return@withLock
                val seenIds = loadGradeIds(KEY_SEEN_GRADE_IDS).orEmpty()
                if (!seenIds.containsAll(ids)) storeGradeIds(KEY_SEEN_GRADE_IDS, seenIds + ids)
            }
        }
    }

    suspend fun runCheck(): GradeNotificationOutcome = checkMutex.withLock { checkGrades() }

    private suspend fun checkGrades(): GradeNotificationOutcome =
        kSafeProvider(kSafe) {
            if (!shouldSchedule()) return GradeNotificationOutcome.Success

            val now = Clock.System.now().toEpochMilliseconds()
            val lastCheck = get(KEY_LAST_CHECK, 0L)
            val interval = getIntervalMinutes().coerceAtLeast(15L) * 60_000
            val tolerance = (interval / 10).coerceAtMost(5 * 60_000L)
            if (now >= lastCheck && now - lastCheck < interval - tolerance) {
                return GradeNotificationOutcome.Success
            }

            val studentId = get<String?>("studentId", null) ?: return GradeNotificationOutcome.Success
            val token = get<String?>("authToken", null) ?: return GradeNotificationOutcome.Success

            val httpClient = createHttpClient()
            return try {
                val authState = mutableStateOf<String?>(token)
                val studentState = mutableStateOf<String?>(studentId)
                val authClient = DefaultOpenIdConnectClient(httpClient, oidcClient.config)
                val auth = BesteSchuleAuth(authClient, kSafe, authState).also { it.restore() }
                val api = BesteSchuleApi(httpClient, authState, studentState, auth::getValidAccessToken)
                val collections = fetchLatestCollections(api)
                val currentIds = collections.flatMap { it.grades.orEmpty() }.map { it.id }.toSet()

                val knownIds = loadKnownGradeIds()
                val newIds = knownIds?.let { currentIds - it - loadGradeIds(KEY_SEEN_GRADE_IDS).orEmpty() }.orEmpty()

                if (newIds.isNotEmpty()) {
                    val newGrades =
                        collections
                            .flatMap { collection ->
                                collection.grades
                                    .orEmpty()
                                    .filter { it.id in newIds }
                                    .map { it to collection }
                            }.sortedBy { it.second.givenAt }
                    val levelsByYear =
                        try {
                            withTimeoutOrNull(5.seconds) { loadLevelsFor(api, newGrades) } ?: emptyMap()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            e.printStackTrace()
                            emptyMap()
                        }
                    val processedIds = knownIds.orEmpty().toMutableSet()
                    for (entry in newGrades) {
                        val submitted =
                            seenMutex.withLock {
                                if (get<String?>("studentId", null) != studentId || get<String?>("authToken", null) != authState.value || !shouldSchedule()) {
                                    return GradeNotificationOutcome.Retry
                                }
                                if (entry.first.id in loadGradeIds(KEY_SEEN_GRADE_IDS).orEmpty()) {
                                    true
                                } else {
                                    notifyNewGrades(listOf(entry), levelsByYear)
                                }
                            }
                        if (!submitted) return GradeNotificationOutcome.Retry
                        processedIds += entry.first.id
                        storeKnownGradeIds(processedIds)
                    }
                }

                if (get<String?>("studentId", null) != studentId || get<String?>("authToken", null) != authState.value || !shouldSchedule()) {
                    return GradeNotificationOutcome.Retry
                }
                storeKnownGradeIds(knownIds.orEmpty() + currentIds)
                put(KEY_LAST_CHECK, Clock.System.now().toEpochMilliseconds())
                GradeNotificationOutcome.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: ClientRequestException) {
                if (e.response.status.value == 401 && getPlatform() != Platform.IOS) {
                    GradeNotificationOutcome.Success
                } else {
                    GradeNotificationOutcome.Retry
                }
            } catch (e: Exception) {
                e.printStackTrace()
                GradeNotificationOutcome.Retry
            } finally {
                httpClient.close()
            }
        }

    private fun hasCredentials(): Boolean =
        kSafeProvider(kSafe) {
            val studentId = get<String?>("studentId", null)
            val token = get<String?>("authToken", null)
            return !studentId.isNullOrBlank() && !token.isNullOrBlank()
        }

    private suspend fun fetchLatestCollections(api: BesteSchuleApi): List<GradeCollection> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val includes = listOf("grades", "interval")
        return api.collectionsIndex(include = includes, page = 1).data.filter { it.isCurrent(today) }
    }

    private fun GradeCollection.isCurrent(today: LocalDate): Boolean =
        try {
            interval?.let { today in LocalDate.parse(it.from)..LocalDate.parse(it.to) } ?: true
        } catch (_: IllegalArgumentException) {
            true
        }

    private suspend fun loadLevelsFor(
        api: BesteSchuleApi,
        entries: List<Pair<Grade, GradeCollection>>,
    ): Map<Int, Level> {
        val yearIds = entries.mapNotNull { it.second.interval?.yearId }.distinct()
        if (yearIds.isEmpty()) return emptyMap()

        val groups = api.groupsIndex(filterMeta = true, filterYear = yearIds.joinToString(",")).data
        val levels = mutableMapOf<Int, Level>()
        for (yearId in yearIds) {
            val groupId = groups.firstOrNull { it.yearId == yearId }?.id ?: continue
            api
                .groupsShow(groupId, listOf("level"), yearId)
                .data.level
                ?.let { levels[yearId] = it }
        }
        return levels
    }

    private suspend fun notifyNewGrades(
        entries: List<Pair<Grade, GradeCollection>>,
        levelsByYear: Map<Int, Level>,
    ): Boolean {
        if (entries.isEmpty()) return true

        val notifications =
            entries.map { (grade, collection) ->
                val title = collection.subject?.name?.let { "Neue Note in $it" } ?: "Neue Note"
                val isSecondaryTwo = collection.interval?.yearId?.let { levelsByYear[it]?.secondaryStage() } == SecondaryStage.TWO
                val value = if (isSecondaryTwo) "${grade.value} Punkte" else "die Note ${grade.value}"
                val body = "Bei " + (collection.name ?: "einer unbekannten Leistung") + " hast du $value erreicht"
                GradeNotificationPayload(
                    id = grade.id.toString(),
                    title = title,
                    body = body,
                )
            }

        return GradeNotificationNotifier.notifyNewGrades(notifications)
    }

    private fun loadKnownGradeIds(): Set<Int>? = loadGradeIds(KEY_KNOWN_GRADE_IDS)

    private fun loadGradeIds(key: String): Set<Int>? =
        kSafeProvider(kSafe) {
            val raw = get<String?>(key, null) ?: return null
            return runCatching { json.decodeFromString<KnownGrades>(raw).ids.toSet() }.getOrNull()
        }

    private fun storeKnownGradeIds(ids: Set<Int>) = storeGradeIds(KEY_KNOWN_GRADE_IDS, ids)

    private fun storeGradeIds(
        key: String,
        ids: Set<Int>,
    ) = kSafeProvider(kSafe) {
        val payload = json.encodeToString(KnownGrades(ids.toList()))
        put(key, payload)
    }

    @Serializable
    private data class KnownGrades(
        val ids: List<Int>,
    )
}
