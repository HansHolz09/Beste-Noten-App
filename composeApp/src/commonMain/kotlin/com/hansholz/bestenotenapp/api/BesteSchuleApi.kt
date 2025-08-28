@file:Suppress("unused")

package com.hansholz.bestenotenapp.api

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.hansholz.bestenotenapp.api.models.Absence
import com.hansholz.bestenotenapp.api.models.AbsenceBatch
import com.hansholz.bestenotenapp.api.models.AbsenceType
import com.hansholz.bestenotenapp.api.models.AbsenceVerification
import com.hansholz.bestenotenapp.api.models.AddFirebaseDeviceUserRequest
import com.hansholz.bestenotenapp.api.models.AddMembershipUserRequest
import com.hansholz.bestenotenapp.api.models.Announcement
import com.hansholz.bestenotenapp.api.models.AnnouncementType
import com.hansholz.bestenotenapp.api.models.ArrayCollection
import com.hansholz.bestenotenapp.api.models.BatchFinalCertificateRequest
import com.hansholz.bestenotenapp.api.models.BatchGroupRequest
import com.hansholz.bestenotenapp.api.models.BatchGuardianRequest
import com.hansholz.bestenotenapp.api.models.BatchStudentRequest
import com.hansholz.bestenotenapp.api.models.BatchSubjectRequest
import com.hansholz.bestenotenapp.api.models.BatchTeacherRequest
import com.hansholz.bestenotenapp.api.models.BatchTokenGuardianRequest
import com.hansholz.bestenotenapp.api.models.BatchTokenGuardianStudentRequest
import com.hansholz.bestenotenapp.api.models.BatchTokenStudentRequest
import com.hansholz.bestenotenapp.api.models.BatchTokenTeacherRequest
import com.hansholz.bestenotenapp.api.models.CertificateGrade
import com.hansholz.bestenotenapp.api.models.ChangeSchoolUserRequest
import com.hansholz.bestenotenapp.api.models.Checklist
import com.hansholz.bestenotenapp.api.models.ChecklistType
import com.hansholz.bestenotenapp.api.models.DataWrapper
import com.hansholz.bestenotenapp.api.models.ExecuteNotificationActionRequest
import com.hansholz.bestenotenapp.api.models.Favorite
import com.hansholz.bestenotenapp.api.models.FinalCertificate
import com.hansholz.bestenotenapp.api.models.Finalgrade
import com.hansholz.bestenotenapp.api.models.FirebaseDevice
import com.hansholz.bestenotenapp.api.models.Grade
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.api.models.Group
import com.hansholz.bestenotenapp.api.models.GroupSubjectOrderResponse
import com.hansholz.bestenotenapp.api.models.Guardian
import com.hansholz.bestenotenapp.api.models.History
import com.hansholz.bestenotenapp.api.models.Importer
import com.hansholz.bestenotenapp.api.models.ImporterLog
import com.hansholz.bestenotenapp.api.models.ImporterStundenplan24
import com.hansholz.bestenotenapp.api.models.Interval
import com.hansholz.bestenotenapp.api.models.JournalDay
import com.hansholz.bestenotenapp.api.models.JournalDayStudent
import com.hansholz.bestenotenapp.api.models.JournalNote
import com.hansholz.bestenotenapp.api.models.JournalNoteType
import com.hansholz.bestenotenapp.api.models.JournalWeek
import com.hansholz.bestenotenapp.api.models.Level
import com.hansholz.bestenotenapp.api.models.ListDataWrapper
import com.hansholz.bestenotenapp.api.models.MarkReadAnnouncementRequest
import com.hansholz.bestenotenapp.api.models.Note
import com.hansholz.bestenotenapp.api.models.NoteType
import com.hansholz.bestenotenapp.api.models.Notification
import com.hansholz.bestenotenapp.api.models.PaginatedDataWrapper
import com.hansholz.bestenotenapp.api.models.PostPasswordUserRequest
import com.hansholz.bestenotenapp.api.models.PushImporterRequest
import com.hansholz.bestenotenapp.api.models.Report
import com.hansholz.bestenotenapp.api.models.ReportFilterResult
import com.hansholz.bestenotenapp.api.models.ReportResult
import com.hansholz.bestenotenapp.api.models.Room
import com.hansholz.bestenotenapp.api.models.School
import com.hansholz.bestenotenapp.api.models.SeatingPlan
import com.hansholz.bestenotenapp.api.models.SetCurrentYearRequest
import com.hansholz.bestenotenapp.api.models.SetSubjectCalculationStudentRequest
import com.hansholz.bestenotenapp.api.models.SimplePasswordResponse
import com.hansholz.bestenotenapp.api.models.SimpleSecretResponse
import com.hansholz.bestenotenapp.api.models.SimpleSuccessResponse
import com.hansholz.bestenotenapp.api.models.SimpleVerifiedResponse
import com.hansholz.bestenotenapp.api.models.SiteStatusResponse
import com.hansholz.bestenotenapp.api.models.StoreAbsenceBatchRequest
import com.hansholz.bestenotenapp.api.models.StoreAbsenceTypeRequest
import com.hansholz.bestenotenapp.api.models.StoreAbsenceVerificationRequest
import com.hansholz.bestenotenapp.api.models.StoreAnnouncementRequest
import com.hansholz.bestenotenapp.api.models.StoreAnnouncementTypeRequest
import com.hansholz.bestenotenapp.api.models.StoreCertificateGradeRequest
import com.hansholz.bestenotenapp.api.models.StoreChecklistRequest
import com.hansholz.bestenotenapp.api.models.StoreChecklistTypeRequest
import com.hansholz.bestenotenapp.api.models.StoreCollectionRequest
import com.hansholz.bestenotenapp.api.models.StoreFavoriteRequest
import com.hansholz.bestenotenapp.api.models.StoreFinalCertificateRequest
import com.hansholz.bestenotenapp.api.models.StoreFinalgradeRequest
import com.hansholz.bestenotenapp.api.models.StoreForDayJournalNoteRequest
import com.hansholz.bestenotenapp.api.models.StoreForLessonJournalNoteRequest
import com.hansholz.bestenotenapp.api.models.StoreForLessonStudentJournalNoteRequest
import com.hansholz.bestenotenapp.api.models.StoreForWeekJournalNoteRequest
import com.hansholz.bestenotenapp.api.models.StoreGradeRequest
import com.hansholz.bestenotenapp.api.models.StoreGroupRequest
import com.hansholz.bestenotenapp.api.models.StoreGuardianRequest
import com.hansholz.bestenotenapp.api.models.StoreImporterRequest
import com.hansholz.bestenotenapp.api.models.StoreImporterStundenplan24Request
import com.hansholz.bestenotenapp.api.models.StoreIntervalRequest
import com.hansholz.bestenotenapp.api.models.StoreJournalDayStudentRequest
import com.hansholz.bestenotenapp.api.models.StoreJournalNoteRequest
import com.hansholz.bestenotenapp.api.models.StoreJournalNoteTypeRequest
import com.hansholz.bestenotenapp.api.models.StoreLevelRequest
import com.hansholz.bestenotenapp.api.models.StoreNoteRequest
import com.hansholz.bestenotenapp.api.models.StoreNoteTypeRequest
import com.hansholz.bestenotenapp.api.models.StoreOrUpdateJournalDayRequest
import com.hansholz.bestenotenapp.api.models.StoreSchoolRequest
import com.hansholz.bestenotenapp.api.models.StoreStudentRequest
import com.hansholz.bestenotenapp.api.models.StoreSubjectRequest
import com.hansholz.bestenotenapp.api.models.StoreSubstitutionPlanRequest
import com.hansholz.bestenotenapp.api.models.StoreTagRequest
import com.hansholz.bestenotenapp.api.models.StoreTeacherRequest
import com.hansholz.bestenotenapp.api.models.StoreTimeTableRequest
import com.hansholz.bestenotenapp.api.models.StoreTimeTableTimeLessonRequest
import com.hansholz.bestenotenapp.api.models.StoreTimeTableTimeRequest
import com.hansholz.bestenotenapp.api.models.StoreUpdateIntervalStudentRequest
import com.hansholz.bestenotenapp.api.models.StoreYearRequest
import com.hansholz.bestenotenapp.api.models.Student
import com.hansholz.bestenotenapp.api.models.Subject
import com.hansholz.bestenotenapp.api.models.SubstitutionPlan
import com.hansholz.bestenotenapp.api.models.SubstitutionPlanDay
import com.hansholz.bestenotenapp.api.models.SubstitutionPlanLesson
import com.hansholz.bestenotenapp.api.models.Tag
import com.hansholz.bestenotenapp.api.models.Teacher
import com.hansholz.bestenotenapp.api.models.TimeTable
import com.hansholz.bestenotenapp.api.models.TimeTableTime
import com.hansholz.bestenotenapp.api.models.TimeTableTimeLesson
import com.hansholz.bestenotenapp.api.models.UpdateAbsenceBatchRequest
import com.hansholz.bestenotenapp.api.models.UpdateAbsenceTypeRequest
import com.hansholz.bestenotenapp.api.models.UpdateAbsenceVerificationRequest
import com.hansholz.bestenotenapp.api.models.UpdateAnnouncementRequest
import com.hansholz.bestenotenapp.api.models.UpdateAnnouncementTypeRequest
import com.hansholz.bestenotenapp.api.models.UpdateCertificateGradeRequest
import com.hansholz.bestenotenapp.api.models.UpdateChecklistRequest
import com.hansholz.bestenotenapp.api.models.UpdateChecklistTypeRequest
import com.hansholz.bestenotenapp.api.models.UpdateCollectionRequest
import com.hansholz.bestenotenapp.api.models.UpdateFavoriteRequest
import com.hansholz.bestenotenapp.api.models.UpdateFinalCertificateRequest
import com.hansholz.bestenotenapp.api.models.UpdateFinalgradeRequest
import com.hansholz.bestenotenapp.api.models.UpdateGradeRequest
import com.hansholz.bestenotenapp.api.models.UpdateGuardianRequest
import com.hansholz.bestenotenapp.api.models.UpdateImporterRequest
import com.hansholz.bestenotenapp.api.models.UpdateImporterStundenplan24Request
import com.hansholz.bestenotenapp.api.models.UpdateIntervalRequest
import com.hansholz.bestenotenapp.api.models.UpdateJournalLessonStudentRequest
import com.hansholz.bestenotenapp.api.models.UpdateJournalNoteRequest
import com.hansholz.bestenotenapp.api.models.UpdateJournalNoteTypeRequest
import com.hansholz.bestenotenapp.api.models.UpdateLevelRequest
import com.hansholz.bestenotenapp.api.models.UpdateNoteRequest
import com.hansholz.bestenotenapp.api.models.UpdateNoteTypeRequest
import com.hansholz.bestenotenapp.api.models.UpdateSchoolRequest
import com.hansholz.bestenotenapp.api.models.UpdateSubstitutionPlanRequest
import com.hansholz.bestenotenapp.api.models.UpdateTagRequest
import com.hansholz.bestenotenapp.api.models.UpdateTeacherRequest
import com.hansholz.bestenotenapp.api.models.UpdateTimeTableRequest
import com.hansholz.bestenotenapp.api.models.UpdateTimeTableTimeLessonRequest
import com.hansholz.bestenotenapp.api.models.UpdateTimeTableTimeRequest
import com.hansholz.bestenotenapp.api.models.UpdateUserRequest
import com.hansholz.bestenotenapp.api.models.UpdateYearRequest
import com.hansholz.bestenotenapp.api.models.User
import com.hansholz.bestenotenapp.api.models.VerifyTwoFactorUserRequest
import com.hansholz.bestenotenapp.api.models.Year
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonObject

class BesteSchuleApi(httpClient: HttpClient, authToken: MutableState<String?>, studentId: MutableState<String?> = mutableStateOf(null)) {
    private val baseUrl = "https://beste.schule/api"

    private val client = httpClient.config {
        install(studentFilterPlugin(studentId))
        defaultRequest {
            authToken.value?.let { bearerAuth(it) }
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

    suspend fun absencesStore(requestBody: Absence): DataWrapper<Absence> {
        return client.post("$baseUrl/absences") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun absencesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Absence> {
        return client.get("$baseUrl/absences") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun absencesUpdate(absence: Int, requestBody: Absence): DataWrapper<Absence> {
        return client.put("$baseUrl/absences/$absence") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun absencesDestroy(absence: Int) {
        client.delete("$baseUrl/absences/$absence")
    }

    suspend fun absencesShow(absence: Int): DataWrapper<Absence> {
        return client.get("$baseUrl/absences/$absence").body()
    }

    suspend fun absenceBatchesStore(requestBody: StoreAbsenceBatchRequest): DataWrapper<AbsenceBatch> {
        return client.post("$baseUrl/absence-batches") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun absenceBatchesIndex(): ListDataWrapper<AbsenceBatch> {
        return client.get("$baseUrl/absence-batches").body()
    }

    suspend fun absenceBatchesUpdate(absenceBatch: Int, requestBody: UpdateAbsenceBatchRequest): DataWrapper<AbsenceBatch> {
        return client.put("$baseUrl/absence-batches/$absenceBatch") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun absenceBatchesDestroy(absenceBatch: Int) {
        client.delete("$baseUrl/absence-batches/$absenceBatch")
    }

    suspend fun absenceBatchesShow(absenceBatch: Int): DataWrapper<AbsenceBatch> {
        return client.get("$baseUrl/absence-batches/$absenceBatch").body()
    }

    suspend fun absenceTypeStore(requestBody: StoreAbsenceTypeRequest): DataWrapper<AbsenceType> {
        return client.post("$baseUrl/types/absence") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun absenceTypeIndex(): ListDataWrapper<AbsenceType> {
        return client.get("$baseUrl/types/absence").body()
    }

    suspend fun absenceTypeShow(id: String): DataWrapper<AbsenceType> {
        return client.get("$baseUrl/types/absence/$id").body()
    }

    suspend fun absenceTypeUpdate(id: String, requestBody: UpdateAbsenceTypeRequest): DataWrapper<AbsenceType> {
        return client.put("$baseUrl/types/absence/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun absenceTypeDestroy(id: String) {
        client.delete("$baseUrl/types/absence/$id")
    }

    suspend fun absencesVerificationsStore(absence: Int, requestBody: StoreAbsenceVerificationRequest? = null): DataWrapper<AbsenceVerification> {
        return client.post("$baseUrl/absences/$absence/verifications") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun verificationsShow(id: String): DataWrapper<AbsenceVerification> {
        return client.get("$baseUrl/absences/verifications/$id").body()
    }

    suspend fun verificationsUpdate(verification: String, requestBody: UpdateAbsenceVerificationRequest? = null): DataWrapper<AbsenceVerification> {
        return client.put("$baseUrl/absences/verifications/$verification") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun verificationsDestroy(verification: String) {
        client.delete("$baseUrl/absences/verifications/$verification")
    }

    suspend fun announcementsStore(requestBody: StoreAnnouncementRequest): DataWrapper<Announcement> {
        return client.post("$baseUrl/announcements") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun announcementsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Announcement> {
        return client.get("$baseUrl/announcements") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun announcementsUpdate(announcement: Int, requestBody: UpdateAnnouncementRequest): DataWrapper<Announcement> {
        return client.put("$baseUrl/announcements/$announcement") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun announcementsDestroy(announcement: Int) {
        client.delete("$baseUrl/announcements/$announcement")
    }

    suspend fun announcementMarkRead(announcement: Int, requestBody: MarkReadAnnouncementRequest? = null): DataWrapper<Announcement> {
        return client.post("$baseUrl/announcements/$announcement/read") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun announcementsShow(id: String): DataWrapper<Announcement> {
        return client.get("$baseUrl/announcements/$id").body()
    }

    suspend fun announcementTypeStore(requestBody: StoreAnnouncementTypeRequest): DataWrapper<AnnouncementType> {
        return client.post("$baseUrl/types/announcement") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun announcementTypeIndex(): ListDataWrapper<AnnouncementType> {
        return client.get("$baseUrl/types/announcement").body()
    }

    suspend fun announcementTypeShow(id: String): DataWrapper<AnnouncementType> {
        return client.get("$baseUrl/types/announcement/$id").body()
    }

    suspend fun announcementTypeUpdate(id: String, requestBody: UpdateAnnouncementTypeRequest): DataWrapper<AnnouncementType> {
        return client.put("$baseUrl/types/announcement/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun announcementTypeDestroy(id: String) {
        client.delete("$baseUrl/types/announcement/$id")
    }

    suspend fun certificateGradeRestore(certificateGradeId: String): DataWrapper<CertificateGrade> {
        return client.post("$baseUrl/certificate-grades/$certificateGradeId/restore").body()
    }

    suspend fun certificateGradesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<CertificateGrade> {
        return client.get("$baseUrl/certificate-grades") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun certificateGradesStore(requestBody: StoreCertificateGradeRequest): DataWrapper<CertificateGrade> {
        return client.post("$baseUrl/certificate-grades") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun certificateGradesShow(id: String): DataWrapper<CertificateGrade> {
        return client.get("$baseUrl/certificate-grades/$id").body()
    }

    suspend fun certificateGradesUpdate(certificateGrade: Int, requestBody: UpdateCertificateGradeRequest? = null): DataWrapper<CertificateGrade> {
        return client.put("$baseUrl/certificate-grades/$certificateGrade") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun certificateGradesDestroy(certificateGrade: Int) {
        client.delete("$baseUrl/certificate-grades/$certificateGrade")
    }

    suspend fun checklistsStore(requestBody: StoreChecklistRequest): DataWrapper<Checklist> {
        return client.post("$baseUrl/checklists") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun checklistsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Checklist> {
        return client.get("$baseUrl/checklists") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun checklistsUpdate(checklist: Int, requestBody: UpdateChecklistRequest): DataWrapper<Checklist> {
        return client.put("$baseUrl/checklists/$checklist") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun checklistsDestroy(checklist: Int) {
        client.delete("$baseUrl/checklists/$checklist")
    }

    suspend fun checklistsShow(id: String): DataWrapper<Checklist> {
        return client.get("$baseUrl/checklists/$id").body()
    }

    suspend fun checklistTypeStore(requestBody: StoreChecklistTypeRequest): DataWrapper<ChecklistType> {
        return client.post("$baseUrl/types/checklist") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun checklistTypeIndex(): ListDataWrapper<ChecklistType> {
        return client.get("$baseUrl/types/checklist").body()
    }

    suspend fun checklistTypeShow(id: String): DataWrapper<ChecklistType> {
        return client.get("$baseUrl/types/checklist/$id").body()
    }

    suspend fun checklistTypeUpdate(id: String, requestBody: UpdateChecklistTypeRequest): DataWrapper<ChecklistType> {
        return client.put("$baseUrl/types/checklist/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun checklistTypeDestroy(id: String) {
        client.delete("$baseUrl/types/checklist/$id")
    }

    suspend fun collectionRestore(id: String): DataWrapper<GradeCollection> {
        return client.post("$baseUrl/collections/$id/restore").body()
    }

    suspend fun collectionsStore(requestBody: StoreCollectionRequest): DataWrapper<GradeCollection> {
        return client.post("$baseUrl/collections") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun collectionsIndex(
        include: List<String>? = null,
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
        page: Int? = null
    ): ListDataWrapper<GradeCollection> {
        return client.get("$baseUrl/collections") {
            parameter("include", include?.joinToString(","))
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
            parameter("page", page)
        }.body()
    }

    suspend fun collectionsUpdate(id: String, requestBody: UpdateCollectionRequest): DataWrapper<GradeCollection> {
        return client.put("$baseUrl/collections/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun collectionsDestroy(id: String) {
        client.delete("$baseUrl/collections/$id")
    }

    suspend fun collectionsShow(id: String): DataWrapper<GradeCollection> {
        return client.get("$baseUrl/collections/$id").body()
    }

    suspend fun computeIntervalTypes(): ArrayCollection {
        return client.get("$baseUrl/types/interval").body()
    }

    suspend fun computeTimeTypes(): ArrayCollection {
        return client.get("$baseUrl/types/time").body()
    }

    suspend fun computeReportTypes(): ArrayCollection {
        return client.get("$baseUrl/types/report").body()
    }

    suspend fun computeTimeNames(): ArrayCollection {
        return client.get("$baseUrl/types/time-names").body()
    }

    suspend fun favoritesIndex(): ListDataWrapper<Favorite> {
        return client.get("$baseUrl/favorites").body()
    }

    suspend fun favoritesStore(requestBody: StoreFavoriteRequest? = null): DataWrapper<Favorite> {
        return client.post("$baseUrl/favorites") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun favoritesShow(favorite: Int): DataWrapper<Favorite> {
        return client.get("$baseUrl/favorites/$favorite").body()
    }

    suspend fun favoritesUpdate(favorite: Int, requestBody: UpdateFavoriteRequest? = null): DataWrapper<Favorite> {
        return client.put("$baseUrl/favorites/$favorite") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun favoritesDestroy(favorite: Int) {
        client.delete("$baseUrl/favorites/$favorite")
    }

    suspend fun finalCertificateRestore(certificateGradeId: String): DataWrapper<FinalCertificate> {
        return client.post("$baseUrl/final-certificates/$certificateGradeId/restore").body()
    }

    suspend fun finalCertificateBatch(requestBody: List<BatchFinalCertificateRequest>? = null): ListDataWrapper<FinalCertificate> {
        return client.post("$baseUrl/final-certificates/batch") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun finalCertificatesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<FinalCertificate> {
        return client.get("$baseUrl/final-certificates") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun finalCertificatesStore(requestBody: StoreFinalCertificateRequest): DataWrapper<FinalCertificate> {
        return client.post("$baseUrl/final-certificates") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun finalCertificatesShow(id: String): DataWrapper<FinalCertificate> {
        return client.get("$baseUrl/final-certificates/$id").body()
    }

    suspend fun finalCertificatesUpdate(finalCertificate: Int, requestBody: UpdateFinalCertificateRequest? = null): DataWrapper<FinalCertificate> {
        return client.put("$baseUrl/final-certificates/$finalCertificate") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun finalCertificatesDestroy(finalCertificate: Int) {
        client.delete("$baseUrl/final-certificates/$finalCertificate")
    }

    suspend fun finalgradesStore(requestBody: StoreFinalgradeRequest): DataWrapper<Finalgrade> {
        return client.post("$baseUrl/finalgrades") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun finalgradesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Finalgrade> {
        return client.get("$baseUrl/finalgrades") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun finalgradesUpdate(id: String, requestBody: UpdateFinalgradeRequest? = null): DataWrapper<Finalgrade> {
        return client.put("$baseUrl/finalgrades/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun finalgradesDestroy(id: String) {
        client.delete("$baseUrl/finalgrades/$id")
    }

    suspend fun finalgradesShow(id: String): DataWrapper<Finalgrade> {
        return client.get("$baseUrl/finalgrades/$id").body()
    }

    suspend fun gradeRestore(id: String): DataWrapper<Grade> {
        return client.post("$baseUrl/grades/$id/restore").body()
    }

    suspend fun gradesStore(requestBody: StoreGradeRequest): DataWrapper<Grade> {
        return client.post("$baseUrl/grades") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun gradesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Grade> {
        return client.get("$baseUrl/grades") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun gradesUpdate(id: String, requestBody: UpdateGradeRequest): DataWrapper<Grade> {
        return client.put("$baseUrl/grades/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun gradesDestroy(id: String) {
        client.delete("$baseUrl/grades/$id")
    }

    suspend fun gradesShow(id: String): DataWrapper<Grade> {
        return client.get("$baseUrl/grades/$id").body()
    }

    suspend fun groupBatch(requestBody: List<BatchGroupRequest>? = null): ListDataWrapper<Group> {
        return client.post("$baseUrl/groups/batch") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun groupSync() {
        client.post("$baseUrl/groups/sync")
    }

    suspend fun groupAddStudent(id: String): DataWrapper<Student> {
        return client.post("$baseUrl/groups/$id/students").body()
    }

    suspend fun groupRemoveStudent(id: String) {
        client.delete("$baseUrl/groups/$id/students")
    }

    suspend fun groupAddSubject(id: String): DataWrapper<Subject> {
        return client.post("$baseUrl/groups/$id/subjects").body()
    }

    suspend fun groupRemoveSubject(id: String) {
        client.delete("$baseUrl/groups/$id/subjects")
    }

    suspend fun groupRestore(id: String): DataWrapper<Group> {
        return client.post("$baseUrl/groups/$id/restore").body()
    }

    suspend fun groupsStore(requestBody: StoreGroupRequest): DataWrapper<Group> {
        return client.post("$baseUrl/groups") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun groupsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Group> {
        return client.get("$baseUrl/groups") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun groupsDestroy(id: String) {
        client.delete("$baseUrl/groups/$id")
    }

    fun groupsUpdate(id: String): DataWrapper<Group> {
        throw NotImplementedError("Request body schema for groupsUpdate is missing in the OpenAPI spec.")
    }

    suspend fun groupSetOrderCollectionType(id: String, subjectId: String): DataWrapper<GroupSubjectOrderResponse> {
        return client.post("$baseUrl/groups/$id/subjects/$subjectId/order").body()
    }

    suspend fun groupsShow(group: Int): DataWrapper<Group> {
        return client.get("$baseUrl/groups/$group").body()
    }

    suspend fun guardiansIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Guardian> {
        return client.get("$baseUrl/guardians") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun guardiansStore(requestBody: StoreGuardianRequest): DataWrapper<Guardian> {
        return client.post("$baseUrl/guardians") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun guardiansShow(id: String): DataWrapper<Guardian> {
        return client.get("$baseUrl/guardians/$id").body()
    }

    suspend fun guardiansUpdate(id: String, requestBody: UpdateGuardianRequest? = null): DataWrapper<Guardian> {
        return client.put("$baseUrl/guardians/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun guardiansDestroy(id: Int) {
        client.delete("$baseUrl/guardians/$id")
    }

    suspend fun guardianBatchToken(requestBody: BatchTokenGuardianRequest): ListDataWrapper<Guardian> {
        return client.post("$baseUrl/guardians/token") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun guardianBatch(requestBody: List<BatchGuardianRequest>? = null): ListDataWrapper<Guardian> {
        return client.post("$baseUrl/guardians/batch") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun guardianRefreshToken(id: String): DataWrapper<Guardian> {
        return client.get("$baseUrl/guardians/$id/token").body()
    }

    suspend fun guardianRemoveToken(id: String): DataWrapper<Guardian> {
        return client.delete("$baseUrl/guardians/$id/token").body()
    }

    suspend fun guardianRestore(id: String): DataWrapper<Guardian> {
        return client.post("$baseUrl/guardians/$id/restore").body()
    }

    suspend fun guardianRemoveUser(guardian: Int): DataWrapper<Guardian> {
        return client.delete("$baseUrl/guardians/$guardian/user").body()
    }

    suspend fun historyShowSchool(): PaginatedDataWrapper<History> {
        return client.get("$baseUrl/histories").body()
    }

    suspend fun historyIndexGroupSubject(): Any {
        return client.get("$baseUrl/histories/table/group-subject").body()
    }

    suspend fun historyShowTable(table: String): PaginatedDataWrapper<History> {
        return client.get("$baseUrl/histories/table/$table").body()
    }

    suspend fun historyShowId(table: String, id: String): PaginatedDataWrapper<History> {
        return client.get("$baseUrl/histories/table/$table/$id").body()
    }

    suspend fun homeNothing() {
        client.get("$baseUrl/user/extend-session")
    }

    suspend fun importerLoad(importer: Int): DataWrapper<Importer> {
        return client.post("$baseUrl/importers/$importer/load").body()
    }

    suspend fun importerLoadTimeTables(importer: Int): DataWrapper<Importer> {
        return client.post("$baseUrl/importers/$importer/load/time-tables").body()
    }

    suspend fun importerLoadSubstitutionPlans(importer: Int): DataWrapper<Importer> {
        return client.post("$baseUrl/importers/$importer/load/substitutionplans").body()
    }

    suspend fun importerIndexLogs(importer: Int): PaginatedDataWrapper<ImporterLog> {
        return client.get("$baseUrl/importers/$importer/logs").body()
    }

    suspend fun importersIndex(): ListDataWrapper<Importer> {
        return client.get("$baseUrl/importers").body()
    }

    suspend fun importersStore(requestBody: StoreImporterRequest): DataWrapper<Importer> {
        return client.post("$baseUrl/importers") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun importersShow(importer: Int): DataWrapper<Importer> {
        return client.get("$baseUrl/importers/$importer").body()
    }

    suspend fun importersUpdate(importer: Int, requestBody: UpdateImporterRequest): DataWrapper<Importer> {
        return client.put("$baseUrl/importers/$importer") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun importersDestroy(importer: Int) {
        client.delete("$baseUrl/importers/$importer")
    }

    suspend fun importerPush(secret: String, requestBody: PushImporterRequest) {
        client.post("$baseUrl/importers/webhooks/$secret") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }
    }

    suspend fun importerStundenplan24Load(id: String): DataWrapper<ImporterStundenplan24> {
        return client.post("$baseUrl/importers/stundenplan24/$id/load").body()
    }

    suspend fun importerStundenplan24LoadTimeTable(id: String): DataWrapper<ImporterStundenplan24> {
        return client.post("$baseUrl/importers/stundenplan24/$id/load/time-table").body()
    }

    suspend fun importerStundenplan24LoadSubstitutionPlan(id: String): DataWrapper<ImporterStundenplan24> {
        return client.post("$baseUrl/importers/stundenplan24/$id/load/substitutionplan").body()
    }

    suspend fun importerStundenplan24IndexLogs(id: String): PaginatedDataWrapper<ImporterLog> {
        return client.get("$baseUrl/importers/stundenplan24/$id/logs").body()
    }

    suspend fun stundenplan24Index(): ListDataWrapper<ImporterStundenplan24> {
        return client.get("$baseUrl/importers/stundenplan24").body()
    }

    suspend fun stundenplan24Store(requestBody: StoreImporterStundenplan24Request): DataWrapper<ImporterStundenplan24> {
        return client.post("$baseUrl/importers/stundenplan24") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun stundenplan24Show(id: String): DataWrapper<ImporterStundenplan24> {
        return client.get("$baseUrl/importers/stundenplan24/$id").body()
    }

    suspend fun stundenplan24Update(id: String, requestBody: UpdateImporterStundenplan24Request): DataWrapper<ImporterStundenplan24> {
        return client.put("$baseUrl/importers/stundenplan24/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun stundenplan24Destroy(id: String) {
        client.delete("$baseUrl/importers/stundenplan24/$id")
    }

    suspend fun intervalRestore(id: String): DataWrapper<Interval> {
        return client.post("$baseUrl/intervals/$id/restore").body()
    }

    suspend fun intervalsStore(requestBody: StoreIntervalRequest): DataWrapper<Interval> {
        return client.post("$baseUrl/intervals") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun intervalIndex(): ListDataWrapper<Interval> {
        return client.get("$baseUrl/intervals").body()
    }

    suspend fun intervalsShow(id: String): DataWrapper<Interval> {
        return client.get("$baseUrl/intervals/$id").body()
    }

    suspend fun intervalsUpdate(id: String, requestBody: UpdateIntervalRequest): DataWrapper<Interval> {
        return client.put("$baseUrl/intervals/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun intervalsDestroy(id: String) {
        client.delete("$baseUrl/intervals/$id")
    }

    suspend fun journalDayStoreOrUpdatePost(date: String, requestBody: StoreOrUpdateJournalDayRequest? = null): DataWrapper<JournalDay> {
        return client.post("$baseUrl/journal/days/$date") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun journalDayStoreOrUpdatePut(date: String, requestBody: StoreOrUpdateJournalDayRequest? = null): DataWrapper<JournalDay> {
        return client.put("$baseUrl/journal/days/$date") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun journalDayDestroy(date: String) {
        client.delete("$baseUrl/journal/days/$date")
    }

    suspend fun journalDayShow(date: String, include: String? = null): DataWrapper<JournalDay> {
        return client.get("$baseUrl/journal/days/$date") {
            parameter("include", include)
        }.body()
    }

    suspend fun journalDayIndex(include: String? = null): ListDataWrapper<JournalDay> {
        return client.get("$baseUrl/journal/days") {
            parameter("include", include)
        }.body()
    }

    suspend fun dayStudentStore(requestBody: StoreJournalDayStudentRequest): DataWrapper<JournalDayStudent> {
        return client.post("$baseUrl/journal/day-student") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun dayStudentIndex(): Any {
        return client.get("$baseUrl/journal/day-student").body()
    }

    suspend fun dayStudentUpdate(id: String, requestBody: UpdateJournalLessonStudentRequest? = null): DataWrapper<JournalDayStudent> {
        return client.put("$baseUrl/journal/day-student/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun dayStudentDestroy(id: String) {
        client.delete("$baseUrl/journal/day-student/$id")
    }

    suspend fun dayStudentShow(id: String): DataWrapper<JournalDayStudent> {
        return client.get("$baseUrl/journal/day-student/$id").body()
    }

    suspend fun journalNoteStoreForWeek(nr: String, requestBody: StoreForWeekJournalNoteRequest): DataWrapper<JournalNote> {
        return client.post("$baseUrl/journal/weeks/$nr/notes") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun journalNoteStoreForDay(date: String, requestBody: StoreForDayJournalNoteRequest): DataWrapper<JournalNote> {
        return client.post("$baseUrl/journal/days/$date/notes") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun journalNoteStoreForLesson(id: String, requestBody: StoreForLessonJournalNoteRequest): DataWrapper<JournalNote> {
        return client.post("$baseUrl/journal/lessons/$id/notes") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun journalNoteStoreForLessonStudent(lesson: String, student: String, requestBody: StoreForLessonStudentJournalNoteRequest): DataWrapper<JournalNote> {
        return client.post("$baseUrl/journal/lessons/$lesson/students/$student/notes") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun journalNoteStore(requestBody: StoreJournalNoteRequest): DataWrapper<JournalNote> {
        return client.post("$baseUrl/journal/notes") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun notesShow(id: String): DataWrapper<JournalNote> {
        return client.get("$baseUrl/journal/notes/$id").body()
    }

    suspend fun journalNoteUpdate(id: String, requestBody: UpdateJournalNoteRequest): DataWrapper<JournalNote> {
        return client.put("$baseUrl/journal/notes/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun journalNoteDestroy(id: String) {
        client.delete("$baseUrl/journal/notes/$id")
    }

    suspend fun journalNotesStore(requestBody: StoreJournalNoteTypeRequest): DataWrapper<JournalNoteType> {
        return client.post("$baseUrl/types/journal-notes") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun journalNoteTypeIndex(filter: String? = null): ListDataWrapper<JournalNoteType> {
        return client.get("$baseUrl/types/journal-notes") {
            parameter("filter", filter)
        }.body()
    }

    suspend fun journalNotesShow(id: String): DataWrapper<JournalNoteType> {
        return client.get("$baseUrl/types/journal-notes/$id").body()
    }

    suspend fun journalNotesUpdate(id: String, requestBody: UpdateJournalNoteTypeRequest): DataWrapper<JournalNoteType> {
        return client.put("$baseUrl/types/journal-notes/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun journalNotesDestroy(id: String) {
        client.delete("$baseUrl/types/journal-notes/$id")
    }

    suspend fun journalWeekStoreOrUpdatePost(nr: String): DataWrapper<JournalWeek> {
        return client.post("$baseUrl/journal/weeks/$nr").body()
    }

    suspend fun journalWeekStoreOrUpdatePut(nr: String): DataWrapper<JournalWeek> {
        return client.put("$baseUrl/journal/weeks/$nr").body()
    }

    suspend fun journalWeekDestroy(nr: String) {
        client.delete("$baseUrl/journal/weeks/$nr")
    }

    suspend fun journalWeekShow(
        nr: String,
        filterYear: String? = null,
        interpolate: Boolean? = null,
        include: String? = null,
    ): DataWrapper<JournalWeek> {
        return client.get("$baseUrl/journal/weeks/$nr") {
            filterYear?.let { parameter("filter[year]", filterYear) }
            interpolate?.let { parameter("interpolate", interpolate) }
            parameter("include", include)
        }.body()
    }

    suspend fun journalWeekIndex(): ListDataWrapper<JournalWeek> {
        return client.get("$baseUrl/journal/weeks").body()
    }

    suspend fun levelsStore(requestBody: StoreLevelRequest): DataWrapper<Level> {
        return client.post("$baseUrl/levels") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun levelsIndex(): ListDataWrapper<Level> {
        return client.get("$baseUrl/levels").body()
    }

    suspend fun levelsShow(id: String): DataWrapper<Level> {
        return client.get("$baseUrl/levels/$id").body()
    }

    suspend fun levelsUpdate(id: String, requestBody: UpdateLevelRequest): DataWrapper<Level> {
        return client.put("$baseUrl/levels/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun levelsDestroy(id: String) {
        client.delete("$baseUrl/levels/$id")
    }

    suspend fun noteStore(requestBody: StoreNoteRequest): DataWrapper<Note> {
        return client.post("$baseUrl/notes") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun notesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Note> {
        return client.get("$baseUrl/notes") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun noteUpdate(note: Int, requestBody: UpdateNoteRequest? = null): DataWrapper<Note> {
        return client.put("$baseUrl/notes/$note") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun noteDestroy(note: Int) {
        client.delete("$baseUrl/notes/$note")
    }

    suspend fun noteTypeStore(requestBody: StoreNoteTypeRequest): DataWrapper<NoteType> {
        return client.post("$baseUrl/types/notes") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun noteTypeIndex(): ListDataWrapper<NoteType> {
        return client.get("$baseUrl/types/notes").body()
    }

    suspend fun noteTypeUpdate(id: Int, requestBody: UpdateNoteTypeRequest? = null): DataWrapper<NoteType> {
        return client.put("$baseUrl/types/notes/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun noteTypeDestroy(id: String) {
        client.delete("$baseUrl/types/notes/$id")
    }

    suspend fun notificationsIndex(): ListDataWrapper<Notification> {
        return client.get("$baseUrl/notifications").body()
    }

    suspend fun notificationMarkRead(notification: String): Notification {
        return client.post("$baseUrl/notifications/$notification/read").body()
    }

    suspend fun notificationExecuteAction(notification: String, requestBody: ExecuteNotificationActionRequest): Notification {
        return client.post("$baseUrl/notifications/$notification/action") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    fun reportPreviewReport(): ReportResult {
        throw NotImplementedError("Request body schema for reportPreviewReport is missing in the OpenAPI spec.")
    }

    suspend fun reportReport(
        reportID: String,
        rangeFrom: String? = null,
        rangeTo: String? = null,
        clearCache: Boolean? = null,
        sleep: Int? = null,
        filterResult: ReportFilterResult? = null
    ): ReportResult {
        return client.get("$baseUrl/reports/$reportID/result") {
            parameter("range_from", rangeFrom)
            parameter("range_to", rangeTo)
            parameter("clear_cache", clearCache)
            parameter("sleep", sleep)
            parameter("filter_result", filterResult)
        }.body()
    }

    suspend fun reportsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Report> {
        return client.get("$baseUrl/reports") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    fun reportsStore(): DataWrapper<Report> {
        throw NotImplementedError("Request body schema for reportsStore is missing in the OpenAPI spec.")
    }

    suspend fun reportsShow(id: String): DataWrapper<Report> {
        return client.get("$baseUrl/reports/$id").body()
    }

    fun reportsUpdate(id: String): DataWrapper<Report> {
        throw NotImplementedError("Request body schema for reportsUpdate is missing in the OpenAPI spec.")
    }

    suspend fun reportsDestroy(id: String) {
        client.delete("$baseUrl/reports/$id")
    }

    suspend fun roomsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Room> {
        return client.get("$baseUrl/rooms") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun roomsShow(id: String): DataWrapper<Room> {
        return client.get("$baseUrl/rooms/$id").body()
    }

    suspend fun schoolUpdate(requestBody: UpdateSchoolRequest): DataWrapper<School> {
        return client.put("$baseUrl/school") {
            setBody(MultiPartFormDataContent(formData {
                append("name", requestBody.name)
                append("email", requestBody.email)
                requestBody.customer?.let { append("customer", it) }
                requestBody.type?.let { append("type", it) }
                requestBody.postalSecondLine?.let { append("postal_second_line", it) }
                append("street", requestBody.street)
                append("street_nr", requestBody.streetNr)
                append("postal_code", requestBody.postalCode)
                append("city", requestBody.city)
                append("state", requestBody.state)
                requestBody.billingName?.let { append("billing_name", it) }
                requestBody.billingPostalSecondLine?.let { append("billing_postal_second_line", it) }
                requestBody.billingStreet?.let { append("billing_street", it) }
                requestBody.billingStreetNr?.let { append("billing_street_nr", it) }
                requestBody.billingPostalCode?.let { append("billing_postal_code", it) }
                requestBody.billingCity?.let { append("billing_city", it) }
                append("modules", requestBody.modules?.joinToString(",") ?: "")
                requestBody.logo?.let {
                    append(
                        "logo",
                        it,
                        Headers.build { append(HttpHeaders.ContentType, ContentType.Application.OctetStream) })
                }
                requestBody.logoUrl?.let { append("logo_url", it) }
                append("admin_name", requestBody.adminName)
                append("admin_email", requestBody.adminEmail)
                append("headteacher_name", requestBody.headteacherName)
            }))
        }.body()
    }

    suspend fun schoolShow(): DataWrapper<School> {
        return client.get("$baseUrl/school").body()
    }

    fun schoolAddUser(): DataWrapper<User> {
        throw NotImplementedError("Request body schema for schoolAddUser is missing in the OpenAPI spec.")
    }

    suspend fun schoolRemoveUser(user: Int) {
        client.delete("$baseUrl/school/users/$user")
    }

    suspend fun schoolsIndex(): ListDataWrapper<School> {
        return client.get("$baseUrl/schools").body()
    }

    suspend fun schoolsStore(requestBody: StoreSchoolRequest): DataWrapper<School> {
        return client.post("$baseUrl/schools") {
            setBody(MultiPartFormDataContent(formData {
                append("name", requestBody.name)
                append("email", requestBody.email)
                requestBody.customer?.let { append("customer", it) }
                requestBody.type?.let { append("type", it) }
                requestBody.postalSecondLine?.let { append("postal_second_line", it) }
                append("street", requestBody.street)
                append("street_nr", requestBody.streetNr)
                append("postal_code", requestBody.postalCode)
                append("city", requestBody.city)
                append("state", requestBody.state)
                requestBody.billingName?.let { append("billing_name", it) }
                requestBody.billingPostalSecondLine?.let { append("billing_postal_second_line", it) }
                requestBody.billingStreet?.let { append("billing_street", it) }
                requestBody.billingStreetNr?.let { append("billing_street_nr", it) }
                requestBody.billingPostalCode?.let { append("billing_postal_code", it) }
                requestBody.billingCity?.let { append("billing_city", it) }
                append("modules", requestBody.modules.joinToString(","))
                requestBody.logo?.let { append("logo", it, Headers.build { append(HttpHeaders.ContentType, ContentType.Application.OctetStream) }) }
                append("admin_name", requestBody.adminName)
                append("admin_email", requestBody.adminEmail)
                append("headteacher_name", requestBody.headteacherName)
            }))
        }.body()
    }

    suspend fun schoolGetLogo(extension: String? = null): String {
        return client.get("$baseUrl/school/logo${extension?.let { ".$it" } ?: ""}").body()
    }

    fun seatingPlansStore(): DataWrapper<SeatingPlan> {
        throw NotImplementedError("Request body schema for seatingPlansStore is missing in the OpenAPI spec.")
    }

    suspend fun seatingPlansIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<SeatingPlan> {
        return client.get("$baseUrl/seating-plans") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    fun seatingPlansUpdate(seatingPlan: Int): DataWrapper<SeatingPlan> {
        throw NotImplementedError("Request body schema for seatingPlansUpdate is missing in the OpenAPI spec.")
    }

    suspend fun seatingPlansDestroy(seatingPlan: Int) {
        client.delete("$baseUrl/seating-plans/$seatingPlan")
    }

    suspend fun seatingPlansShow(seatingPlan: Int): DataWrapper<SeatingPlan> {
        return client.get("$baseUrl/seating-plans/$seatingPlan").body()
    }

    suspend fun siteStatusIndex(): DataWrapper<SiteStatusResponse> {
        return client.get("$baseUrl/status").body()
    }

    suspend fun studentRefreshToken(id: String): DataWrapper<Student> {
        return client.get("$baseUrl/students/$id/token").body()
    }

    suspend fun studentRemoveToken(id: String): DataWrapper<Student> {
        return client.delete("$baseUrl/students/$id/token").body()
    }

    suspend fun studentBatchToken(requestBody: BatchTokenStudentRequest): ListDataWrapper<Student> {
        return client.post("$baseUrl/students/token") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun studentRefreshTokenGuardian(id: String): DataWrapper<Student> {
        return client.get("$baseUrl/students/$id/token-guardian").body()
    }

    suspend fun studentRemoveTokenGuardian(id: String): DataWrapper<Student> {
        return client.delete("$baseUrl/students/$id/token-guardian").body()
    }

    suspend fun studentBatchTokenGuardian(requestBody: BatchTokenGuardianStudentRequest): ListDataWrapper<Student> {
        return client.post("$baseUrl/students/token-guardian") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun studentDestroyInterval(id: String, interval: String): DataWrapper<Student> {
        return client.delete("$baseUrl/students/$id/intervals/$interval").body()
    }

    suspend fun studentStoreUpdateIntervalPost(id: String, interval: String, requestBody: StoreUpdateIntervalStudentRequest? = null): DataWrapper<Student> {
        return client.post("$baseUrl/students/$id/intervals/$interval") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun studentStoreUpdateIntervalPut(id: String, interval: String, requestBody: StoreUpdateIntervalStudentRequest? = null): DataWrapper<Student> {
        return client.put("$baseUrl/students/$id/intervals/$interval") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun studentRemoveGuardian(student: Int, guardian: Int): DataWrapper<Student> {
        return client.delete("$baseUrl/students/$student/guardians/$guardian").body()
    }

    suspend fun studentRestore(id: String): DataWrapper<Student> {
        return client.post("$baseUrl/students/$id/restore").body()
    }

    suspend fun studentRestoreInterval(id: String): DataWrapper<Student> {
        return client.post("$baseUrl/interval_student/$id/restore").body()
    }

    suspend fun studentBatch(requestBody: List<BatchStudentRequest>? = null): ListDataWrapper<Student> {
        return client.post("$baseUrl/students/batch") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun studentsStore(requestBody: StoreStudentRequest): DataWrapper<Student> {
        return client.post("$baseUrl/students") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun studentsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Student> {
        return client.get("$baseUrl/students") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    fun studentsUpdate(id: String): DataWrapper<Student> {
        throw NotImplementedError("Request body schema for studentsUpdate is missing in the OpenAPI spec.")
    }

    suspend fun studentsDestroy(id: String) {
        client.delete("$baseUrl/students/$id")
    }

    suspend fun studentsShow(id: String): DataWrapper<Student> {
        return client.get("$baseUrl/students/$id").body()
    }

    suspend fun studentSetSubjectCalculation(id: String, subjectId: String, requestBody: SetSubjectCalculationStudentRequest): JsonObject {
        return client.post("$baseUrl/students/$id/subjects/$subjectId/calculation") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun studentRemoveUser(studentId: String, userId: String): DataWrapper<Student> {
        return client.delete("$baseUrl/students/$studentId/user/$userId").body()
    }

    suspend fun subjectRestore(id: String): DataWrapper<Subject> {
        return client.post("$baseUrl/subjects/$id/restore").body()
    }

    suspend fun subjectBatch(requestBody: List<BatchSubjectRequest>? = null): ListDataWrapper<Subject> {
        return client.post("$baseUrl/subjects/batch") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun subjectsStore(requestBody: StoreSubjectRequest): DataWrapper<Subject> {
        return client.post("$baseUrl/subjects") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun subjectsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Subject> {
        return client.get("$baseUrl/subjects") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun subjectsShow(id: String): DataWrapper<Subject> {
        return client.get("$baseUrl/subjects/$id").body()
    }

    fun subjectsUpdate(id: String): DataWrapper<Subject> {
        throw NotImplementedError("Request body schema for subjectsUpdate is missing in the OpenAPI spec.")
    }

    suspend fun subjectsDestroy(id: String) {
        client.delete("$baseUrl/subjects/$id")
    }

    suspend fun substitutionPlansIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<SubstitutionPlan> {
        return client.get("$baseUrl/substitution-plans") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun substitutionPlansStore(requestBody: StoreSubstitutionPlanRequest): DataWrapper<SubstitutionPlan> {
        return client.post("$baseUrl/substitution-plans") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun substitutionPlansShow(id: String): DataWrapper<SubstitutionPlan> {
        return client.get("$baseUrl/substitution-plans/$id").body()
    }

    suspend fun substitutionPlansUpdate(id: String, requestBody: UpdateSubstitutionPlanRequest): DataWrapper<SubstitutionPlan> {
        return client.put("$baseUrl/substitution-plans/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun substitutionPlansDestroy(id: String) {
        client.delete("$baseUrl/substitution-plans/$id")
    }

    suspend fun substitutionPlanDayIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<SubstitutionPlanDay> {
        return client.get("$baseUrl/substitution-plans/days") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun substitutionPlanDayShow(date: String): DataWrapper<SubstitutionPlanDay> {
        return client.get("$baseUrl/substitution-plans/days/$date").body()
    }

    suspend fun substitutionPlanLessonIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<SubstitutionPlanLesson> {
        return client.get("$baseUrl/substitution-plans/lessons") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun tagsStore(requestBody: StoreTagRequest): DataWrapper<Tag> {
        return client.post("$baseUrl/types/tags") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun tagIndex(filter: String? = null): ListDataWrapper<Tag> {
        return client.get("$baseUrl/types/tags") {
            parameter("filter", filter)
        }.body()
    }

    suspend fun tagsShow(id: String): DataWrapper<Tag> {
        return client.get("$baseUrl/types/tags/$id").body()
    }

    suspend fun tagsUpdate(id: String, requestBody: UpdateTagRequest): DataWrapper<Tag> {
        return client.put("$baseUrl/types/tags/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun tagsDestroy(id: String) {
        client.delete("$baseUrl/types/tags/$id")
    }

    suspend fun teacherRefreshToken(id: String): DataWrapper<Teacher> {
        return client.get("$baseUrl/teachers/$id/token").body()
    }

    suspend fun teacherRemoveToken(id: String): DataWrapper<Teacher> {
        return client.delete("$baseUrl/teachers/$id/token").body()
    }

    suspend fun teacherBatchToken(requestBody: BatchTokenTeacherRequest): ListDataWrapper<Teacher> {
        return client.post("$baseUrl/teachers/token") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun teacherRestore(id: String): DataWrapper<Teacher> {
        return client.post("$baseUrl/teachers/$id/restore").body()
    }

    suspend fun teacherBatch(requestBody: List<BatchTeacherRequest>? = null): ListDataWrapper<Teacher> {
        return client.post("$baseUrl/teachers/batch") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun teachersStore(requestBody: StoreTeacherRequest): DataWrapper<Teacher> {
        return client.post("$baseUrl/teachers") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun teachersIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<Teacher> {
        return client.get("$baseUrl/teachers") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun teachersUpdate(id: String, requestBody: UpdateTeacherRequest): DataWrapper<Teacher> {
        return client.put("$baseUrl/teachers/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun teachersDestroy(id: String) {
        client.delete("$baseUrl/teachers/$id")
    }

    suspend fun teachersShow(id: String): DataWrapper<Teacher> {
        return client.get("$baseUrl/teachers/$id").body()
    }

    suspend fun teacherRemoveUser(id: String): DataWrapper<Teacher> {
        return client.delete("$baseUrl/teachers/$id/user").body()
    }

    suspend fun timeTablesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<TimeTable> {
        return client.get("$baseUrl/time-tables") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun timeTablesStore(requestBody: StoreTimeTableRequest): DataWrapper<TimeTable> {
        return client.post("$baseUrl/time-tables") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun timeTablesShow(id: String): DataWrapper<TimeTable> {
        return client.get("$baseUrl/time-tables/$id").body()
    }

    suspend fun timeTablesUpdate(id: String, requestBody: UpdateTimeTableRequest): DataWrapper<TimeTable> {
        return client.put("$baseUrl/time-tables/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun timeTablesDestroy(id: String) {
        client.delete("$baseUrl/time-tables/$id")
    }

    suspend fun timeTableShowCurrent(): DataWrapper<TimeTable> {
        return client.get("$baseUrl/time-tables/current").body()
    }

    suspend fun timeTableTimesStore(requestBody: StoreTimeTableTimeRequest): DataWrapper<TimeTableTime> {
        return client.post("$baseUrl/time-table-times") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun timeTableTimesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<TimeTableTime> {
        return client.get("$baseUrl/time-table-times") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun timeTableTimesUpdate(id: String, requestBody: UpdateTimeTableTimeRequest): DataWrapper<TimeTableTime> {
        return client.put("$baseUrl/time-table-times/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun timeTableTimesDestroy(id: String) {
        client.delete("$baseUrl/time-table-times/$id")
    }

    suspend fun timeTableTimesShow(id: String): DataWrapper<TimeTableTime> {
        return client.get("$baseUrl/time-table-times/$id").body()
    }

    suspend fun timeTableTimeLessonsStore(requestBody: StoreTimeTableTimeLessonRequest): DataWrapper<TimeTableTimeLesson> {
        return client.post("$baseUrl/time-table-time-lessons") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun timeTableTimeLessonsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<TimeTableTimeLesson> {
        return client.get("$baseUrl/time-table-time-lessons") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun timeTableTimeLessonsUpdate(id: String, requestBody: UpdateTimeTableTimeLessonRequest): DataWrapper<TimeTableTimeLesson> {
        return client.put("$baseUrl/time-table-time-lessons/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun timeTableTimeLessonsDestroy(id: String) {
        client.delete("$baseUrl/time-table-time-lessons/$id")
    }

    suspend fun timeTableTimeLessonsShow(id: String): DataWrapper<TimeTableTimeLesson> {
        return client.get("$baseUrl/time-table-time-lessons/$id").body()
    }

    suspend fun userDisableTwoFactorById(id: String? = null): SimpleSuccessResponse {
        return client.get("$baseUrl/users/$id/2fa/disable").body()
    }

    suspend fun userGetNewPassword(id: String): SimplePasswordResponse {
        return client.get("$baseUrl/users/$id/newpassword").body()
    }

    suspend fun userResendMailById(id: String? = null): SimpleSuccessResponse {
        return client.post("$baseUrl/users/$id/resend-mail").body()
    }

    suspend fun usersIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null
    ): ListDataWrapper<User> {
        return client.get("$baseUrl/users") {
            parameter("filter[group]", filterGroup)
            parameter("filter[student]", filterStudent)
            parameter("filter[guardian]", filterGuardian)
            parameter("filter[teacher]", filterTeacher)
            parameter("filter[subject]", filterSubject)
            parameter("filter[room]", filterRoom)
            parameter("filter[interval]", filterInterval)
            parameter("filter[year]", filterYear)
            parameter("filter[role]", filterRole)
        }.body()
    }

    suspend fun usersShow(id: Int? = null): DataWrapper<User> {
        return client.get("$baseUrl/users/$id").body()
    }

    suspend fun usersUpdate(id: Int? = null, requestBody: UpdateUserRequest? = null): DataWrapper<User> {
        return client.put("$baseUrl/users/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun usersDestroy(id: Int? = null) {
        client.delete("$baseUrl/users/$id")
    }

    suspend fun userMe(): DataWrapper<User> {
        return client.get("$baseUrl/me").body()
    }

    suspend fun userUpdateMe(requestBody: UpdateUserRequest? = null): DataWrapper<User> {
        return client.put("$baseUrl/user") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun userDestroyMe() {
        client.delete("$baseUrl/user")
    }

    suspend fun userAddMembership(requestBody: AddMembershipUserRequest): Any {
        return client.post("$baseUrl/user/token") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun userPostPassword(requestBody: PostPasswordUserRequest): DataWrapper<User> {
        return client.post("$baseUrl/user/password") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun userEnableTwoFactor(): SimpleSecretResponse {
        return client.get("$baseUrl/user/2fa/enable").body()
    }

    suspend fun userDisableTwoFactorMe(): SimpleSuccessResponse {
        return client.get("$baseUrl/user/2fa/disable").body()
    }

    suspend fun userVerifyTwoFactor(requestBody: VerifyTwoFactorUserRequest? = null): SimpleVerifiedResponse {
        return client.post("$baseUrl/user/2fa/verify") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun userAddFirebaseDevice(requestBody: AddFirebaseDeviceUserRequest): DataWrapper<FirebaseDevice> {
        return client.post("$baseUrl/user/firebase-device") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun userDeleteFirebaseDevice(id: String? = null, queryId: Int? = null, queryToken: String? = null): SimpleSuccessResponse {
        return client.delete("$baseUrl/user/firebase-device/$id") {
            parameter("id", queryId)
            parameter("token", queryToken)
        }.body()
    }

    suspend fun userDeleteSocialite(userSocialite: Int): SimpleSuccessResponse {
        return client.delete("$baseUrl/user/auth-provider/$userSocialite").body()
    }

    suspend fun userResendMailMe(): SimpleSuccessResponse {
        return client.post("$baseUrl/user/resend-mail").body()
    }

    suspend fun userChangeSchool(requestBody: ChangeSchoolUserRequest): DataWrapper<User> {
        return client.put("$baseUrl/user/school") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun userLogoutApiMe() {
        client.get("$baseUrl/me/logout")
    }

    suspend fun userLogoutApiUser() {
        client.get("$baseUrl/user/logout")
    }

    suspend fun yearIndex(): ListDataWrapper<Year> {
        return client.get("$baseUrl/years").body()
    }

    suspend fun yearsStore(requestBody: StoreYearRequest): DataWrapper<Year> {
        return client.post("$baseUrl/years") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun yearsShow(id: String): DataWrapper<Year> {
        return client.get("$baseUrl/years/$id").body()
    }

    suspend fun yearsUpdate(id: String, requestBody: UpdateYearRequest): DataWrapper<Year> {
        return client.put("$baseUrl/years/$id") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun yearsDestroy(id: String) {
        client.delete("$baseUrl/years/$id")
    }

    suspend fun yearRestore(id: String): DataWrapper<Year> {
        return client.post("$baseUrl/years/$id/restore").body()
    }

    suspend fun yearSetCurrent(requestBody: SetCurrentYearRequest? = null): DataWrapper<Year> {
        return client.post("$baseUrl/years/current") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }
}