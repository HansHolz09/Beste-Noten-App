package com.hansholz.bestenotenapp.api

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * ----------------------------------------------------------------------------------
 * Beste.schule API Client für Ktor in Kotlin Multiplatform (Finale Komplettversion)
 * ----------------------------------------------------------------------------------
 *
 * Diese Datei enthält eine vollständige Implementierung des API-Clients, inklusive
 * aller Endpunkte und Datenklassen, die aus der offiziellen Dokumentation
 * (beste.schule/docs) abgeleitet wurden.
 *
 * BENÖTIGTE ABHÄNGIGKEITEN (build.gradle.kts):
 * - io.ktor:ktor-client-core
 * - io.ktor:ktor-client-content-negotiation
 * - io.ktor:ktor-serialization-kotlinx-json
 * - org.jetbrains.kotlinx:kotlinx-serialization-json
 */

@Serializable
data class PaginatedApiResponse<T>(
    val data: List<T>,
    val links: Links? = null,
    val meta: Meta? = null
)

@Serializable
data class Links(
    val first: String?,
    val last: String?,
    val prev: String?,
    val next: String?
)

@Serializable
data class Meta(
    @SerialName("current_page") val currentPage: Int? = null,
    val from: Int? = null,
    @SerialName("last_page") val lastPage: Int? = null,
    val path: String? = null,
    @SerialName("per_page") val perPage: Int? = null,
    val to: Int? = null,
    val total: Int? = null
)

@Serializable
data class ApiResponse<T>(
    val data: T,
    val meta: Meta? = null
)

@Serializable
data class UserDetail(
    val id: Int,
    val username: String,
    val email: String?,
    @SerialName("email_verified") val emailVerified: Boolean,
    @SerialName("two_factor") val twoFactor: Boolean,
    val teacher: Teacher?,
    val guardian: Guardian?,
    val students: List<UserStudent>,
    val year: Year,
    val config: Config,
    val school: School,
    @SerialName("unread_notifications_count") val unreadNotificationsCount: Int,
    val schools: List<School>,
    val guardians: List<Guardian>,
    @SerialName("firebase_devices") val firebaseDevices: List<FirebaseDevice>,
    val role: String
)

@Serializable
data class UserStudent(
    val id: Int,
    val forename: String,
    val name: String,
    val nickname: String?,
    val users: List<UserInStudent>,
    val guardians: List<Guardian>
)

@Serializable
data class UserInStudent(
    val id: Int,
    val username: String,
    val role: String
)

@Serializable
data class Guardian(
    val id: Int,
    val forename: String,
    val name: String
)

@Serializable
data class Config(
    val id: Int,
    val role: String,
    @SerialName("year_id") val yearId: Int
)

@Serializable
data class School(
    val id: Int,
    val customer: Boolean,
    val name: String,
    val email: String?,
    val type: String?,
    val street: String?,
    @SerialName("street_nr") val streetNr: String?,
    @SerialName("postal_code") val postalCode: String?,
    val city: String?,
    val state: String?,
    val modules: List<String>
)

@Serializable
data class FirebaseDevice(
    val id: Int,
    val name: String,
    val language: String,
    @SerialName("user_id") val userId: Int,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class Year(
    val id: Int,
    val ids: List<Int>? = null, // Oft redundant, aber im Beispiel vorhanden
    val name: String,
    val from: String,
    val to: String,
    val intervals: List<Interval> // Wichtige Korrektur: Intervalle sind hier verschachtelt
)

@Serializable
data class Interval(
    val id: Int,
    val name: String,
    val type: String,
    val from: String,
    val to: String,
    @SerialName("editable_to") val editableTo: String,
    @SerialName("included_interval_id") val includedIntervalId: Int? = null,
    @SerialName("interval_ids") val intervalIds: List<Int>,
    @SerialName("year_id") val yearId: Int
)

@Serializable
data class GradeCollection(
    val id: Int,
    val name: String,
    val type: String,
    @SerialName("given_at") val givenAt: String,
    @SerialName("visible_from") val visibleFrom: String?,
    @SerialName("subject_id") val subjectId: Int,
    @SerialName("interval_id") val intervalId: Int,
    val subject: Subject? = null,
    val interval: Interval? = null,
    val grades: List<Grade>? = null,
    val histories: List<HistoryEntry>? = null
)

@Serializable
data class Grade(
    val id: Int,
    val value: String?,
    @SerialName("given_at") val givenAt: String,
    // Die folgenden Felder sind optional und werden nur bei `include` mitgesendet
    val student: Student? = null,
    val teacher: Teacher? = null,
    val subject: Subject? = null,
    val collection: GradeCollection? = null,
    val histories: List<HistoryEntry>? = null
)

@Serializable
data class HistoryEntry(
    val id: Int,
    @SerialName("history_entry_type") val entryType: String,
    @SerialName("history_entry_id") val entryId: Int,
    val body: String,
    val action: String,
    val attr: String? = null,
    @SerialName("old_value") val oldValue: String? = null,
    @SerialName("conductor_id") val conductorId: Int,
    @SerialName("conductor_type") val conductorType: String,
    val conductor: Conductor
)

@Serializable
data class Conductor(
    val id: Int,
    @SerialName("local_id") val localId: String?,
    val forename: String?,
    val name: String?,
    val tags: List<String>? = emptyList()
)

@Serializable
data class FinalGrade(
    val id: Int,
    @SerialName("grade_value") val gradeValue: Double? = null,
    @SerialName("grade_verbal") val gradeVerbal: String? = null,
    val teacher: Teacher,
    val subject: Subject
)

@Serializable
data class Student(
    val id: Int,
    val name: String,
    val forename: String
)

@Serializable
data class Teacher(
    val id: Int,
    @SerialName("local_id") val localId: String?,
    val forename: String?,
    val name: String?
)

@Serializable
data class Subject(
    val id: String?,
    val name: String?,
    @SerialName("local_id") val localId: String?,
    @SerialName("for") val forType: String? = null,
    val tags: List<String>? = emptyList()
)

@Serializable
data class LessonStudentDetail(
    val id: Int,
    val present: Int,
    @SerialName("too_late") val tooLate: Int? = null,
    @SerialName("missing_homework") val missingHomework: Int? = null,
    @SerialName("missing_equipment") val missingEquipment: Int? = null,
    val lesson: LessonDetail,
    val notes: List<JsonObject>? = emptyList()
)

@Serializable
data class LessonDetail(
    val id: Int,
    val nr: Int,
    val status: String,
    val subject: Subject,
    val teachers: List<Teacher>,
    val rooms: List<Room>,
    val day: DayInfo
)

@Serializable
data class Room(
    val id: Int,
    @SerialName("local_id") val localId: String?
)

@Serializable
data class DayInfo(
    val id: String,
    val date: String,
    @SerialName("time_name") val timeName: String?
)

@Serializable
data class LessonStudentStats(
    val count: Int,
    @SerialName("not_present_count") val notPresentCount: Int,
    @SerialName("not_present_with_absence_count") val notPresentWithAbsenceCount: Int,
    @SerialName("too_late_sum") val tooLateSum: Int? = null,
    @SerialName("too_late_with_absence_sum") val tooLateWithAbsenceSum: Int,
    @SerialName("too_early_sum") val tooEarlySum: Int? = null,
    @SerialName("too_early_with_absence_sum") val tooEarlyWithAbsenceSum: Int,
    @SerialName("missing_equipment_sum") val missingEquipmentSum: Int,
    @SerialName("missing_homework_sum") val missingHomeworkSum: Int,
    @SerialName("student_id") val studentId: Int,
)

@Serializable
data class DayStudentStats(
    val count: Int,
    @SerialName("lessons_count") val lessonsCount: String,
    @SerialName("not_present_count") val notPresentCount: Int,
    @SerialName("not_present_with_absence_count") val notPresentWithAbsenceCount: Int,
    @SerialName("not_present_without_absence_count") val notPresentWithoutAbsenceCount: Int,
    @SerialName("lessons_not_present_with_absence_count") val lessonsNotPresentWithAbsenceCount: String,
    @SerialName("lessons_not_present_without_absence_count") val lessonsNotPresentWithoutAbsenceCount: String,
    @SerialName("student_id") val studentId: Int,
)


// ---------------------------------------------------
// 3. Reduzierte API-Client-Klasse
// ---------------------------------------------------

class BesteSchuleApi(private val authToken: String) {

    private val client = createHttpClient().config {
        defaultRequest {
            url("https://beste.schule/api/")
            bearerAuth(authToken)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

    /**
     * Ruft alle detaillierten Informationen zum aktuell authentifizierten Benutzer ab.
     *
     * @return Ein `ApiResponse`-Objekt, das das `UserDetail`-Objekt enthält.
     */
    suspend fun getUser(): ApiResponse<UserDetail> = client.get("user").body()

    /**
     * Ruft eine Liste aller verfügbaren Schuljahre samt ihrer Intervalle (Halbjahre) ab.
     */
    suspend fun getYears(): PaginatedApiResponse<Year> = client.get("years").body()

    /**
     * Setzt das aktive Schuljahr für den aktuellen Benutzer.
     *
     * @param yearId Die ID des Jahres, das als aktuell gesetzt werden soll.
     */
    suspend fun setCurrentYear(yearId: Int? = null): HttpResponse {
        return client.post("years/current") {
            if (yearId != null) {
                setBody(buildJsonObject { put("id", yearId) })
            }
        }
    }

    /**
     * Ruft eine paginierte Liste von Leistungsnachweisen (`collections`) ab.
     *
     * @param title Filtert nach exaktem Titel.
     * @param type Filtert nach Typ (z.B. "Klassenarbeit").
     * @param studentIds Filtert nach einer oder mehreren Schüler-IDs.
     * @param groupIds Filtert nach einer oder mehreren Gruppen-IDs.
     * @param intervalIds Filtert nach einer oder mehreren Intervall-IDs.
     * @param yearIds Filtert nach einer oder mehreren Jahrgangs-IDs.
     * @param subjectIds Filtert nach einer oder mehreren Fach-IDs.
     * @param teacherIds Filtert nach einer oder mehreren Lehrer-IDs.
     * @param include Fügt verknüpfte Daten hinzu. Mögliche Werte: "grades", "grades.histories", "subject", "interval", etc.
     * @param sort Sortierreihenfolge. Mögliche Werte: "id", "name", "given_at", "visible_from", etc.
     * @return Eine `PaginatedApiResponse`, die die Liste der Leistungsnachweise enthält.
     */
    suspend fun getCollections(
        title: String? = null,
        type: String? = null,
        studentIds: List<Int>? = null,
        groupIds: List<Int>? = null,
        intervalIds: List<Int>? = null,
        yearIds: List<Int>? = null,
        subjectIds: List<Int>? = null,
        teacherIds: List<Int>? = null,
        include: List<String>? = null,
        sort: List<String>? = null,
        page: Int? = null
    ): PaginatedApiResponse<GradeCollection> = client.get("collections") {
        title?.let { parameter("filter[title]", it) }
        type?.let { parameter("filter[type]", it) }
        studentIds?.let { parameter("filter[student]", it.joinToString(",")) }
        groupIds?.let { parameter("filter[group]", it.joinToString(",")) }
        intervalIds?.let { parameter("filter[interval]", it.joinToString(",")) }
        yearIds?.let { parameter("filter[year]", it.joinToString(",")) }
        subjectIds?.let { parameter("filter[subject]", it.joinToString(",")) }
        teacherIds?.let { parameter("filter[teacher]", it.joinToString(",")) }

        include?.let { parameter("include", it.joinToString(",")) }
        sort?.let { parameter("sort", it.joinToString(",")) }
        page?.let { parameter("page", it) }
    }.body()

    /**
     * Ruft eine paginierte Liste von Noten ab.
     *
     * @param gradeVerbal Filtert nach der verbalen Note (z.B. "gut").
     * @param gradeValue Filtert nach dem exakten Notenwert (z.B. 2.0).
     * @param gradeTendency Filtert nach der Tendenz ("+" oder "-").
     * @param intervalIds Filtert nach einer oder mehreren Intervall-IDs.
     * @param levelScope Filtert nach Level-IDs.
     * @param studentIds Filtert nach einer oder mehreren Schüler-IDs. **(Wichtig für die meisten Abfragen)**
     * @param groupIds Filtert nach einer oder mehreren Gruppen-IDs.
     * @param include Fügt verknüpfte Daten hinzu. Mögliche Werte: "student", "teacher", "collection", "collection.subject", "histories".
     * @param sort Sortierreihenfolge. Mögliche Werte: "id", "value", "given_at", "student". Vorsatz "-" für absteigend.
     * @param page Die abzurufende Seite für die Paginierung.
     * @return Eine `PaginatedApiResponse`, die die Liste der Noten und Paginierungsinformationen enthält.
     */
    suspend fun getGrades(
        gradeVerbal: String? = null,
        gradeValue: Double? = null,
        gradeTendency: String? = null,
        intervalIds: List<Int>? = null,
        levelScope: List<Int>? = null,
        studentIds: List<Int>? = null,
        groupIds: List<Int>? = null,
        include: List<String>? = null,
        sort: List<String>? = null,
        page: Int? = null
    ): PaginatedApiResponse<Grade> = client.get("grades") {
        // Filter-Parameter hinzufügen, falls sie nicht null sind
        gradeVerbal?.let { parameter("filter[grade_verbal]", it) }
        gradeValue?.let { parameter("filter[grade_value]", it) }
        gradeTendency?.let { parameter("filter[grade_tendency]", it) }
        intervalIds?.let { parameter("filter[interval]", it.joinToString(",")) }
        levelScope?.let { parameter("filter[level]", it.joinToString(",")) }
        studentIds?.let { parameter("filter[student]", it.joinToString(",")) }
        groupIds?.let { parameter("filter[group]", it.joinToString(",")) }

        // Include- und Sort-Parameter
        include?.let { parameter("include", it.joinToString(",")) }
        sort?.let { parameter("sort", it.joinToString(",")) }

        // Paginierungs-Parameter
        page?.let { parameter("page", it) }
    }.body()

    suspend fun getFinalGrades(
        subjectIds: List<Int>? = null,
        intervalIds: List<Int>? = null,
        page: Int? = null
    ): PaginatedApiResponse<FinalGrade> = client.get("finalgrades") {
        subjectIds?.let { parameter("filter[subject]", it.joinToString(",")) }
        intervalIds?.let { parameter("filter[interval]", it.joinToString(",")) }
        page?.let { parameter("page", it) }
    }.body()

    /**
     * Ruft eine paginierte Liste von Fächern ab.
     *
     * @param localId Filtert nach dem Kürzel des Fachs.
     * @param name Filtert nach dem Namen des Fachs.
     * @param studentIds Schränkt die Fächer auf die eines bestimmten Schülers ein.
     * @param groupIds Schränkt die Fächer auf die einer bestimmten Gruppe ein.
     * @param sort Sortierreihenfolge. Mögliche Werte: "local_id", "name".
     * @return Eine `PaginatedApiResponse`, die die Liste der Fächer enthält.
     */
    suspend fun getSubjects(
        localId: String? = null,
        name: String? = null,
        studentIds: List<Int>? = null,
        groupIds: List<Int>? = null,
        sort: List<String>? = null,
        page: Int? = null
    ): PaginatedApiResponse<Subject> = client.get("subjects") {
        localId?.let { parameter("filter[local_id]", it) }
        name?.let { parameter("filter[name]", it) }
        studentIds?.let { parameter("filter[student]", it.joinToString(",")) }
        groupIds?.let { parameter("filter[group]", it.joinToString(",")) }
        sort?.let { parameter("sort", it.joinToString(",")) }
        page?.let { parameter("page", it) }
    }.body()

    suspend fun getLessonStudentDetails(
        fromDate: String? = null,
        toDate: String? = null,
        yearIds: List<Int>? = null,
        subjectIds: List<Int>? = null,
        page: Int? = null
    ): PaginatedApiResponse<LessonStudentDetail> = client.get("journal/lesson-student") {
        fromDate?.let { parameter("filter[range][from]", it) }
        toDate?.let { parameter("filter[range][to]", it) }
        yearIds?.let { parameter("filter[year]", it.joinToString(",")) }
        subjectIds?.let { parameter("filter[subject]", it.joinToString(",")) }
        page?.let { parameter("page", it) }
    }.body()

    suspend fun getLessonStudentStats(
    ): PaginatedApiResponse<LessonStudentStats> = client.get("journal/lesson-student") {
        parameter("count", true)
    }.body()

    suspend fun getDayStudentStats(): PaginatedApiResponse<DayStudentStats> = client.get("journal/day-student") {
        parameter("count", true)
    }.body()
}