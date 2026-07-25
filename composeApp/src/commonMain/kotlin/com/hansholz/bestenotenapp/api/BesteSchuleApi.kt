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
import com.hansholz.bestenotenapp.api.models.Attachment
import com.hansholz.bestenotenapp.api.models.BatchChecklistStudentRequest
import com.hansholz.bestenotenapp.api.models.BatchFinalCertificateRequest
import com.hansholz.bestenotenapp.api.models.BatchFinalgradeRequest
import com.hansholz.bestenotenapp.api.models.BatchGroupRequest
import com.hansholz.bestenotenapp.api.models.BatchGuardianRequest
import com.hansholz.bestenotenapp.api.models.BatchIntervalStudentRequest
import com.hansholz.bestenotenapp.api.models.BatchJournalLessonStudentRequest
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
import com.hansholz.bestenotenapp.api.models.ChecklistStudent
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
import com.hansholz.bestenotenapp.api.models.IntervalStudent
import com.hansholz.bestenotenapp.api.models.JournalDay
import com.hansholz.bestenotenapp.api.models.JournalDayStudent
import com.hansholz.bestenotenapp.api.models.JournalDayStudentCount
import com.hansholz.bestenotenapp.api.models.JournalLessonStudent
import com.hansholz.bestenotenapp.api.models.JournalLessonStudentCount
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
import com.hansholz.bestenotenapp.api.models.StoreAnnouncementResponseRequest
import com.hansholz.bestenotenapp.api.models.StoreAnnouncementTypeRequest
import com.hansholz.bestenotenapp.api.models.StoreAttachmentRequest
import com.hansholz.bestenotenapp.api.models.StoreCertificateGradeRequest
import com.hansholz.bestenotenapp.api.models.StoreChecklistRequest
import com.hansholz.bestenotenapp.api.models.StoreChecklistStudentRequest
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
import com.hansholz.bestenotenapp.api.models.StoreNewsletterRequest
import com.hansholz.bestenotenapp.api.models.StoreNoteRequest
import com.hansholz.bestenotenapp.api.models.StoreNoteTypeRequest
import com.hansholz.bestenotenapp.api.models.StoreOrUpdateJournalDayRequest
import com.hansholz.bestenotenapp.api.models.StoreOrUpdateSubstitutionPlanDayRequest
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
import com.hansholz.bestenotenapp.api.models.UpdateChecklistStudentRequest
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

class BesteSchuleApi(
    httpClient: HttpClient,
    authToken: MutableState<String?>,
    studentId: MutableState<String?> = mutableStateOf(null),
) {
    private val baseUrl = "https://beste.schule/api"

    private val client =
        httpClient.config {
            install(studentFilterPlugin(studentId))
            defaultRequest {
                authToken.value?.let { bearerAuth(it) }
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }

    /** Access: Guardian+ Required */
    suspend fun absencesStore(requestBody: Absence): DataWrapper<Absence> =
        client
            .post("$baseUrl/absences") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun absencesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Absence> =
        client
            .get("$baseUrl/absences") {
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

    /** Access: Guardian+ Required */
    suspend fun absencesUpdate(
        absence: Int,
        requestBody: Absence,
    ): DataWrapper<Absence> =
        client
            .put("$baseUrl/absences/$absence") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Guardian+ Required */
    suspend fun absencesDestroy(absence: Int) {
        client.delete("$baseUrl/absences/$absence")
    }

    /** Access: Any role */
    suspend fun absencesShow(absence: Int): DataWrapper<Absence> = client.get("$baseUrl/absences/$absence").body()

    /** Access: Teacher+ Required */
    suspend fun absenceBatchesStore(requestBody: StoreAbsenceBatchRequest): DataWrapper<AbsenceBatch> =
        client
            .post("$baseUrl/absence-batches") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun absenceBatchesIndex(): ListDataWrapper<AbsenceBatch> = client.get("$baseUrl/absence-batches").body()

    /** Access: Teacher+ Required */
    suspend fun absenceBatchesUpdate(
        absenceBatch: Int,
        requestBody: UpdateAbsenceBatchRequest,
    ): DataWrapper<AbsenceBatch> =
        client
            .put("$baseUrl/absence-batches/$absenceBatch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun absenceBatchesDestroy(absenceBatch: Int) {
        client.delete("$baseUrl/absence-batches/$absenceBatch")
    }

    /** Access: Any role */
    suspend fun absenceBatchesShow(absenceBatch: Int): DataWrapper<AbsenceBatch> = client.get("$baseUrl/absence-batches/$absenceBatch").body()

    /** Access: Mod+ Required */
    suspend fun absenceTypeStore(requestBody: StoreAbsenceTypeRequest): DataWrapper<AbsenceType> =
        client
            .post("$baseUrl/types/absence") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Guardian+ Required */
    suspend fun absenceTypeIndex(): ListDataWrapper<AbsenceType> = client.get("$baseUrl/types/absence").body()

    /** Access: Mod+ Required */
    suspend fun absenceTypeShow(id: String): DataWrapper<AbsenceType> = client.get("$baseUrl/types/absence/$id").body()

    /** Access: Mod+ Required */
    suspend fun absenceTypeUpdate(
        id: String,
        requestBody: UpdateAbsenceTypeRequest,
    ): DataWrapper<AbsenceType> =
        client
            .put("$baseUrl/types/absence/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun absenceTypeDestroy(id: String) {
        client.delete("$baseUrl/types/absence/$id")
    }

    /** Access: Teacher+ Required */
    suspend fun absencesVerificationsStore(
        absence: Int,
        requestBody: StoreAbsenceVerificationRequest? = null,
    ): DataWrapper<AbsenceVerification> =
        client
            .post("$baseUrl/absences/$absence/verifications") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun verificationsShow(id: String): DataWrapper<AbsenceVerification> = client.get("$baseUrl/absences/verifications/$id").body()

    /** Access: Teacher+ Required */
    suspend fun verificationsUpdate(
        verification: String,
        requestBody: UpdateAbsenceVerificationRequest? = null,
    ): DataWrapper<AbsenceVerification> =
        client
            .put("$baseUrl/absences/verifications/$verification") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun verificationsDestroy(verification: String) {
        client.delete("$baseUrl/absences/verifications/$verification")
    }

    /** Access: Teacher+ Required */
    suspend fun announcementsStore(requestBody: StoreAnnouncementRequest): DataWrapper<Announcement> =
        client
            .post("$baseUrl/announcements") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun announcementsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Announcement> =
        client
            .get("$baseUrl/announcements") {
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

    /** Access: Teacher+ Required */
    suspend fun announcementsUpdate(
        announcement: Int,
        requestBody: UpdateAnnouncementRequest,
    ): DataWrapper<Announcement> =
        client
            .put("$baseUrl/announcements/$announcement") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun announcementsDestroy(announcement: Int) {
        client.delete("$baseUrl/announcements/$announcement")
    }

    /** Access: Not documented in current OpenAPI specification */
    suspend fun announcementMarkRead(
        announcement: Int,
        requestBody: MarkReadAnnouncementRequest? = null,
    ): DataWrapper<Announcement> =
        client
            .post("$baseUrl/announcements/$announcement/read") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun announcementsShow(id: String): DataWrapper<Announcement> = client.get("$baseUrl/announcements/$id").body()

    /** Access: Mod+ Required */
    suspend fun announcementTypeStore(requestBody: StoreAnnouncementTypeRequest): DataWrapper<AnnouncementType> =
        client
            .post("$baseUrl/types/announcement") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun announcementTypeIndex(): ListDataWrapper<AnnouncementType> = client.get("$baseUrl/types/announcement").body()

    /** Access: Mod+ Required */
    suspend fun announcementTypeShow(id: String): DataWrapper<AnnouncementType> = client.get("$baseUrl/types/announcement/$id").body()

    /** Access: Mod+ Required */
    suspend fun announcementTypeUpdate(
        id: String,
        requestBody: UpdateAnnouncementTypeRequest,
    ): DataWrapper<AnnouncementType> =
        client
            .put("$baseUrl/types/announcement/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun announcementTypeDestroy(id: String) {
        client.delete("$baseUrl/types/announcement/$id")
    }

    /** Access: Teacher+ Required */
    suspend fun certificateGradeRestore(certificateGradeId: String): DataWrapper<CertificateGrade> = client.post("$baseUrl/certificate-grades/$certificateGradeId/restore").body()

    /** Access: Teacher+ Required */
    suspend fun certificateGradesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<CertificateGrade> =
        client
            .get("$baseUrl/certificate-grades") {
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

    /** Access: Teacher+ Required */
    suspend fun certificateGradesStore(requestBody: StoreCertificateGradeRequest): DataWrapper<CertificateGrade> =
        client
            .post("$baseUrl/certificate-grades") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun certificateGradesShow(id: String): DataWrapper<CertificateGrade> = client.get("$baseUrl/certificate-grades/$id").body()

    /** Access: Teacher+ Required */
    suspend fun certificateGradesUpdate(
        certificateGrade: Int,
        requestBody: UpdateCertificateGradeRequest? = null,
    ): DataWrapper<CertificateGrade> =
        client
            .put("$baseUrl/certificate-grades/$certificateGrade") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun certificateGradesDestroy(certificateGrade: Int) {
        client.delete("$baseUrl/certificate-grades/$certificateGrade")
    }

    /** Access: Teacher+ Required */
    suspend fun checklistsStore(requestBody: StoreChecklistRequest): DataWrapper<Checklist> =
        client
            .post("$baseUrl/checklists") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun checklistsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Checklist> =
        client
            .get("$baseUrl/checklists") {
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

    /** Access: Teacher+ Required */
    suspend fun checklistsUpdate(
        checklist: Int,
        requestBody: UpdateChecklistRequest,
    ): DataWrapper<Checklist> =
        client
            .put("$baseUrl/checklists/$checklist") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun checklistsDestroy(checklist: Int) {
        client.delete("$baseUrl/checklists/$checklist")
    }

    /** Access: Any role */
    suspend fun checklistsShow(id: String): DataWrapper<Checklist> = client.get("$baseUrl/checklists/$id").body()

    /** Access: Mod+ Required */
    suspend fun checklistTypeStore(requestBody: StoreChecklistTypeRequest): DataWrapper<ChecklistType> =
        client
            .post("$baseUrl/types/checklist") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun checklistTypeIndex(): ListDataWrapper<ChecklistType> = client.get("$baseUrl/types/checklist").body()

    /** Access: Mod+ Required */
    suspend fun checklistTypeShow(id: String): DataWrapper<ChecklistType> = client.get("$baseUrl/types/checklist/$id").body()

    /** Access: Mod+ Required */
    suspend fun checklistTypeUpdate(
        id: String,
        requestBody: UpdateChecklistTypeRequest,
    ): DataWrapper<ChecklistType> =
        client
            .put("$baseUrl/types/checklist/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun checklistTypeDestroy(id: String) {
        client.delete("$baseUrl/types/checklist/$id")
    }

    /** Access: Teacher+ Required */
    suspend fun collectionRestore(id: String): DataWrapper<GradeCollection> = client.post("$baseUrl/collections/$id/restore").body()

    /** Access: Teacher+ Required */
    suspend fun collectionsStore(requestBody: StoreCollectionRequest): DataWrapper<GradeCollection> =
        client
            .post("$baseUrl/collections") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
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
        page: Int? = null,
    ): ListDataWrapper<GradeCollection> =
        client
            .get("$baseUrl/collections") {
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

    /** Access: Teacher+ Required */
    suspend fun collectionsUpdate(
        id: String,
        requestBody: UpdateCollectionRequest,
    ): DataWrapper<GradeCollection> =
        client
            .put("$baseUrl/collections/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun collectionsDestroy(id: String) {
        client.delete("$baseUrl/collections/$id")
    }

    /** Access: Any role */
    suspend fun collectionsShow(id: String): DataWrapper<GradeCollection> = client.get("$baseUrl/collections/$id").body()

    /** Access: Mod+ Required */
    suspend fun computeIntervalTypes(): ArrayCollection = client.get("$baseUrl/types/interval").body()

    /** Access: Mod+ Required */
    suspend fun computeTimeTypes(): ArrayCollection = client.get("$baseUrl/types/time").body()

    /** Access: Teacher+ Required */
    suspend fun computeReportTypes(): ArrayCollection = client.get("$baseUrl/types/report").body()

    /** Access: Teacher+ Required */
    suspend fun computeTimeNames(): ArrayCollection = client.get("$baseUrl/types/time-names").body()

    /** Access: Teacher+ Required */
    suspend fun favoritesIndex(): ListDataWrapper<Favorite> = client.get("$baseUrl/favorites").body()

    /** Access: Teacher+ Required */
    suspend fun favoritesStore(requestBody: StoreFavoriteRequest? = null): DataWrapper<Favorite> =
        client
            .post("$baseUrl/favorites") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun favoritesShow(favorite: Int): DataWrapper<Favorite> = client.get("$baseUrl/favorites/$favorite").body()

    /** Access: Teacher+ Required */
    suspend fun favoritesUpdate(
        favorite: Int,
        requestBody: UpdateFavoriteRequest? = null,
    ): DataWrapper<Favorite> =
        client
            .put("$baseUrl/favorites/$favorite") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun favoritesDestroy(favorite: Int) {
        client.delete("$baseUrl/favorites/$favorite")
    }

    /** Access: Teacher+ Required */
    suspend fun finalCertificateRestore(certificateGradeId: String): DataWrapper<FinalCertificate> = client.post("$baseUrl/final-certificates/$certificateGradeId/restore").body()

    /** Access: Teacher+ Required */
    suspend fun finalCertificateBatch(requestBody: List<BatchFinalCertificateRequest>? = null): ListDataWrapper<FinalCertificate> =
        client
            .post("$baseUrl/final-certificates/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun finalCertificatesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<FinalCertificate> =
        client
            .get("$baseUrl/final-certificates") {
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

    /** Access: Teacher+ Required */
    suspend fun finalCertificatesStore(requestBody: StoreFinalCertificateRequest): DataWrapper<FinalCertificate> =
        client
            .post("$baseUrl/final-certificates") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun finalCertificatesShow(id: String): DataWrapper<FinalCertificate> = client.get("$baseUrl/final-certificates/$id").body()

    /** Access: Teacher+ Required */
    suspend fun finalCertificatesUpdate(
        finalCertificate: Int,
        requestBody: UpdateFinalCertificateRequest? = null,
    ): DataWrapper<FinalCertificate> =
        client
            .put("$baseUrl/final-certificates/$finalCertificate") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun finalCertificatesDestroy(finalCertificate: Int) {
        client.delete("$baseUrl/final-certificates/$finalCertificate")
    }

    /** Access: Teacher+ Required */
    suspend fun finalgradesStore(requestBody: StoreFinalgradeRequest): DataWrapper<Finalgrade> =
        client
            .post("$baseUrl/finalgrades") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun finalgradesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Finalgrade> =
        client
            .get("$baseUrl/finalgrades") {
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

    /** Access: Teacher+ Required */
    suspend fun finalgradesUpdate(
        id: String,
        requestBody: UpdateFinalgradeRequest? = null,
    ): DataWrapper<Finalgrade> =
        client
            .put("$baseUrl/finalgrades/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun finalgradesDestroy(id: String) {
        client.delete("$baseUrl/finalgrades/$id")
    }

    /** Access: Any role */
    suspend fun finalgradesShow(id: String): DataWrapper<Finalgrade> = client.get("$baseUrl/finalgrades/$id").body()

    /** Access: Teacher+ Required */
    suspend fun gradeRestore(id: String): DataWrapper<Grade> = client.post("$baseUrl/grades/$id/restore").body()

    /** Access: Teacher+ Required */
    suspend fun gradesStore(requestBody: StoreGradeRequest): DataWrapper<Grade> =
        client
            .post("$baseUrl/grades") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun gradesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Grade> =
        client
            .get("$baseUrl/grades") {
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

    /** Access: Teacher+ Required */
    suspend fun gradesUpdate(
        id: String,
        requestBody: UpdateGradeRequest,
    ): DataWrapper<Grade> =
        client
            .put("$baseUrl/grades/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun gradesDestroy(id: String) {
        client.delete("$baseUrl/grades/$id")
    }

    /** Access: Any role */
    suspend fun gradesShow(id: String): DataWrapper<Grade> = client.get("$baseUrl/grades/$id").body()

    /** Access: Mod+ Required */
    suspend fun groupBatch(requestBody: List<BatchGroupRequest>? = null): ListDataWrapper<Group> =
        client
            .post("$baseUrl/groups/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun groupSync() {
        client.post("$baseUrl/groups/sync")
    }

    /** Access: Mod+ Required */
    suspend fun groupAddStudent(id: String): DataWrapper<Student> = client.post("$baseUrl/groups/$id/students").body()

    /** Access: Mod+ Required */
    suspend fun groupRemoveStudent(id: String) {
        client.delete("$baseUrl/groups/$id/students")
    }

    /** Access: Mod+ Required */
    suspend fun groupAddSubject(id: String): DataWrapper<Subject> = client.post("$baseUrl/groups/$id/subjects").body()

    /** Access: Mod+ Required */
    suspend fun groupRemoveSubject(id: String) {
        client.delete("$baseUrl/groups/$id/subjects")
    }

    /** Access: Teacher+ Required */
    suspend fun groupRestore(id: String): DataWrapper<Group> = client.post("$baseUrl/groups/$id/restore").body()

    /** Access: Mod+ Required */
    suspend fun groupsStore(requestBody: StoreGroupRequest): DataWrapper<Group> =
        client
            .post("$baseUrl/groups") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun groupsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Group> =
        client
            .get("$baseUrl/groups") {
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

    /** Access: Mod+ Required */
    suspend fun groupsDestroy(id: String) {
        client.delete("$baseUrl/groups/$id")
    }

    /** Access: Not documented in current OpenAPI specification */
    fun groupsUpdate(id: String): DataWrapper<Group> = throw NotImplementedError("Request body schema for groupsUpdate is missing in the OpenAPI spec.")

    /** Access: Teacher+ Required */
    suspend fun groupSetOrderCollectionType(
        id: String,
        subjectId: String,
    ): DataWrapper<GroupSubjectOrderResponse> = client.post("$baseUrl/groups/$id/subjects/$subjectId/order").body()

    /** Access: Any role */
    suspend fun groupsShow(
        group: Int,
        include: List<String>? = null,
    ): DataWrapper<Group> =
        client
            .get("$baseUrl/groups/$group") {
                parameter("include", include?.joinToString(","))
            }.body()

    /** Access: Any role */
    suspend fun guardiansIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Guardian> =
        client
            .get("$baseUrl/guardians") {
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

    /** Access: Mod+ Required */
    suspend fun guardiansStore(requestBody: StoreGuardianRequest): DataWrapper<Guardian> =
        client
            .post("$baseUrl/guardians") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun guardiansShow(id: String): DataWrapper<Guardian> = client.get("$baseUrl/guardians/$id").body()

    /** Access: Mod+ Required */
    suspend fun guardiansUpdate(
        id: String,
        requestBody: UpdateGuardianRequest? = null,
    ): DataWrapper<Guardian> =
        client
            .put("$baseUrl/guardians/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun guardiansDestroy(id: Int) {
        client.delete("$baseUrl/guardians/$id")
    }

    /** Access: Mod+ Required */
    suspend fun guardianBatchToken(requestBody: BatchTokenGuardianRequest): ListDataWrapper<Guardian> =
        client
            .post("$baseUrl/guardians/token") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun guardianBatch(requestBody: List<BatchGuardianRequest>? = null): ListDataWrapper<Guardian> =
        client
            .post("$baseUrl/guardians/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun guardianRefreshToken(id: String): DataWrapper<Guardian> = client.get("$baseUrl/guardians/$id/token").body()

    /** Access: Mod+ Required */
    suspend fun guardianRemoveToken(id: String): DataWrapper<Guardian> = client.delete("$baseUrl/guardians/$id/token").body()

    /** Access: Mod+ Required */
    suspend fun guardianRestore(id: String): DataWrapper<Guardian> = client.post("$baseUrl/guardians/$id/restore").body()

    /** Access: Guardian+ Required */
    suspend fun guardianRemoveUser(guardian: Int): DataWrapper<Guardian> = client.delete("$baseUrl/guardians/$guardian/user").body()

    /** Access: Mod+ Required */
    suspend fun historyShowSchool(): PaginatedDataWrapper<History> = client.get("$baseUrl/histories").body()

    /** Access: Teacher+ Required */
    suspend fun historyIndexGroupSubject(): Any = client.get("$baseUrl/histories/table/group-subject").body()

    /** Access: Teacher+ Required */
    suspend fun historyShowTable(table: String): PaginatedDataWrapper<History> = client.get("$baseUrl/histories/table/$table").body()

    /** Access: Teacher+ Required */
    suspend fun historyShowId(
        table: String,
        id: String,
    ): PaginatedDataWrapper<History> = client.get("$baseUrl/histories/table/$table/$id").body()

    /** Access: Any role */
    suspend fun homeNothing() {
        client.get("$baseUrl/user/extend-session")
    }

    /** Access: Mod+ Required */
    suspend fun importerLoad(importer: Int): DataWrapper<Importer> = client.post("$baseUrl/importers/$importer/load").body()

    /** Access: Mod+ Required */
    suspend fun importerLoadTimeTables(importer: Int): DataWrapper<Importer> = client.post("$baseUrl/importers/$importer/load/time-tables").body()

    /** Access: Mod+ Required */
    suspend fun importerLoadSubstitutionPlans(importer: Int): DataWrapper<Importer> = client.post("$baseUrl/importers/$importer/load/substitutionplans").body()

    /** Access: Mod+ Required */
    suspend fun importerIndexLogs(importer: Int): PaginatedDataWrapper<ImporterLog> = client.get("$baseUrl/importers/$importer/logs").body()

    /** Access: Mod+ Required */
    suspend fun importersIndex(): ListDataWrapper<Importer> = client.get("$baseUrl/importers").body()

    /** Access: Mod+ Required */
    suspend fun importersStore(requestBody: StoreImporterRequest): DataWrapper<Importer> =
        client
            .post("$baseUrl/importers") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun importersShow(importer: Int): DataWrapper<Importer> = client.get("$baseUrl/importers/$importer").body()

    /** Access: Mod+ Required */
    suspend fun importersUpdate(
        importer: Int,
        requestBody: UpdateImporterRequest,
    ): DataWrapper<Importer> =
        client
            .put("$baseUrl/importers/$importer") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun importersDestroy(importer: Int) {
        client.delete("$baseUrl/importers/$importer")
    }

    /** Access: Authentication requirement not documented */
    suspend fun importerPush(
        secret: String,
        requestBody: PushImporterRequest,
    ) {
        client.post("$baseUrl/importers/webhooks/$secret") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }
    }

    /** Access: Mod+ Required */
    suspend fun importerStundenplan24Load(id: String): DataWrapper<ImporterStundenplan24> = client.post("$baseUrl/importers/stundenplan24/$id/load").body()

    /** Access: Mod+ Required */
    suspend fun importerStundenplan24LoadTimeTable(id: String): DataWrapper<ImporterStundenplan24> = client.post("$baseUrl/importers/stundenplan24/$id/load/time-table").body()

    /** Access: Mod+ Required */
    suspend fun importerStundenplan24LoadSubstitutionPlan(id: String): DataWrapper<ImporterStundenplan24> =
        client.post("$baseUrl/importers/stundenplan24/$id/load/substitutionplan").body()

    /** Access: Mod+ Required */
    suspend fun importerStundenplan24IndexLogs(id: String): PaginatedDataWrapper<ImporterLog> = client.get("$baseUrl/importers/stundenplan24/$id/logs").body()

    /** Access: Mod+ Required */
    suspend fun stundenplan24Index(): ListDataWrapper<ImporterStundenplan24> = client.get("$baseUrl/importers/stundenplan24").body()

    /** Access: Mod+ Required */
    suspend fun stundenplan24Store(requestBody: StoreImporterStundenplan24Request): DataWrapper<ImporterStundenplan24> =
        client
            .post("$baseUrl/importers/stundenplan24") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun stundenplan24Show(id: String): DataWrapper<ImporterStundenplan24> = client.get("$baseUrl/importers/stundenplan24/$id").body()

    /** Access: Mod+ Required */
    suspend fun stundenplan24Update(
        id: String,
        requestBody: UpdateImporterStundenplan24Request,
    ): DataWrapper<ImporterStundenplan24> =
        client
            .put("$baseUrl/importers/stundenplan24/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun stundenplan24Destroy(id: String) {
        client.delete("$baseUrl/importers/stundenplan24/$id")
    }

    /** Access: Mod+ Required */
    suspend fun intervalRestore(id: String): DataWrapper<Interval> = client.post("$baseUrl/intervals/$id/restore").body()

    /** Access: Mod+ Required */
    suspend fun intervalsStore(requestBody: StoreIntervalRequest): DataWrapper<Interval> =
        client
            .post("$baseUrl/intervals") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun intervalIndex(): ListDataWrapper<Interval> = client.get("$baseUrl/intervals").body()

    /** Access: Mod+ Required */
    suspend fun intervalsShow(id: String): DataWrapper<Interval> = client.get("$baseUrl/intervals/$id").body()

    /** Access: Mod+ Required */
    suspend fun intervalsUpdate(
        id: String,
        requestBody: UpdateIntervalRequest,
    ): DataWrapper<Interval> =
        client
            .put("$baseUrl/intervals/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun intervalsDestroy(id: String) {
        client.delete("$baseUrl/intervals/$id")
    }

    /** Access: Management+ Required */
    suspend fun journalDayStoreOrUpdatePost(
        date: String,
        requestBody: StoreOrUpdateJournalDayRequest? = null,
    ): DataWrapper<JournalDay> =
        client
            .post("$baseUrl/journal/days/$date") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Management+ Required */
    suspend fun journalDayStoreOrUpdatePut(
        date: String,
        requestBody: StoreOrUpdateJournalDayRequest? = null,
    ): DataWrapper<JournalDay> =
        client
            .put("$baseUrl/journal/days/$date") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Management+ Required */
    suspend fun journalDayDestroy(date: String) {
        client.delete("$baseUrl/journal/days/$date")
    }

    /** Access: Any role */
    suspend fun journalDayShow(
        date: String,
        include: String? = null,
    ): DataWrapper<JournalDay> =
        client
            .get("$baseUrl/journal/days/$date") {
                parameter("include", include)
            }.body()

    /** Access: Any role */
    suspend fun journalDayIndex(include: String? = null): ListDataWrapper<JournalDay> =
        client
            .get("$baseUrl/journal/days") {
                parameter("include", include)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun dayStudentStore(requestBody: StoreJournalDayStudentRequest): DataWrapper<JournalDayStudent> =
        client
            .post("$baseUrl/journal/day-student") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun dayStudentIndex(): Any = client.get("$baseUrl/journal/day-student").body()

    /** Access: Teacher+ Required */
    suspend fun dayStudentUpdate(
        id: String,
        requestBody: UpdateJournalLessonStudentRequest? = null,
    ): DataWrapper<JournalDayStudent> =
        client
            .put("$baseUrl/journal/day-student/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun dayStudentDestroy(id: String) {
        client.delete("$baseUrl/journal/day-student/$id")
    }

    /** Access: Any role */
    suspend fun dayStudentShow(id: String): DataWrapper<JournalDayStudent> = client.get("$baseUrl/journal/day-student/$id").body()

    /** Access: Teacher+ Required */
    suspend fun journalNoteStoreForWeek(
        nr: String,
        requestBody: StoreForWeekJournalNoteRequest,
    ): DataWrapper<JournalNote> =
        client
            .post("$baseUrl/journal/weeks/$nr/notes") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun journalNoteStoreForDay(
        date: String,
        requestBody: StoreForDayJournalNoteRequest,
    ): DataWrapper<JournalNote> =
        client
            .post("$baseUrl/journal/days/$date/notes") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun journalNoteStoreForLesson(
        id: String,
        requestBody: StoreForLessonJournalNoteRequest,
    ): DataWrapper<JournalNote> =
        client
            .post("$baseUrl/journal/lessons/$id/notes") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun journalNoteStoreForLessonStudent(
        lesson: String,
        student: String,
        requestBody: StoreForLessonStudentJournalNoteRequest,
    ): DataWrapper<JournalNote> =
        client
            .post("$baseUrl/journal/lessons/$lesson/students/$student/notes") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun journalNoteStore(requestBody: StoreJournalNoteRequest): DataWrapper<JournalNote> =
        client
            .post("$baseUrl/journal/notes") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun notesShow(id: String): DataWrapper<JournalNote> = client.get("$baseUrl/journal/notes/$id").body()

    /** Access: Teacher+ Required */
    suspend fun journalNoteUpdate(
        id: String,
        requestBody: UpdateJournalNoteRequest,
    ): DataWrapper<JournalNote> =
        client
            .put("$baseUrl/journal/notes/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun journalNoteDestroy(id: String) {
        client.delete("$baseUrl/journal/notes/$id")
    }

    /** Access: Mod+ Required */
    suspend fun journalNotesStore(requestBody: StoreJournalNoteTypeRequest): DataWrapper<JournalNoteType> =
        client
            .post("$baseUrl/types/journal-notes") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun journalNoteTypeIndex(filter: String? = null): ListDataWrapper<JournalNoteType> =
        client
            .get("$baseUrl/types/journal-notes") {
                parameter("filter", filter)
            }.body()

    /** Access: Mod+ Required */
    suspend fun journalNotesShow(id: String): DataWrapper<JournalNoteType> = client.get("$baseUrl/types/journal-notes/$id").body()

    /** Access: Mod+ Required */
    suspend fun journalNotesUpdate(
        id: String,
        requestBody: UpdateJournalNoteTypeRequest,
    ): DataWrapper<JournalNoteType> =
        client
            .put("$baseUrl/types/journal-notes/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun journalNotesDestroy(id: String) {
        client.delete("$baseUrl/types/journal-notes/$id")
    }

    /** Access: Management+ Required */
    suspend fun journalWeekStoreOrUpdatePost(nr: String): DataWrapper<JournalWeek> = client.post("$baseUrl/journal/weeks/$nr").body()

    /** Access: Management+ Required */
    suspend fun journalWeekStoreOrUpdatePut(nr: String): DataWrapper<JournalWeek> = client.put("$baseUrl/journal/weeks/$nr").body()

    /** Access: Management+ Required */
    suspend fun journalWeekDestroy(nr: String) {
        client.delete("$baseUrl/journal/weeks/$nr")
    }

    /** Access: Any role */
    suspend fun journalWeekShow(
        nr: String,
        filterYear: String? = null,
        interpolate: Boolean? = null,
        include: String? = null,
    ): DataWrapper<JournalWeek> =
        client
            .get("$baseUrl/journal/weeks/$nr") {
                filterYear?.let { parameter("filter[year]", filterYear) }
                interpolate?.let { parameter("interpolate", interpolate) }
                parameter("include", include)
            }.body()

    /** Access: Any role */
    suspend fun journalWeekIndex(): ListDataWrapper<JournalWeek> = client.get("$baseUrl/journal/weeks").body()

    /** Access: Mod+ Required */
    suspend fun levelsStore(requestBody: StoreLevelRequest): DataWrapper<Level> =
        client
            .post("$baseUrl/levels") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun levelsIndex(): ListDataWrapper<Level> = client.get("$baseUrl/levels").body()

    /** Access: Mod+ Required */
    suspend fun levelsShow(id: String): DataWrapper<Level> = client.get("$baseUrl/levels/$id").body()

    /** Access: Mod+ Required */
    suspend fun levelsUpdate(
        id: String,
        requestBody: UpdateLevelRequest,
    ): DataWrapper<Level> =
        client
            .put("$baseUrl/levels/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun levelsDestroy(id: String) {
        client.delete("$baseUrl/levels/$id")
    }

    /** Access: Teacher+ Required */
    suspend fun noteStore(requestBody: StoreNoteRequest): DataWrapper<Note> =
        client
            .post("$baseUrl/notes") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun notesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Note> =
        client
            .get("$baseUrl/notes") {
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

    /** Access: Teacher+ Required */
    suspend fun noteUpdate(
        note: Int,
        requestBody: UpdateNoteRequest? = null,
    ): DataWrapper<Note> =
        client
            .put("$baseUrl/notes/$note") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun noteDestroy(note: Int) {
        client.delete("$baseUrl/notes/$note")
    }

    /** Access: Mod+ Required */
    suspend fun noteTypeStore(requestBody: StoreNoteTypeRequest): DataWrapper<NoteType> =
        client
            .post("$baseUrl/types/notes") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun noteTypeIndex(): ListDataWrapper<NoteType> = client.get("$baseUrl/types/notes").body()

    /** Access: Mod+ Required */
    suspend fun noteTypeUpdate(
        id: Int,
        requestBody: UpdateNoteTypeRequest? = null,
    ): DataWrapper<NoteType> =
        client
            .put("$baseUrl/types/notes/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun noteTypeDestroy(id: String) {
        client.delete("$baseUrl/types/notes/$id")
    }

    /** Access: Any role */
    suspend fun notificationsIndex(): ListDataWrapper<Notification> = client.get("$baseUrl/notifications").body()

    /** Access: Any role */
    suspend fun notificationMarkRead(notification: String): Notification = client.post("$baseUrl/notifications/$notification/read").body()

    /** Access: Any role */
    suspend fun notificationExecuteAction(
        notification: String,
        requestBody: ExecuteNotificationActionRequest,
    ): Notification =
        client
            .post("$baseUrl/notifications/$notification/action") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Not documented in current OpenAPI specification */
    fun reportPreviewReport(): ReportResult = throw NotImplementedError("Request body schema for reportPreviewReport is missing in the OpenAPI spec.")

    /** Access: Teacher+ Required */
    suspend fun reportReport(
        reportID: String,
        rangeFrom: String? = null,
        rangeTo: String? = null,
        clearCache: Boolean? = null,
        sleep: Int? = null,
        filterResult: ReportFilterResult? = null,
    ): ReportResult =
        client
            .get("$baseUrl/reports/$reportID/result") {
                parameter("range_from", rangeFrom)
                parameter("range_to", rangeTo)
                parameter("clear_cache", clearCache)
                parameter("sleep", sleep)
                parameter("filter_result", filterResult)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun reportsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Report> =
        client
            .get("$baseUrl/reports") {
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

    /** Access: Not documented in current OpenAPI specification */
    fun reportsStore(): DataWrapper<Report> = throw NotImplementedError("Request body schema for reportsStore is missing in the OpenAPI spec.")

    /** Access: Teacher+ Required */
    suspend fun reportsShow(id: String): DataWrapper<Report> = client.get("$baseUrl/reports/$id").body()

    /** Access: Not documented in current OpenAPI specification */
    fun reportsUpdate(id: String): DataWrapper<Report> = throw NotImplementedError("Request body schema for reportsUpdate is missing in the OpenAPI spec.")

    /** Access: Teacher+ Required */
    suspend fun reportsDestroy(id: String) {
        client.delete("$baseUrl/reports/$id")
    }

    /** Access: Teacher+ Required */
    suspend fun roomsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Room> =
        client
            .get("$baseUrl/rooms") {
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

    /** Access: Teacher+ Required */
    suspend fun roomsShow(id: String): DataWrapper<Room> = client.get("$baseUrl/rooms/$id").body()

    /** Access: Mod+ Required */
    suspend fun schoolUpdate(requestBody: UpdateSchoolRequest): DataWrapper<School> =
        client
            .put("$baseUrl/school") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
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
                                    Headers.build { append(HttpHeaders.ContentType, ContentType.Application.OctetStream) },
                                )
                            }
                            requestBody.logoUrl?.let { append("logo_url", it) }
                            append("admin_name", requestBody.adminName)
                            append("admin_email", requestBody.adminEmail)
                            append("headteacher_name", requestBody.headteacherName)
                        },
                    ),
                )
            }.body()

    /** Access: Any role */
    suspend fun schoolShow(): DataWrapper<School> = client.get("$baseUrl/school").body()

    /** Access: Not documented in current OpenAPI specification */
    fun schoolAddUser(): DataWrapper<User> = throw NotImplementedError("Request body schema for schoolAddUser is missing in the OpenAPI spec.")

    /** Access: Mod+ Required */
    suspend fun schoolRemoveUser(user: Int) {
        client.delete("$baseUrl/school/users/$user")
    }

    /** Access: Mod+ Required */
    suspend fun schoolsIndex(): ListDataWrapper<School> = client.get("$baseUrl/schools").body()

    /** Access: Mod+ Required */
    suspend fun schoolsStore(requestBody: StoreSchoolRequest): DataWrapper<School> =
        client
            .post("$baseUrl/schools") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
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
                        },
                    ),
                )
            }.body()

    /** Access: Not documented in current OpenAPI specification */
    suspend fun schoolGetLogo(extension: String? = null): String = client.get("$baseUrl/school/logo${extension?.let { ".$it" } ?: ""}").body()

    /** Access: Not documented in current OpenAPI specification */
    fun seatingPlansStore(): DataWrapper<SeatingPlan> = throw NotImplementedError("Request body schema for seatingPlansStore is missing in the OpenAPI spec.")

    /** Access: Any role */
    suspend fun seatingPlansIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<SeatingPlan> =
        client
            .get("$baseUrl/seating-plans") {
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

    /** Access: Not documented in current OpenAPI specification */
    fun seatingPlansUpdate(seatingPlan: Int): DataWrapper<SeatingPlan> = throw NotImplementedError("Request body schema for seatingPlansUpdate is missing in the OpenAPI spec.")

    /** Access: Teacher+ Required */
    suspend fun seatingPlansDestroy(seatingPlan: Int) {
        client.delete("$baseUrl/seating-plans/$seatingPlan")
    }

    /** Access: Any role */
    suspend fun seatingPlansShow(seatingPlan: Int): DataWrapper<SeatingPlan> = client.get("$baseUrl/seating-plans/$seatingPlan").body()

    /** Access: Any role */
    suspend fun siteStatusIndex(): DataWrapper<SiteStatusResponse> = client.get("$baseUrl/status").body()

    /** Access: Mod+ Required */
    suspend fun studentRefreshToken(id: String): DataWrapper<Student> = client.get("$baseUrl/students/$id/token").body()

    /** Access: Mod+ Required */
    suspend fun studentRemoveToken(id: String): DataWrapper<Student> = client.delete("$baseUrl/students/$id/token").body()

    /** Access: Mod+ Required */
    suspend fun studentBatchToken(requestBody: BatchTokenStudentRequest): ListDataWrapper<Student> =
        client
            .post("$baseUrl/students/token") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun studentRefreshTokenGuardian(id: String): DataWrapper<Student> = client.get("$baseUrl/students/$id/token-guardian").body()

    /** Access: Mod+ Required */
    suspend fun studentRemoveTokenGuardian(id: String): DataWrapper<Student> = client.delete("$baseUrl/students/$id/token-guardian").body()

    /** Access: Mod+ Required */
    suspend fun studentBatchTokenGuardian(requestBody: BatchTokenGuardianStudentRequest): ListDataWrapper<Student> =
        client
            .post("$baseUrl/students/token-guardian") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun studentDestroyInterval(
        id: String,
        interval: String,
    ): DataWrapper<Student> = client.delete("$baseUrl/students/$id/intervals/$interval").body()

    /** Access: Teacher+ Required */
    suspend fun studentStoreUpdateIntervalPost(
        id: String,
        interval: String,
        requestBody: StoreUpdateIntervalStudentRequest? = null,
    ): DataWrapper<Student> =
        client
            .post("$baseUrl/students/$id/intervals/$interval") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun studentStoreUpdateIntervalPut(
        id: String,
        interval: String,
        requestBody: StoreUpdateIntervalStudentRequest? = null,
    ): DataWrapper<Student> =
        client
            .put("$baseUrl/students/$id/intervals/$interval") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun studentRemoveGuardian(
        student: Int,
        guardian: Int,
    ): DataWrapper<Student> = client.delete("$baseUrl/students/$student/guardians/$guardian").body()

    /** Access: Mod+ Required */
    suspend fun studentRestore(id: String): DataWrapper<Student> = client.post("$baseUrl/students/$id/restore").body()

    /** Access: Mod+ Required */
    suspend fun studentRestoreInterval(id: String): DataWrapper<Student> = client.post("$baseUrl/interval_student/$id/restore").body()

    /** Access: Mod+ Required */
    suspend fun studentBatch(requestBody: List<BatchStudentRequest>? = null): ListDataWrapper<Student> =
        client
            .post("$baseUrl/students/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun studentsStore(requestBody: StoreStudentRequest): DataWrapper<Student> =
        client
            .post("$baseUrl/students") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun studentsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Student> =
        client
            .get("$baseUrl/students") {
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

    /** Access: Not documented in current OpenAPI specification */
    fun studentsUpdate(id: String): DataWrapper<Student> = throw NotImplementedError("Request body schema for studentsUpdate is missing in the OpenAPI spec.")

    /** Access: Mod+ Required */
    suspend fun studentsDestroy(id: String) {
        client.delete("$baseUrl/students/$id")
    }

    /** Access: Any role */
    suspend fun studentsShow(
        id: String,
        include: List<String>? = null,
    ): DataWrapper<Student> =
        client
            .get("$baseUrl/students/$id") {
                parameter("include", include?.joinToString(","))
            }.body()

    /** Access: Teacher+ Required */
    suspend fun studentSetSubjectCalculation(
        id: String,
        subjectId: String,
        requestBody: SetSubjectCalculationStudentRequest,
    ): JsonObject =
        client
            .post("$baseUrl/students/$id/subjects/$subjectId/calculation") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun studentRemoveUser(
        studentId: String,
        userId: String,
    ): DataWrapper<Student> = client.delete("$baseUrl/students/$studentId/user/$userId").body()

    /** Access: Mod+ Required */
    suspend fun subjectRestore(id: String): DataWrapper<Subject> = client.post("$baseUrl/subjects/$id/restore").body()

    /** Access: Mod+ Required */
    suspend fun subjectBatch(requestBody: List<BatchSubjectRequest>? = null): ListDataWrapper<Subject> =
        client
            .post("$baseUrl/subjects/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun subjectsStore(requestBody: StoreSubjectRequest): DataWrapper<Subject> =
        client
            .post("$baseUrl/subjects") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun subjectsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Subject> =
        client
            .get("$baseUrl/subjects") {
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

    /** Access: Mod+ Required */
    suspend fun subjectsShow(id: String): DataWrapper<Subject> = client.get("$baseUrl/subjects/$id").body()

    /** Access: Not documented in current OpenAPI specification */
    fun subjectsUpdate(id: String): DataWrapper<Subject> = throw NotImplementedError("Request body schema for subjectsUpdate is missing in the OpenAPI spec.")

    /** Access: Mod+ Required */
    suspend fun subjectsDestroy(id: String) {
        client.delete("$baseUrl/subjects/$id")
    }

    /** Access: Mod+ Required */
    suspend fun substitutionPlansIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<SubstitutionPlan> =
        client
            .get("$baseUrl/substitution-plans") {
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

    /** Access: Mod+ Required */
    suspend fun substitutionPlansStore(requestBody: StoreSubstitutionPlanRequest): DataWrapper<SubstitutionPlan> =
        client
            .post("$baseUrl/substitution-plans") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun substitutionPlansShow(id: String): DataWrapper<SubstitutionPlan> = client.get("$baseUrl/substitution-plans/$id").body()

    /** Access: Mod+ Required */
    suspend fun substitutionPlansUpdate(
        id: String,
        requestBody: UpdateSubstitutionPlanRequest,
    ): DataWrapper<SubstitutionPlan> =
        client
            .put("$baseUrl/substitution-plans/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun substitutionPlansDestroy(id: String) {
        client.delete("$baseUrl/substitution-plans/$id")
    }

    /** Access: Mod+ Required */
    suspend fun substitutionPlanDayIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<SubstitutionPlanDay> =
        client
            .get("$baseUrl/substitution-plans/days") {
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

    /** Access: Any role */
    suspend fun substitutionPlanDayShow(date: String): DataWrapper<SubstitutionPlanDay> = client.get("$baseUrl/substitution-plans/days/$date").body()

    /** Access: Mod+ Required */
    suspend fun substitutionPlanLessonIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<SubstitutionPlanLesson> =
        client
            .get("$baseUrl/substitution-plans/lessons") {
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

    /** Access: Mod+ Required */
    suspend fun tagsStore(requestBody: StoreTagRequest): DataWrapper<Tag> =
        client
            .post("$baseUrl/types/tags") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun tagIndex(filter: String? = null): ListDataWrapper<Tag> =
        client
            .get("$baseUrl/types/tags") {
                parameter("filter", filter)
            }.body()

    /** Access: Mod+ Required */
    suspend fun tagsShow(id: String): DataWrapper<Tag> = client.get("$baseUrl/types/tags/$id").body()

    /** Access: Mod+ Required */
    suspend fun tagsUpdate(
        id: String,
        requestBody: UpdateTagRequest,
    ): DataWrapper<Tag> =
        client
            .put("$baseUrl/types/tags/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun tagsDestroy(id: String) {
        client.delete("$baseUrl/types/tags/$id")
    }

    /** Access: Mod+ Required */
    suspend fun teacherRefreshToken(id: String): DataWrapper<Teacher> = client.get("$baseUrl/teachers/$id/token").body()

    /** Access: Mod+ Required */
    suspend fun teacherRemoveToken(id: String): DataWrapper<Teacher> = client.delete("$baseUrl/teachers/$id/token").body()

    /** Access: Mod+ Required */
    suspend fun teacherBatchToken(requestBody: BatchTokenTeacherRequest): ListDataWrapper<Teacher> =
        client
            .post("$baseUrl/teachers/token") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun teacherRestore(id: String): DataWrapper<Teacher> = client.post("$baseUrl/teachers/$id/restore").body()

    /** Access: Mod+ Required */
    suspend fun teacherBatch(requestBody: List<BatchTeacherRequest>? = null): ListDataWrapper<Teacher> =
        client
            .post("$baseUrl/teachers/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun teachersStore(requestBody: StoreTeacherRequest): DataWrapper<Teacher> =
        client
            .post("$baseUrl/teachers") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun teachersIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<Teacher> =
        client
            .get("$baseUrl/teachers") {
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

    /** Access: Mod+ Required */
    suspend fun teachersUpdate(
        id: String,
        requestBody: UpdateTeacherRequest,
    ): DataWrapper<Teacher> =
        client
            .put("$baseUrl/teachers/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun teachersDestroy(id: String) {
        client.delete("$baseUrl/teachers/$id")
    }

    /** Access: Teacher+ Required */
    suspend fun teachersShow(id: String): DataWrapper<Teacher> = client.get("$baseUrl/teachers/$id").body()

    /** Access: Teacher+ Required */
    suspend fun teacherRemoveUser(id: String): DataWrapper<Teacher> = client.delete("$baseUrl/teachers/$id/user").body()

    /** Access: Any role */
    suspend fun timeTablesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<TimeTable> =
        client
            .get("$baseUrl/time-tables") {
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

    /** Access: Mod+ Required */
    suspend fun timeTablesStore(requestBody: StoreTimeTableRequest): DataWrapper<TimeTable> =
        client
            .post("$baseUrl/time-tables") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun timeTablesShow(id: String): DataWrapper<TimeTable> = client.get("$baseUrl/time-tables/$id").body()

    /** Access: Mod+ Required */
    suspend fun timeTablesUpdate(
        id: String,
        requestBody: UpdateTimeTableRequest,
    ): DataWrapper<TimeTable> =
        client
            .put("$baseUrl/time-tables/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun timeTablesDestroy(id: String) {
        client.delete("$baseUrl/time-tables/$id")
    }

    /** Access: Any role */
    suspend fun timeTableShowCurrent(): DataWrapper<TimeTable> = client.get("$baseUrl/time-tables/current").body()

    /** Access: Mod+ Required */
    suspend fun timeTableTimesStore(requestBody: StoreTimeTableTimeRequest): DataWrapper<TimeTableTime> =
        client
            .post("$baseUrl/time-table-times") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun timeTableTimesIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<TimeTableTime> =
        client
            .get("$baseUrl/time-table-times") {
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

    /** Access: Mod+ Required */
    suspend fun timeTableTimesUpdate(
        id: String,
        requestBody: UpdateTimeTableTimeRequest,
    ): DataWrapper<TimeTableTime> =
        client
            .put("$baseUrl/time-table-times/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun timeTableTimesDestroy(id: String) {
        client.delete("$baseUrl/time-table-times/$id")
    }

    /** Access: Teacher+ Required */
    suspend fun timeTableTimesShow(id: String): DataWrapper<TimeTableTime> = client.get("$baseUrl/time-table-times/$id").body()

    /** Access: Mod+ Required */
    suspend fun timeTableTimeLessonsStore(requestBody: StoreTimeTableTimeLessonRequest): DataWrapper<TimeTableTimeLesson> =
        client
            .post("$baseUrl/time-table-time-lessons") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun timeTableTimeLessonsIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<TimeTableTimeLesson> =
        client
            .get("$baseUrl/time-table-time-lessons") {
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

    /** Access: Mod+ Required */
    suspend fun timeTableTimeLessonsUpdate(
        id: String,
        requestBody: UpdateTimeTableTimeLessonRequest,
    ): DataWrapper<TimeTableTimeLesson> =
        client
            .put("$baseUrl/time-table-time-lessons/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun timeTableTimeLessonsDestroy(id: String) {
        client.delete("$baseUrl/time-table-time-lessons/$id")
    }

    /** Access: Teacher+ Required */
    suspend fun timeTableTimeLessonsShow(id: String): DataWrapper<TimeTableTimeLesson> = client.get("$baseUrl/time-table-time-lessons/$id").body()

    /** Access: Mod+ Required */
    suspend fun userDisableTwoFactorById(id: String? = null): SimpleSuccessResponse = client.get("$baseUrl/users/$id/2fa/disable").body()

    /** Access: Mod+ Required */
    suspend fun userGetNewPassword(id: String): SimplePasswordResponse = client.get("$baseUrl/users/$id/newpassword").body()

    /** Access: Not documented in current OpenAPI specification */
    suspend fun userResendMailById(id: String? = null): SimpleSuccessResponse = client.post("$baseUrl/users/$id/resend-mail").body()

    /** Access: Any role */
    suspend fun usersIndex(
        filterGroup: String? = null,
        filterStudent: String? = null,
        filterGuardian: String? = null,
        filterTeacher: String? = null,
        filterSubject: String? = null,
        filterRoom: String? = null,
        filterInterval: String? = null,
        filterYear: String? = null,
        filterRole: String? = null,
    ): ListDataWrapper<User> =
        client
            .get("$baseUrl/users") {
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

    /** Access: Any role */
    suspend fun usersShow(id: Int? = null): DataWrapper<User> = client.get("$baseUrl/users/$id").body()

    /** Access: Any role */
    suspend fun usersUpdate(
        id: Int? = null,
        requestBody: UpdateUserRequest? = null,
    ): DataWrapper<User> =
        client
            .put("$baseUrl/users/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun usersDestroy(id: Int? = null) {
        client.delete("$baseUrl/users/$id")
    }

    /** Access: Any role */
    suspend fun userMe(): DataWrapper<User> = client.get("$baseUrl/me").body()

    /** Access: Any role */
    suspend fun userUpdateMe(requestBody: UpdateUserRequest? = null): DataWrapper<User> =
        client
            .put("$baseUrl/user") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun userDestroyMe() {
        client.delete("$baseUrl/user")
    }

    /** Access: Any role */
    suspend fun userAddMembership(requestBody: AddMembershipUserRequest): Any =
        client
            .post("$baseUrl/user/token") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun userPostPassword(requestBody: PostPasswordUserRequest): DataWrapper<User> =
        client
            .post("$baseUrl/user/password") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun userEnableTwoFactor(): SimpleSecretResponse = client.get("$baseUrl/user/2fa/enable").body()

    /** Access: Any role */
    suspend fun userDisableTwoFactorMe(): SimpleSuccessResponse = client.get("$baseUrl/user/2fa/disable").body()

    /** Access: Any role */
    suspend fun userVerifyTwoFactor(requestBody: VerifyTwoFactorUserRequest? = null): SimpleVerifiedResponse =
        client
            .post("$baseUrl/user/2fa/verify") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun userAddFirebaseDevice(requestBody: AddFirebaseDeviceUserRequest): DataWrapper<FirebaseDevice> =
        client
            .post("$baseUrl/user/firebase-device") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun userDeleteFirebaseDevice(
        id: String? = null,
        queryId: Int? = null,
        queryToken: String? = null,
    ): SimpleSuccessResponse =
        client
            .delete("$baseUrl/user/firebase-device/$id") {
                parameter("id", queryId)
                parameter("token", queryToken)
            }.body()

    /** Access: Any role */
    suspend fun userDeleteSocialite(userSocialite: Int): SimpleSuccessResponse = client.delete("$baseUrl/user/auth-provider/$userSocialite").body()

    /** Access: Not documented in current OpenAPI specification */
    suspend fun userResendMailMe(): SimpleSuccessResponse = client.post("$baseUrl/user/resend-mail").body()

    /** Access: Any role */
    suspend fun userChangeSchool(requestBody: ChangeSchoolUserRequest): DataWrapper<User> =
        client
            .put("$baseUrl/user/school") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun userLogoutApiMe() {
        client.get("$baseUrl/me/logout")
    }

    /** Access: Any role */
    suspend fun userLogoutApiUser() {
        client.get("$baseUrl/user/logout")
    }

    /** Access: Any role */
    suspend fun yearIndex(): ListDataWrapper<Year> = client.get("$baseUrl/years").body()

    /** Access: Mod+ Required */
    suspend fun yearsStore(requestBody: StoreYearRequest): DataWrapper<Year> =
        client
            .post("$baseUrl/years") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun yearsShow(id: String): DataWrapper<Year> = client.get("$baseUrl/years/$id").body()

    /** Access: Mod+ Required */
    suspend fun yearsUpdate(
        id: String,
        requestBody: UpdateYearRequest,
    ): DataWrapper<Year> =
        client
            .put("$baseUrl/years/$id") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun yearsDestroy(id: String) {
        client.delete("$baseUrl/years/$id")
    }

    /** Access: Mod+ Required */
    suspend fun yearRestore(id: String): DataWrapper<Year> = client.post("$baseUrl/years/$id/restore").body()

    /** Access: Any role */
    suspend fun yearSetCurrent(requestBody: SetCurrentYearRequest? = null) =
        client
            .post("$baseUrl/years/current") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

    /** Access: Mod+ Required */
    suspend fun sendUserVerification(id: Int): SimpleSuccessResponse = client.post("$baseUrl/users/$id/send-verification").body()

    /** Access: Mod+ Required */
    suspend fun sendUserPasswordReset(id: Int): SimpleSuccessResponse = client.post("$baseUrl/users/$id/send-password-reset").body()

    /** Access: Any role */
    suspend fun announcementRespond(
        announcement: Int,
        requestBody: StoreAnnouncementResponseRequest? = null,
    ): DataWrapper<Announcement> =
        client
            .post("$baseUrl/announcements/$announcement/respond") {
                contentType(ContentType.Application.Json)
                requestBody?.let { setBody(it) }
            }.body()

    /** Access: Any role */
    suspend fun attachmentsIndex(
        attachmentableType: String,
        attachmentableId: Int,
    ): ListDataWrapper<Attachment> =
        client
            .get("$baseUrl/attachments") {
                parameter("attachmentable_type", attachmentableType)
                parameter("attachmentable_id", attachmentableId)
            }.body()

    /** Access: Any role */
    suspend fun attachmentsStore(requestBody: StoreAttachmentRequest): DataWrapper<Attachment> =
        client
            .post("$baseUrl/attachments") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            requestBody.attachmentableType?.let { append("attachmentable_type", it) }
                            requestBody.attachmentableId?.let { append("attachmentable_id", it.toString()) }
                            append(
                                "attachment",
                                requestBody.attachment,
                                Headers.build {
                                    append(HttpHeaders.ContentType, ContentType.Application.OctetStream)
                                    append(HttpHeaders.ContentDisposition, "filename=attachment")
                                },
                            )
                        },
                    ),
                )
            }.body()

    /** Access: Any role */
    suspend fun attachmentsShow(attachmentId: String): DataWrapper<Attachment> =
        client
            .get("$baseUrl/attachments/$attachmentId") {
                header(HttpHeaders.Accept, ContentType.Application.Json)
            }.body()

    /** Access: Any role */
    suspend fun attachmentsDestroy(attachment: Int) {
        client.delete("$baseUrl/attachments/$attachment")
    }

    /** Access: Teacher+ Required */
    suspend fun checklistStudentsIndex(checklist: Int): ListDataWrapper<ChecklistStudent> = client.get("$baseUrl/checklists/$checklist/students").body()

    /** Access: Teacher+ Required */
    suspend fun checklistStudentsStore(
        checklist: Int,
        requestBody: StoreChecklistStudentRequest,
    ): DataWrapper<ChecklistStudent> =
        client
            .post("$baseUrl/checklists/$checklist/students") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun checklistStudentsBatch(
        checklist: Int,
        requestBody: BatchChecklistStudentRequest,
    ): ListDataWrapper<ChecklistStudent> =
        client
            .post("$baseUrl/checklists/$checklist/students/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun checklistStudentsUpdate(
        checklist: Int,
        student: Int,
        requestBody: UpdateChecklistStudentRequest,
    ): DataWrapper<ChecklistStudent> =
        client
            .put("$baseUrl/checklists/$checklist/students/$student") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Teacher+ Required */
    suspend fun checklistStudentsDestroy(
        checklist: Int,
        student: Int,
    ) {
        client.delete("$baseUrl/checklists/$checklist/students/$student")
    }

    /** Access: Teacher+ Required */
    suspend fun finalgradesBatch(requestBody: BatchFinalgradeRequest): ListDataWrapper<Finalgrade> =
        client
            .post("$baseUrl/finalgrades/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun gradeMarkRead(grade: String): DataWrapper<Grade> = client.post("$baseUrl/grades/$grade/read").body()

    /** Access: Mod+ Required */
    suspend fun guardianSendTokenEmail(id: String): SimpleSuccessResponse = client.post("$baseUrl/guardians/$id/token/send-email").body()

    /** Access: Teacher+ Required */
    suspend fun studentIntervalShow(
        id: String,
        interval: String,
    ): DataWrapper<IntervalStudent> = client.get("$baseUrl/students/$id/intervals/$interval").body()

    /** Access: Teacher+ Required */
    suspend fun studentIntervalsBatch(requestBody: BatchIntervalStudentRequest): ListDataWrapper<IntervalStudent> =
        client
            .post("$baseUrl/students/intervals/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun studentSendTokenEmail(id: String): SimpleSuccessResponse = client.post("$baseUrl/students/$id/token/send-email").body()

    /** Access: Mod+ Required */
    suspend fun studentSendGuardianTokenEmail(id: String): SimpleSuccessResponse = client.post("$baseUrl/students/$id/token-guardian/send-email").body()

    /** Access: Teacher+ Required */
    suspend fun journalLessonStudentsBatch(requestBody: BatchJournalLessonStudentRequest): ListDataWrapper<JournalLessonStudent> =
        client
            .post("$baseUrl/journal/lesson-student/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Any role */
    suspend fun journalDayStudentStatisticsCount(
        filterStudent: String? = null,
        filterGroup: String? = null,
        filterYear: String? = null,
        filterRange: String? = null,
    ): ListDataWrapper<JournalDayStudentCount> =
        client
            .get("$baseUrl/journal/day-student/count") {
                parameter("filter[student]", filterStudent)
                parameter("filter[group]", filterGroup)
                parameter("filter[year]", filterYear)
                parameter("filter[range]", filterRange)
            }.body()

    /** Access: Any role */
    suspend fun journalLessonStudentStatisticsCount(
        filterStudent: String? = null,
        filterGroup: String? = null,
        filterYear: String? = null,
        filterRange: String? = null,
    ): ListDataWrapper<JournalLessonStudentCount> =
        client
            .get("$baseUrl/journal/lesson-student/count") {
                parameter("filter[student]", filterStudent)
                parameter("filter[group]", filterGroup)
                parameter("filter[year]", filterYear)
                parameter("filter[range]", filterRange)
            }.body()

    /** Access: Any role */
    suspend fun journalLessonStudentStatisticsByWeek(
        filterStudent: String? = null,
        filterYear: String? = null,
    ): JsonObject =
        client
            .get("$baseUrl/journal/lesson-student/by-week") {
                parameter("filter[student]", filterStudent)
                parameter("filter[year]", filterYear)
            }.body()

    /** Access: Any role */
    suspend fun journalLessonStudentStatisticsBySlot(
        filterStudent: String? = null,
        filterYear: String? = null,
    ): JsonObject =
        client
            .get("$baseUrl/journal/lesson-student/by-slot") {
                parameter("filter[student]", filterStudent)
                parameter("filter[year]", filterYear)
            }.body()

    /** Access: Any role */
    suspend fun journalLessonStudentStatisticsByLesson(
        filterStudent: String? = null,
        filterYear: String? = null,
    ): JsonObject =
        client
            .get("$baseUrl/journal/lesson-student/by-lesson") {
                parameter("filter[student]", filterStudent)
                parameter("filter[year]", filterYear)
            }.body()

    /** Access: Any role */
    suspend fun journalNotesIndex(
        type: String,
        notableId: String,
        include: List<String>? = null,
    ): ListDataWrapper<JournalNote> =
        client
            .get("$baseUrl/journal/notes/$type/$notableId") {
                parameter("include", include?.joinToString(","))
            }.body()

    /** Access: Teacher+ Required */
    suspend fun journalLessonSelfServiceGenerateUrl(id: String): SimpleSuccessResponse = client.get("$baseUrl/journal/lessons/$id/students/self-service").body()

    /** Access: Any role */
    suspend fun journalLessonStudentSelfServiceShow(id: String): DataWrapper<JournalLessonStudent> = client.get("$baseUrl/journal/lesson-student/$id/self-service").body()

    /** Access: Any role */
    suspend fun journalLessonStudentSelfServiceConfirm(id: String): DataWrapper<JournalLessonStudent> = client.post("$baseUrl/journal/lesson-student/$id/self-service").body()

    /** Access: Any role */
    suspend fun newsletterDestroy() {
        client.delete("$baseUrl/newsletter")
    }

    /** Access: Authentication requirement not documented */
    suspend fun newsletterStore(requestBody: StoreNewsletterRequest): SimpleSuccessResponse =
        client
            .post("$baseUrl/newsletter") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

    /** Access: Mod+ Required */
    suspend fun substitutionPlanDayStoreOrUpdatePost(
        date: String,
        requestBody: StoreOrUpdateSubstitutionPlanDayRequest? = null,
    ): DataWrapper<SubstitutionPlanDay> =
        client
            .post("$baseUrl/substitution-plans/days/$date") {
                contentType(ContentType.Application.Json)
                requestBody?.let { setBody(it) }
            }.body()

    /** Access: Mod+ Required */
    suspend fun substitutionPlanDayStoreOrUpdatePut(
        date: String,
        requestBody: StoreOrUpdateSubstitutionPlanDayRequest? = null,
    ): DataWrapper<SubstitutionPlanDay> =
        client
            .put("$baseUrl/substitution-plans/days/$date") {
                contentType(ContentType.Application.Json)
                requestBody?.let { setBody(it) }
            }.body()

    /** Access: Mod+ Required */
    suspend fun substitutionPlanDayDestroy(date: String) {
        client.delete("$baseUrl/substitution-plans/days/$date")
    }

    /** Access: Teacher+ Required */
    suspend fun timeTableTimeShowCurrent(): DataWrapper<TimeTableTime> = client.get("$baseUrl/time-table-times/current").body()

    /** Access: Any role */
    suspend fun userTwoFactorEnable(): JsonObject = client.get("$baseUrl/user/2fa/enable").body()

    /** Access: Any role */
    suspend fun userTwoFactorDisable(): SimpleSuccessResponse = client.get("$baseUrl/user/2fa/disable").body()

    /** Access: Any role */
    suspend fun userTwoFactorVerify(requestBody: VerifyTwoFactorUserRequest): SimpleVerifiedResponse =
        client
            .post("$baseUrl/user/2fa/verify") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()
}
