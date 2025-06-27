@file:Suppress("unused")

package com.hansholz.bestenotenapp.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class Absence(
    val id: Int,
    val from: String,
    val to: String,
    val note: String? = null,
    @SerialName("note_teacher") val noteTeacher: String? = null,
    @SerialName("note_guardian") val noteGuardian: String? = null,
    @SerialName("recorded_at") val recordedAt: String? = null,
    val type: AbsenceType,
    val teacher: Teacher? = null,
    val batch: AbsenceBatch? = null,
    @SerialName("batch_id") val batchId: Int? = null,
    val student: Student? = null,
    val subjects: List<Subject>? = null,
    val verification: AbsenceVerification? = null,
    @SerialName("lesson_students") val lessonStudents: List<JournalLessonStudent>? = null,
    @SerialName("lesson_students_count") val lessonStudentsCount: String? = null,
    val lessons: List<JournalLessonStudent>? = null,
    @SerialName("lessons_count") val lessonsCount: String? = null
)

@Serializable
data class AbsenceBatch(
    val id: Int,
    @SerialName("single_group") val singleGroup: String,
    val students: List<Student>? = null,
    val from: String? = null,
    val to: String? = null,
    val note: String? = null,
    @SerialName("note_teacher") val noteTeacher: String? = null,
    @SerialName("note_guardian") val noteGuardian: String? = null,
    @SerialName("recorded_at") val recordedAt: String? = null,
    val type: AbsenceType? = null,
    val teacher: Teacher? = null,
    val batch: AbsenceBatch? = null,
    @SerialName("batch_id") val batchId: Int? = null,
    val student: Student? = null,
    val subjects: List<Subject>? = null,
    val verification: AbsenceVerification? = null,
    @SerialName("lesson_students") val lessonStudents: List<JournalLessonStudent>? = null,
    @SerialName("lesson_students_count") val lessonStudentsCount: String? = null,
    val lessons: List<JournalLessonStudent>? = null,
    @SerialName("lessons_count") val lessonsCount: String? = null
)

@Serializable
data class AbsenceType(
    val id: Int,
    val name: String,
    val default: Int,
    @SerialName("editable_as") val editableAs: String,
    @SerialName("default_present") val defaultPresent: Int? = null,
    val absences: List<Absence>? = null
)

@Serializable
data class AbsenceVerification(
    val id: Int,
    val confirmed: Boolean,
    @SerialName("local_id") val localId: String? = null,
    val note: String? = null,
    @SerialName("recorded_at") val recordedAt: String? = null,
    val teacher: Teacher? = null,
    val absence: Absence? = null
)

@Serializable
data class AddFirebaseDeviceUserRequest(
    val token: String,
    val name: String? = null,
    val language: String? = null
)

@Serializable
data class Announcement(
    val id: Int,
    val title: String,
    val message: String? = null,
    val from: String,
    val to: String,
    @SerialName("for") val for_: String,
    @SerialName("need_confirmation_from_student") val needConfirmationFromStudent: Int,
    @SerialName("need_confirmation_from_guardian") val needConfirmationFromGuardian: Int,
    @SerialName("students_count") val studentsCount: String? = null,
    @SerialName("guardians_count") val guardiansCount: String? = null,
    @SerialName("all_students_count") val allStudentsCount: String? = null,
    @SerialName("all_guardians_count") val allGuardiansCount: String? = null,
    @SerialName("single_group") val singleGroup: Boolean,
    val type: AnnouncementType? = null,
    val groups: List<Group>? = null,
    val students: List<Student>? = null,
    val guardians: List<Guardian>? = null,
    @SerialName("all_students") val allStudents: List<Student>? = null,
    @SerialName("all_guardians") val allGuardians: List<Guardian>? = null,
    val teacher: Teacher? = null
)

@Serializable
data class AnnouncementType(
    val id: Int,
    val name: String,
    val color: String? = null,
    val default: Int,
    @SerialName("default_for") val defaultFor: String,
    val announcements: List<Announcement>? = null
)

@Serializable
data class ArrayCollection(
    val data: JsonArray,
    val meta: Meta
)

@Serializable
data class BatchFinalCertificateRequest(
    val id: String? = null,
    @SerialName("year_id") val yearId: String? = null,
    @SerialName("student_id") val studentId: String? = null,
    @SerialName("certificate_type") val certificateType: String? = null,
    @SerialName("certificate_date") val certificateDate: String? = null,
    val custom: List<String>? = null,
    @SerialName("year_ids") val yearIds: List<String>? = null,
    @SerialName("import_action") val importAction: String
)

@Serializable
data class BatchGroupRequest(
    val id: Int? = null,
    val name: String? = null,
    @SerialName("local_id") val localId: String,
    val custom: String? = null,
    val subjects: List<String>? = null,
    val students: List<String>? = null,
    val tags: List<String>? = null,
    val importAction: String,
    @SerialName("year_id") val yearId: String
)

@Serializable
data class BatchGuardianRequest(
    val id: String? = null,
    val name: String? = null,
    val forename: String? = null,
    @SerialName("email_private") val emailPrivate: String? = null,
    @SerialName("email_business") val emailBusiness: String? = null,
    @SerialName("phone_private") val phonePrivate: String? = null,
    @SerialName("phone_business") val phoneBusiness: String? = null,
    @SerialName("local_id") val localId: String,
    val importAction: String,
    val students: List<String>? = null
)

@Serializable
data class BatchStudentRequest(
    val id: Int? = null,
    val name: String? = null,
    val nickname: String? = null,
    val forename: String? = null,
    @SerialName("is_adult") val isAdult: Boolean? = null,
    val birthday: String? = null,
    val phone: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("local_id") val localId: String? = null,
    val deleted: Boolean? = null,
    val custom: List<String>? = null,
    val tags: List<String>? = null,
    val importAction: String
)

@Serializable
data class BatchSubjectRequest(
    val id: Int? = null,
    val name: String,
    @SerialName("local_id") val localId: String,
    val tags: List<String>? = null,
    val importAction: String
)

@Serializable
data class BatchTeacherRequest(
    val id: Int? = null,
    val name: String? = null,
    val forename: String? = null,
    @SerialName("local_id") val localId: String,
    val tags: List<String>? = null,
    val importAction: String
)

@Serializable
data class BatchTokenGuardianRequest(
    val ids: List<String>,
    val mode: String? = null
)

@Serializable
data class BatchTokenGuardianStudentRequest(
    val ids: List<String>,
    val mode: String? = null
)

@Serializable
data class BatchTokenStudentRequest(
    val ids: List<String>,
    val mode: String? = null
)

@Serializable
data class BatchTokenTeacherRequest(
    val ids: List<String>,
    val mode: String? = null
)

@Serializable
data class CertificateGrade(
    val id: Int,
    val value: String,
    val type: String,
    val name: String? = null,
    @SerialName("given_at") val givenAt: String,
    val subject: Subject? = null,
    val teacher: Teacher? = null,
    val year: Year? = null,
    val student: Student? = null
)

@Serializable
data class ChangeSchoolUserRequest(
    val id: Int
)

@Serializable
data class Checklist(
    val id: Int,
    val name: String,
    val date: String,
    val description: String? = null,
    @SerialName("for") val for_: String,
    @SerialName("unchecked_count") val uncheckedCount: String,
    @SerialName("checked_count") val checkedCount: String,
    @SerialName("single_group") val singleGroup: Boolean,
    val type: ChecklistType? = null,
    val groups: List<Group>? = null,
    val checks: List<ChecklistStudent>? = null,
    val teacher: Teacher? = null
)

@Serializable
data class ChecklistStudent(
    val id: String,
    @SerialName("student_id") val studentId: Int,
    val forename: String,
    val nickname: String,
    val name: String,
    val gender: String,
    val checked: String,
    val note: String,
    @SerialName("checked_at") val checkedAt: String? = null,
    val teacher: Teacher? = null,
    val tags: List<Tag>? = null
)

@Serializable
data class ChecklistType(
    val id: Int,
    val name: String,
    val checklists: List<Checklist>? = null
)

@Serializable
data class GradeCollection(
    val id: Int,
    val type: String,
    val weighting: Int,
    val name: String? = null,
    @SerialName("given_at") val givenAt: String,
    @SerialName("visible_from") val visibleFrom: String? = null,
    @SerialName("interval_id") val intervalId: Int,
    @SerialName("subject_id") val subjectId: Int,
    @SerialName("teacher_id") val teacherId: Int,
    val interval: Interval? = null,
    val group: Group? = null,
    val subject: Subject? = null,
    val teacher: Teacher? = null,
    val grades: List<Grade>? = null,
    val histories: List<History>? = null
)

@Serializable
data class ExecuteNotificationActionRequest(
    val action: String
)

@Serializable
data class Favorite(
    val id: Int,
    val student: Student,
    val group: Group,
    val subject: Subject
)

@Serializable
data class FinalCertificate(
    val id: Int,
    @SerialName("certificate_type") val certificateType: String? = null,
    @SerialName("certificate_date") val certificateDate: String,
    val custom: String? = null,
    val student: Student? = null,
    val year: Year? = null,
    val years: List<Year>? = null
)

@Serializable
data class Finalgrade(
    val id: Int,
    val value: String? = null,
    @SerialName("value_int") val valueInt: Double? = null,
    @SerialName("value_calc") val valueCalc: JsonPrimitive? = null,
    @SerialName("value_calc_int") val valueCalcInt: String? = null,
    @SerialName("calculation_rule") val calculationRule: String? = null,
    @SerialName("calculation_verbal") val calculationVerbal: String? = null,
    @SerialName("calculation_for") val calculationFor: String,
    @SerialName("subject_id") val subjectId: Int,
    @SerialName("interval_id") val intervalId: Int,
    @SerialName("teacher_id") val teacherId: Int? = null,
    val levels: List<Level>? = null,
    val student: Student? = null,
    val teacher: Teacher? = null,
    val subject: Subject? = null,
    val interval: Interval? = null,
    val histories: List<History>? = null
)

@Serializable
data class FirebaseDevice(
    val id: Int,
    val name: String,
    val language: String,
    @SerialName("user_id") val userId: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class Grade(
    val id: Int,
    val value: String,
    @SerialName("given_at") val givenAt: String,
    val student: Student? = null,
    val subject: Subject? = null,
    val collection: GradeCollection? = null,
    val teacher: Teacher? = null,
    val histories: List<History>? = null
)

@Serializable
data class Group(
    val id: Int,
    @SerialName("local_id") val localId: String? = null,
    val name: String,
    val meta: Int,
    val custom: String? = null,
    @SerialName("level_id") val levelId: Int? = null,
    @SerialName("year_id") val yearId: Int,
    val level: Level? = null,
    val levels: List<Level>? = null,
    val subjects: List<Subject>? = null,
    val collections: List<GradeCollection>? = null,
    val teachers: List<Teacher>? = null,
    val students: List<Student>? = null,
    val tags: List<Tag>? = null
)

@Serializable
data class Guardian(
    val id: Int,
    @SerialName("local_id") val localId: String? = null,
    val forename: String,
    val name: String,
    @SerialName("email_private") val emailPrivate: String? = null,
    @SerialName("email_business") val emailBusiness: String? = null,
    @SerialName("phone_private") val phonePrivate: String? = null,
    @SerialName("phone_business") val phoneBusiness: String? = null,
    val token: String? = null,
    @SerialName("token_valid_until") val tokenValidUntil: String? = null,
    val deleted: Boolean? = null,
    val students: List<Student>? = null,
    @SerialName("students_count") val studentsCount: String? = null,
    val intervals: List<Interval>? = null,
    val tags: List<Tag>? = null,
    val user: User? = null
)

@Serializable
data class History(
    val id: Int,
    @SerialName("history_entry_type") val historyEntryType: String,
    @SerialName("history_entry_id") val historyEntryId: Int,
    val body: String,
    val action: String? = null,
    val attr: String? = null,
    @SerialName("old_value") val oldValue: String? = null,
    @SerialName("conductor_id") val conductorId: Int? = null,
    @SerialName("conductor_type") val conductorType: String,
    val conductor: Conductor? = null,
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
data class Importer(
    val id: Int,
    val active: Int,
    @SerialName("secret_student") val secretStudent: String? = null,
    @SerialName("secret_teacher") val secretTeacher: String? = null,
    @SerialName("webhook_secret") val webhookSecret: String? = null,
    val logs: List<ImporterLog>? = null
)

@Serializable
data class ImporterLog(
    val type: String,
    val message: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class ImporterStundenplan24(
    val id: Int,
    val name: String,
    val version: Int,
    @SerialName("splan_url") val splanUrl: String? = null,
    @SerialName("mobil_url") val mobilUrl: String? = null,
    @SerialName("moble_url") val mobleUrl: String? = null,
    @SerialName("username_teacher") val usernameTeacher: String? = null,
    @SerialName("password_teacher") val passwordTeacher: String,
    @SerialName("username_student") val usernameStudent: String? = null,
    @SerialName("password_student") val passwordStudent: String,
    @SerialName("time_table_meta_id") val timeTableMetaId: Int? = null,
    @SerialName("substitution_plan_meta_id") val substitutionPlanMetaId: Int? = null,
    val logs: List<ImporterLog>? = null
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
    @SerialName("interval_ids") val intervalIds: List<String>,
    @SerialName("year_id") val yearId: Int,
    val year: List<Year>? = null,
    @SerialName("certificate_type") val certificateType: String? = null,
    @SerialName("certificate_date") val certificateDate: String? = null,
    val custom: String? = null,
    @SerialName("not_present_without_absence_count") val notPresentWithoutAbsenceCount: String? = null,
    @SerialName("not_present_with_absence_count") val notPresentWithAbsenceCount: String? = null
)

@Serializable
data class JournalDay(
    val id: String,
    val date: String,
    @SerialName("time_name") val timeName: String,
    val lessons: List<JournalLesson>? = null,
    val notes: List<JournalNote>? = null,
    val week: JournalWeek? = null
)

@Serializable
data class JournalDayStudent(
    val id: String,
    val present: Int,
    @SerialName("present_mixed") val presentMixed: String,
    @SerialName("too_early") val tooEarly: Int? = null,
    @SerialName("too_late") val tooLate: Int? = null,
    val dates: String? = null,
    val student: Student? = null,
    val absence: Absence? = null,
    @SerialName("absence_mixed") val absenceMixed: String? = null,
    val teacher: Teacher? = null,
    val day: JournalDay? = null
)

@Serializable
data class JournalLesson(
    val id: String,
    val nr: String,
    val status: String,
    val ids: String? = null,
    val nrs: String? = null,
    val source: String? = null,
    val times: List<TimeTableTimeLesson>? = null,
    val subject: Subject? = null,
    val group: Group? = null,
    val students: List<Student>? = null,
    val statuses: List<JournalLessonStudent>? = null,
    val teachers: List<Teacher>? = null,
    val rooms: List<Room>? = null,
    val notes: List<JournalNote>? = null,
    val day: JournalDay? = null,
    val time: TimeTableTimeLesson? = null,
    val owner: Teacher? = null
)

@Serializable
data class JournalLessonStudent(
    val id: String,
    val student: Student? = null,
    val ids: String? = null,
    val present: Int,
    @SerialName("too_early") val tooEarly: Int? = null,
    @SerialName("too_late") val tooLate: Int? = null,
    @SerialName("missing_homework") val missingHomework: Int? = null,
    @SerialName("missing_equipment") val missingEquipment: Int? = null,
    val absence: Absence? = null,
    val teacher: Teacher? = null,
    val lesson: JournalLesson? = null,
    @SerialName("journal_lesson_ids") val journalLessonIds: String? = null,
    val notes: List<JournalNote>? = null
)

@Serializable
data class JournalNote(
    val id: String,
    val ids: String? = null,
    val description: String,
    @SerialName("for") val for_: String,
    val type: JournalNoteType? = null,
    val group: Group? = null,
    val source: String? = null,
    val notable: JournalLessonStudent? = null,
    val teacher: Teacher? = null,
    @SerialName("notable_type") val notableType: String
)

@Serializable
data class JournalNoteType(
    val id: Int,
    val name: String,
    val color: String? = null,
    val default: Int,
    @SerialName("default_for") val defaultFor: String,
    @SerialName("journal_notable_type") val journalNotableType: String
)

@Serializable
data class JournalWeek(
    val id: String,
    @SerialName("calendar_year") val calendarYear: Int,
    val nr: Int,
    @SerialName("year_id") val yearId: String,
    val days: List<JournalDay>? = null,
    val notes: List<JournalNote>? = null
)

@Serializable
data class Level(
    val id: Int,
    val name: String,
    @SerialName("interval_type") val intervalType: String,
    @SerialName("time_type") val timeType: String,
    @SerialName("best_grade") val bestGrade: Int? = null,
    @SerialName("worst_grade") val worstGrade: Int? = null,
    val intervals: List<Interval>? = null,
    val times: List<TimeTableTime>? = null
)

@Serializable
data class MarkReadAnnouncementRequest(
    @SerialName("guardian_id") val guardianId: Int? = null,
    @SerialName("student_id") val studentId: Int? = null,
    @SerialName("read_at") val readAt: String? = null
)

@Serializable
data class Note(
    val id: Int,
    val title: String,
    val description: String,
    @SerialName("for") val for_: String,
    @SerialName("note_type") val noteType: NoteType? = null,
    @SerialName("recorded_at") val recordedAt: String,
    val teacher: Teacher? = null,
    val student: Student? = null,
    val group: Group? = null
)

@Serializable
data class NoteType(
    val id: Int,
    val color: String? = null,
    val name: String,
    @SerialName("notable_type") val notableType: String,
    val default: Int,
    @SerialName("default_for") val defaultFor: String
)

@Serializable
data class Notification(
    val id: String,
    @SerialName("notification_type") val notificationType: String,
    @SerialName("available_actions") val availableActions: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("read_at") val readAt: String,
    @SerialName("notifiable_type") val notifiableType: String,
    val data: String
)

@Serializable
data class PostPasswordUserRequest(
    @SerialName("old_password") val oldPassword: String,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String
)

@Serializable
data class PushImporterRequest(
    val data: List<String>,
    val meta: PushImporterMeta
)

@Serializable
data class PushImporterMeta(
    val version: String,
    val type: String
)

@Serializable
data class Report(
    val id: Int,
    val name: String,
    @SerialName("for") val for_: String,
    val range: String,
    val model: String,
    @SerialName("filter_result") val filterResult: List<JsonObject>? = null,
    @SerialName("group_by") val groupBy: String? = null,
    @SerialName("aggregate_type") val aggregateType: String,
    @SerialName("teacher_id") val teacherId: Int? = null
)

@Serializable
data class ReportFilterResult(
    val group: String? = null,
    val student: String? = null,
    val interval: String? = null,
    val subject: String? = null,
    val teacher: String? = null,
    val room: String? = null,
    @SerialName("lessThanOrEqual") val lessThanOrEqual: String? = null,
    @SerialName("greaterThanOrEqual") val greaterThanOrEqual: String? = null,
    @SerialName("calculatedLessThanOrEqual") val calculatedLessThanOrEqual: String? = null,
    @SerialName("calculatedGreaterThanOrEqual") val calculatedGreaterThanOrEqual: String? = null,
    val lesson: String? = null,
    val nr: String? = null,
    @SerialName("hasNotes") val hasNotes: String? = null,
    @SerialName("hasStatuses") val hasStatuses: String? = null,
    @SerialName("tooEarly") val tooEarly: String? = null,
    @SerialName("tooLate") val tooLate: String? = null,
    @SerialName("missingHomework") val missingHomework: String? = null,
    @SerialName("missingEquipment") val missingEquipment: String? = null,
    @SerialName("valueSet") val valueSet: String? = null,
    val status: String? = null,
    @SerialName("statusNot") val statusNot: String? = null,
    val level: String? = null,
    val present: String? = null,
    @SerialName("collectionType") val collectionType: String? = null,
    @SerialName("notableType") val notableType: String? = null,
    val type: String? = null,
    @SerialName("withoutJournal") val withoutJournal: String? = null,
    val complete: String? = null,
    val verified: String? = null,
    @SerialName("hasAbsence") val hasAbsence: String? = null,
    @SerialName("hasVerifiedAbsence") val hasVerifiedAbsence: String? = null,
    @SerialName("hasUnverifiedAbsence") val hasUnverifiedAbsence: String? = null
)

@Serializable
data class ReportDataProcessing(
    val message: String,
    val hash: String
)

@Serializable
data class ReportMetaSuccess(
    val status: String,
    @SerialName("generated_at") val generatedAt: String
)

@Serializable
data class ReportMetaProcessing(
    val status: String,
    @SerialName("generated_at") val generatedAt: String? = null
)

@Serializable
sealed interface ReportResult {
    @Serializable
    data class Success(val data: List<String>, val meta: ReportMetaSuccess) : ReportResult
    @Serializable
    data class Processing(val data: ReportDataProcessing, val meta: ReportMetaProcessing) : ReportResult
    @Serializable
    data class Error(val message: String) : ReportResult
}


@Serializable
data class Room(
    val id: Int,
    @SerialName("local_id") val localId: String
)

@Serializable
data class School(
    val id: Int,
    val customer: Boolean,
    val name: String,
    val email: String,
    val type: String? = null,
    @SerialName("postal_second_line") val postalSecondLine: String? = null,
    val street: String,
    @SerialName("street_nr") val streetNr: String,
    @SerialName("postal_code") val postalCode: String,
    val city: String,
    val state: String,
    @SerialName("logo_url") val logoUrl: String? = null,
)

@Serializable
data class SeatingPlan(
    val id: Int,
    val width: Int,
    val height: Int,
    val room: Room? = null,
    val group: Group? = null,
    val seats: List<SeatingPlanStudent>? = null
)

@Serializable
data class SeatingPlanStudent(
    val id: String,
    @SerialName("student_id") val studentId: Int,
    val forename: String,
    val nickname: String,
    val name: String,
    val gender: String,
    val x: String,
    val y: String,
    @SerialName("changed_recently") val changedRecently: Boolean,
    @SerialName("meta_groups") val metaGroups: List<Group>? = null,
    val tags: List<Tag>? = null
)

@Serializable
data class SetCurrentYearRequest(
    val id: Int? = null
)

@Serializable
data class SetSubjectCalculationStudentRequest(
    val calculation: String,
    val grade: String? = null
)

@Serializable
data class StoreAbsenceBatchRequest(
    val from: String,
    val to: String,
    @SerialName("type_id") val typeId: Int,
    @SerialName("student_ids") val studentIds: List<String>,
    @SerialName("note_teacher") val noteTeacher: String? = null,
    @SerialName("recorded_at") val recordedAt: String? = null,
    @SerialName("lesson_ids") val lessonIds: List<String>? = null,
    @SerialName("subject_ids") val subjectIds: List<String>? = null,
    val verification: StoreAbsenceVerificationRequest? = null
)

@Serializable
data class StoreAbsenceTypeRequest(
    val name: String,
    val default: Boolean,
    @SerialName("editable_as") val editableAs: String? = null,
    @SerialName("default_present") val defaultPresent: Boolean? = null
)

@Serializable
data class StoreAbsenceVerificationRequest(
    val confirmed: Boolean? = null,
    @SerialName("local_id") val localId: String? = null,
    val note: String? = null,
    @SerialName("recorded_at") val recordedAt: String? = null
)

@Serializable
data class StoreAnnouncementRequest(
    val title: String,
    val message: String? = null,
    val from: String,
    val to: String,
    @SerialName("for") val for_: String,
    @SerialName("need_confirmation_from_student") val needConfirmationFromStudent: Boolean,
    @SerialName("need_confirmation_from_guardian") val needConfirmationFromGuardian: Boolean,
    @SerialName("type_id") val typeId: Int? = null,
    @SerialName("group_ids") val groupIds: List<Int>
)

@Serializable
data class StoreAnnouncementTypeRequest(
    val name: String,
    val color: String? = null,
    val default: Boolean,
    @SerialName("default_for") val defaultFor: String
)

@Serializable
data class StoreCertificateGradeRequest(
    val value: String,
    val type: String,
    val name: String? = null,
    @SerialName("given_at") val givenAt: String? = null,
    @SerialName("student_id") val studentId: Int,
    @SerialName("subject_id") val subjectId: String,
    @SerialName("year_id") val yearId: String
)

@Serializable
data class StoreChecklistRequest(
    val name: String,
    val date: String,
    val description: String? = null,
    @SerialName("for") val for_: String,
    @SerialName("type_id") val typeId: Int? = null,
    @SerialName("group_ids") val groupIds: List<Int>,
    val checks: StoreChecklistCheck? = null
)

@Serializable
data class StoreChecklistCheck(
    val checked: Boolean,
    @SerialName("checked_at") val checkedAt: String? = null,
    val note: String? = null
)

@Serializable
data class StoreChecklistTypeRequest(
    val name: String
)

@Serializable
data class StoreCollectionRequest(
    val type: String,
    val name: String,
    val weighting: Int? = null,
    @SerialName("given_at") val givenAt: String,
    @SerialName("visible_from") val visibleFrom: String,
    @SerialName("interval_id") val intervalId: Int,
    @SerialName("subject_id") val subjectId: Int,
    @SerialName("group_id") val groupId: Int
)

@Serializable
data class StoreFavoriteRequest(
    @SerialName("student_id") val studentId: Int? = null,
    @SerialName("subject_id") val subjectId: Int? = null,
    @SerialName("group_id") val groupId: Int? = null
)

@Serializable
data class StoreFinalCertificateRequest(
    @SerialName("student_id") val studentId: String,
    @SerialName("year_id") val yearId: String,
    @SerialName("certificate_type") val certificateType: String? = null,
    @SerialName("certificate_date") val certificateDate: String? = null,
    val custom: List<String>? = null,
    @SerialName("year_ids") val yearIds: List<String>? = null
)

@Serializable
data class StoreFinalgradeRequest(
    val value: String? = null,
    @SerialName("student_id") val studentId: Int,
    @SerialName("subject_id") val subjectId: Int,
    @SerialName("interval_id") val intervalId: Int,
    @SerialName("calculation_rule") val calculationRule: String? = null,
    @SerialName("calculation_verbal") val calculationVerbal: String? = null,
    @SerialName("calculation_for") val calculationFor: String
)

@Serializable
data class StoreForDayJournalNoteRequest(
    @SerialName("type_id") val typeId: Int,
    val description: String,
    @SerialName("for") val for_: String,
    @SerialName("group_id") val groupId: Int
)

@Serializable
data class StoreForLessonJournalNoteRequest(
    @SerialName("type_id") val typeId: Int,
    @SerialName("for") val for_: String,
    val description: String
)

@Serializable
data class StoreForLessonStudentJournalNoteRequest(
    @SerialName("type_id") val typeId: Int,
    @SerialName("for") val for_: String,
    val description: String
)

@Serializable
data class StoreForWeekJournalNoteRequest(
    @SerialName("type_id") val typeId: Int,
    val description: String,
    @SerialName("for") val for_: String,
    @SerialName("group_id") val groupId: Int
)

@Serializable
data class StoreGradeRequest(
    val value: String,
    @SerialName("given_at") val givenAt: String? = null,
    @SerialName("student_id") val studentId: Int,
    @SerialName("collection_id") val collectionId: Int
)

@Serializable
data class StoreGroupRequest(
    val name: String,
    val meta: Boolean,
    val custom: String? = null,
    @SerialName("level_id") val levelId: Int? = null,
    @SerialName("local_id") val localId: String,
    @SerialName("year_id") val yearId: String,
    @SerialName("tag_ids") val tagIds: List<Int>? = null
)

@Serializable
data class StoreGuardianRequest(
    val name: String,
    val forename: String,
    @SerialName("local_id") val localId: String? = null,
    val verified: Boolean? = null,
    @SerialName("email_private") val emailPrivate: String? = null,
    @SerialName("email_business") val emailBusiness: String? = null,
    @SerialName("phone_private") val phonePrivate: String? = null,
    @SerialName("phone_business") val phoneBusiness: String? = null,
    @SerialName("student_ids") val studentIds: List<Int>? = null,
    @SerialName("tag_ids") val tagIds: List<Int>? = null
)

@Serializable
data class StoreImporterRequest(
    val active: Boolean,
    @SerialName("secret_student") val secretStudent: String? = null,
    @SerialName("secret_teacher") val secretTeacher: String? = null,
    @SerialName("webhook_secret") val webhookSecret: String? = null
)

@Serializable
data class StoreImporterStundenplan24Request(
    val name: String,
    val version: Int,
    @SerialName("splan_url") val splanUrl: String? = null,
    @SerialName("moble_url") val mobleUrl: String? = null,
    @SerialName("mobil_url") val mobilUrl: String? = null,
    @SerialName("username_teacher") val usernameTeacher: String? = null,
    @SerialName("password_teacher") val passwordTeacher: String? = null,
    @SerialName("username_student") val usernameStudent: String? = null,
    @SerialName("password_student") val passwordStudent: String? = null,
    @SerialName("time_table_meta_id") val timeTableMetaId: Int? = null,
    @SerialName("substitution_plan_meta_id") val substitutionPlanMetaId: Int? = null
)

@Serializable
data class StoreIntervalRequest(
    val name: String,
    val type: String? = null,
    val from: String,
    val to: String,
    @SerialName("editable_to") val editableTo: String,
    @SerialName("included_interval_id") val includedIntervalId: Int? = null,
    @SerialName("year_id") val yearId: Int
)

@Serializable
data class StoreJournalDayStudentRequest(
    val present: Boolean,
    @SerialName("student_id") val studentId: Int,
    @SerialName("absence_id") val absenceId: Int? = null,
    @SerialName("journal_day_id") val journalDayId: String
)

@Serializable
data class StoreJournalLessonRequest(
    val nr: String,
    @SerialName("day_id") val dayId: String? = null,
    @SerialName("journal_day_id") val journalDayId: String? = null,
    val status: String? = null,
    @SerialName("group_id") val groupId: Int,
    @SerialName("subject_id") val subjectId: Int? = null,
    val statuses: List<String>? = null,
    val description: String? = null,
    @SerialName("for") val for_: String? = null,
    @SerialName("type_id") val typeId: Int? = null,
    @SerialName("teacher_ids") val teacherIds: List<Int?>? = null,
    @SerialName("room_ids") val roomIds: List<Int?>? = null,
    val note: StoreJournalLessonNote? = null
)

@Serializable
data class StoreJournalLessonNote(
    val description: String,
    @SerialName("for") val for_: String,
    @SerialName("type_id") val typeId: Int
)

@Serializable
data class StoreJournalLessonStudentRequest(
    val present: Boolean,
    @SerialName("too_early") val tooEarly: Int? = null,
    @SerialName("too_late") val tooLate: Int? = null,
    @SerialName("missing_homework") val missingHomework: Boolean? = null,
    @SerialName("missing_equipment") val missingEquipment: Boolean? = null,
    @SerialName("student_id") val studentId: Int,
    @SerialName("absence_id") val absenceId: Int? = null,
    @SerialName("journal_lesson_id") val journalLessonId: String
)

@Serializable
data class StoreJournalNoteRequest(
    @SerialName("journal_notable_id") val journalNotableId: String,
    @SerialName("journal_notable_type") val journalNotableType: String,
    @SerialName("type_id") val typeId: Int,
    @SerialName("for") val for_: String,
    val description: String
)

@Serializable
data class StoreJournalNoteTypeRequest(
    val name: String,
    val color: String? = null,
    val default: Boolean,
    @SerialName("default_for") val defaultFor: String,
    @SerialName("journal_notable_type") val journalNotableType: String
)

@Serializable
data class StoreLevelRequest(
    val name: String,
    @SerialName("interval_type") val intervalType: String? = null,
    @SerialName("time_type") val timeType: String? = null,
    @SerialName("best_grade") val bestGrade: Int? = null,
    @SerialName("worst_grade") val worstGrade: Int? = null
)

@Serializable
data class StoreNoteRequest(
    val title: String,
    val description: String,
    @SerialName("for") val for_: String,
    @SerialName("notable_type") val notableType: String,
    @SerialName("notable_id") val notableId: String,
    @SerialName("note_type_id") val noteTypeId: Int,
    @SerialName("recorded_at") val recordedAt: String
)

@Serializable
data class StoreNoteTypeRequest(
    val color: String? = null,
    @SerialName("notable_type") val notableType: String,
    val name: String,
    val default: Boolean,
    @SerialName("default_for") val defaultFor: String
)

@Serializable
data class StoreOrUpdateJournalDayRequest(
    @SerialName("time_name") val timeName: String? = null
)

@Serializable
data class StoreSchoolRequest(
    val name: String,
    val email: String,
    val customer: Boolean? = null,
    val type: String? = null,
    @SerialName("postal_second_line") val postalSecondLine: String? = null,
    val street: String,
    @SerialName("street_nr") val streetNr: String,
    @SerialName("postal_code") val postalCode: String,
    val city: String,
    val state: String,
    @SerialName("billing_name") val billingName: String? = null,
    @SerialName("billing_postal_second_line") val billingPostalSecondLine: String? = null,
    @SerialName("billing_street") val billingStreet: String? = null,
    @SerialName("billing_street_nr") val billingStreetNr: String? = null,
    @SerialName("billing_postal_code") val billingPostalCode: String? = null,
    @SerialName("billing_city") val billingCity: String? = null,
    val modules: List<String>,
    val logo: ByteArray? = null,
    @SerialName("admin_name") val adminName: String,
    @SerialName("admin_email") val adminEmail: String,
    @SerialName("headteacher_name") val headteacherName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StoreSchoolRequest

        if (customer != other.customer) return false
        if (name != other.name) return false
        if (email != other.email) return false
        if (type != other.type) return false
        if (postalSecondLine != other.postalSecondLine) return false
        if (street != other.street) return false
        if (streetNr != other.streetNr) return false
        if (postalCode != other.postalCode) return false
        if (city != other.city) return false
        if (state != other.state) return false
        if (billingName != other.billingName) return false
        if (billingPostalSecondLine != other.billingPostalSecondLine) return false
        if (billingStreet != other.billingStreet) return false
        if (billingStreetNr != other.billingStreetNr) return false
        if (billingPostalCode != other.billingPostalCode) return false
        if (billingCity != other.billingCity) return false
        if (modules != other.modules) return false
        if (!logo.contentEquals(other.logo)) return false
        if (adminName != other.adminName) return false
        if (adminEmail != other.adminEmail) return false
        if (headteacherName != other.headteacherName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = customer?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (postalSecondLine?.hashCode() ?: 0)
        result = 31 * result + street.hashCode()
        result = 31 * result + streetNr.hashCode()
        result = 31 * result + postalCode.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + (billingName?.hashCode() ?: 0)
        result = 31 * result + (billingPostalSecondLine?.hashCode() ?: 0)
        result = 31 * result + (billingStreet?.hashCode() ?: 0)
        result = 31 * result + (billingStreetNr?.hashCode() ?: 0)
        result = 31 * result + (billingPostalCode?.hashCode() ?: 0)
        result = 31 * result + (billingCity?.hashCode() ?: 0)
        result = 31 * result + modules.hashCode()
        result = 31 * result + (logo?.contentHashCode() ?: 0)
        result = 31 * result + adminName.hashCode()
        result = 31 * result + adminEmail.hashCode()
        result = 31 * result + headteacherName.hashCode()
        return result
    }
}

@Serializable
data class StoreStudentRequest(
    val name: String,
    val forename: String? = null,
    val nickname: String? = null,
    val gender: String? = null,
    val birthday: String? = null,
    @SerialName("is_adult") val isAdult: Boolean? = null,
    val phone: String? = null,
    @SerialName("local_id") val localId: String,
    @SerialName("tag_ids") val tagIds: List<Int>? = null
)

@Serializable
data class StoreSubjectRequest(
    val name: String,
    @SerialName("local_id") val localId: String,
    @SerialName("for") val for_: String,
    @SerialName("tag_ids") val tagIds: List<Int>? = null
)

@Serializable
data class StoreSubstitutionPlanRequest(
    val name: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    @SerialName("importer_stundenplan24_id") val importerStundenplan24Id: Int? = null
)

@Serializable
data class StoreTagRequest(
    @SerialName("local_id") val localId: String,
    val name: String,
    val hide: Boolean,
    @SerialName("for") val for_: String,
    val taggable: String
)

@Serializable
data class StoreTeacherRequest(
    val name: String,
    val forename: String,
    val role: String? = null,
    @SerialName("local_id") val localId: String,
    @SerialName("tag_ids") val tagIds: List<Int>? = null
)

@Serializable
data class StoreTimeTableRequest(
    val name: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    @SerialName("weeks") val weeks: String? = null,
    @SerialName("no_school_dates") val noSchoolDates: String? = null 
)

@Serializable
data class StoreTimeTableTimeLessonRequest(
    val nr: String,
    val from: String,
    val to: String,
    @SerialName("time_table_time_id") val timeTableTimeId: Int
)

@Serializable
data class StoreTimeTableTimeRequest(
    val name: String,
    val type: String? = null,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    val default: Boolean
)

@Serializable
data class StoreUpdateIntervalStudentRequest(
    @SerialName("certificate_type") val certificateType: String? = null,
    @SerialName("certificate_date") val certificateDate: String? = null,
    val custom: String? = null,
    @SerialName("not_present_without_absence_count") val notPresentWithoutAbsenceCount: String? = null,
    @SerialName("not_present_with_absence_count") val notPresentWithAbsenceCount: String? = null
)

@Serializable
data class StoreYearRequest(
    val name: String,
    val from: String,
    val to: String
)

@Serializable
data class Student(
    val id: Int,
    @SerialName("local_id") val localId: String? = null,
    val forename: String? = null,
    val nickname: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val birthday: String? = null,
    @SerialName("is_adult") val isAdult: Int,
    val phone: String? = null,
    val token: String? = null,
    @SerialName("token_valid_until") val tokenValidUntil: String? = null,
    @SerialName("token_guardian") val tokenGuardian: String? = null,
    @SerialName("token_guardian_valid_until") val tokenGuardianValidUntil: String? = null,
    val deleted: Boolean? = null,
    val groups: List<Group>? = null,
    @SerialName("meta_groups") val metaGroups: List<Group>? = null,
    val subjects: List<Subject>? = null,
    val intervals: List<Interval>? = null,
    val grades: List<Grade>? = null,
    val collections: List<GradeCollection>? = null,
    val finalgrades: List<Finalgrade>? = null,
    @SerialName("final_certificates") val finalCertificates: List<FinalCertificate>? = null,
    @SerialName("certificate_grades") val certificateGrades: List<CertificateGrade>? = null,
    val users: List<User>? = null,
    @SerialName("users_count") val usersCount: String? = null,
    val guardians: List<Guardian>? = null,
    @SerialName("guardians_count") val guardiansCount: Int? = null,
    val tags: List<Tag>? = null
)

@Serializable
data class Subject(
    val id: Int,
    @SerialName("local_id") val localId: String,
    val name: String,
    @SerialName("for") val for_: String,
    val collections: List<GradeCollection>? = null,
    val groups: List<GradeCollection>? = null,
    val finalgrades: List<Finalgrade>? = null,
    val tags: List<Tag>? = null,
    val teachers: List<Teacher>? = null,
    @SerialName("collection_type_order") val collectionTypeOrder: String? = null,
    @SerialName("sort_by_main_group") val sortByMainGroup: String? = null,
    @SerialName("sort_by_gender") val sortByGender: String? = null,
    @SerialName("collection_label") val collectionLabel: String? = null,
    @SerialName("group_id") val groupId: String? = null
)

@Serializable
data class SubstitutionPlan(
    val id: String,
    val name: String,
    @SerialName("valid_from") val validFrom: String? = null,
    @SerialName("valid_to") val validTo: String? = null,
    val stundenplan24: String? = null,
    val days: List<SubstitutionPlanDay>? = null
)

@Serializable
data class SubstitutionPlanDay(
    val id: Int,
    val date: String,
    val notes: String,
    val lessons: List<SubstitutionPlanLesson>? = null
)

@Serializable
data class SubstitutionPlanLesson(
    val id: Int,
    val nr: String,
    val status: String,
    val notes: List<JsonObject>? = null,
    val day: SubstitutionPlanDay? = null,
    val subject: Subject? = null,
    val group: Group? = null,
    val teachers: List<Teacher>? = null,
    val rooms: List<Room>? = null
)

@Serializable
data class Tag(
    val id: Int,
    @SerialName("local_id") val localId: String,
    val name: String,
    val hide: Int,
    @SerialName("for") val for_: String,
    val taggable: String
)

@Serializable
data class Teacher(
    val id: Int,
    @SerialName("local_id") val localId: String? = null,
    val forename: String? = null,
    val name: String? = null,
    val role: String? = null,
    val token: String? = null,
    @SerialName("token_valid_until") val tokenValidUntil: String? = null,
    val deleted: Boolean? = null,
    val grades: List<Grade>? = null,
    val collections: List<GradeCollection>? = null,
    val subjects: List<Subject>? = null,
    val tags: List<Tag>? = null,
    val valid: String? = null,
    val notes: String? = null
)

@Serializable
data class TimeTable(
    val id: String,
    val name: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    val stundenplan24: String? = null,
    val weeks: String,
    @SerialName("no_school_dates") val noSchoolDates: String,
    val lessons: List<TimeTableLesson>? = null
)

@Serializable
data class TimeTableLesson(
    val id: Int,
    val weeks: List<JsonObject>,
    val weekday: Int,
    val nr: String,
    val subject: Subject? = null,
    val group: Group? = null,
    val teachers: List<Teacher>? = null,
    val rooms: List<Room>? = null
)

@Serializable
data class TimeTableTime(
    val id: Int,
    val name: String,
    val type: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    val default: Int,
    val lessons: List<TimeTableTimeLesson>
)

@Serializable
data class TimeTableTimeLesson(
    val id: String,
    val nr: String,
    val to: String? = null,
    val from: String? = null
)

@Serializable
data class UpdateAbsenceBatchRequest(
    val from: String,
    val to: String,
    @SerialName("type_id") val typeId: Int,
    @SerialName("student_ids") val studentIds: List<String>? = null,
    @SerialName("note_teacher") val noteTeacher: String? = null,
    @SerialName("lesson_ids") val lessonIds: List<String>? = null,
    @SerialName("subject_ids") val subjectIds: List<String>? = null,
    val verification: UpdateAbsenceVerificationRequest? = null
)

@Serializable
data class UpdateAbsenceTypeRequest(
    val name: String,
    val default: Boolean,
    @SerialName("editable_as") val editableAs: String? = null,
    @SerialName("default_present") val defaultPresent: Boolean? = null
)

@Serializable
data class UpdateAbsenceVerificationRequest(
    val id: Double? = null,
    val confirmed: Boolean? = null,
    @SerialName("local_id") val localId: String? = null,
    val note: String? = null,
    @SerialName("recorded_at") val recordedAt: String? = null
)

@Serializable
data class UpdateAnnouncementRequest(
    val title: String,
    val message: String? = null,
    val from: String,
    val to: String,
    @SerialName("for") val for_: String,
    @SerialName("need_confirmation_from_student") val needConfirmationFromStudent: Boolean? = null,
    @SerialName("need_confirmation_from_guardian") val needConfirmationFromGuardian: Boolean? = null,
    @SerialName("type_id") val typeId: Int? = null,
    @SerialName("group_ids") val groupIds: List<Int>? = null
)

@Serializable
data class UpdateAnnouncementTypeRequest(
    val name: String,
    val color: String? = null,
    val default: Boolean,
    @SerialName("default_for") val defaultFor: String
)

@Serializable
data class UpdateCertificateGradeRequest(
    val value: String? = null,
    val name: String? = null,
    @SerialName("given_at") val givenAt: String? = null,
    val type: String? = null
)

@Serializable
data class UpdateChecklistRequest(
    val name: String,
    val date: String,
    val description: String? = null,
    @SerialName("for") val for_: String,
    @SerialName("type_id") val typeId: Int? = null,
    @SerialName("group_ids") val groupIds: List<Int?>? = null,
    val checks: UpdateChecklistCheck? = null
)

@Serializable
data class UpdateChecklistCheck(
    val checked: Boolean,
    @SerialName("checked_at") val checkedAt: String? = null,
    val note: String? = null
)

@Serializable
data class UpdateChecklistTypeRequest(
    val name: String
)

@Serializable
data class UpdateCollectionRequest(
    val type: String,
    val name: String,
    val weighting: Int? = null,
    @SerialName("given_at") val givenAt: String,
    @SerialName("visible_from") val visibleFrom: String,
    @SerialName("interval_id") val intervalId: Int
)

@Serializable
data class UpdateFavoriteRequest(
    @SerialName("student_id") val studentId: Int? = null,
    @SerialName("subject_id") val subjectId: Int? = null,
    @SerialName("group_id") val groupId: Int? = null
)

@Serializable
data class UpdateFinalCertificateRequest(
    @SerialName("certificate_type") val certificateType: String? = null,
    @SerialName("certificate_date") val certificateDate: String? = null,
    val custom: List<String>? = null,
    @SerialName("year_ids") val yearIds: List<String>? = null
)

@Serializable
data class UpdateFinalgradeRequest(
    val value: String? = null,
    @SerialName("calculation_rule") val calculationRule: String? = null,
    @SerialName("calculation_verbal") val calculationVerbal: String? = null,
    @SerialName("calculation_for") val calculationFor: String? = null
)

@Serializable
data class UpdateGradeRequest(
    val value: String
)

@Serializable
data class UpdateGuardianRequest(
    val name: String? = null,
    val forename: String? = null,
    @SerialName("local_id") val localId: String? = null,
    val verified: Boolean? = null,
    @SerialName("email_private") val emailPrivate: String? = null,
    @SerialName("email_business") val emailBusiness: String? = null,
    @SerialName("phone_private") val phonePrivate: String? = null,
    @SerialName("phone_business") val phoneBusiness: String? = null,
    val deleted: Boolean? = null,
    @SerialName("student_ids") val studentIds: List<Int>? = null,
    @SerialName("tag_ids") val tagIds: List<Int>? = null
)

@Serializable
data class UpdateImporterRequest(
    val active: Boolean,
    @SerialName("secret_student") val secretStudent: String? = null,
    @SerialName("secret_teacher") val secretTeacher: String? = null,
    @SerialName("webhook_secret") val webhookSecret: String? = null
)

@Serializable
data class UpdateImporterStundenplan24Request(
    val name: String,
    val version: Int,
    @SerialName("splan_url") val splanUrl: String? = null,
    @SerialName("moble_url") val mobleUrl: String? = null,
    @SerialName("mobil_url") val mobilUrl: String? = null,
    @SerialName("username_teacher") val usernameTeacher: String? = null,
    @SerialName("password_teacher") val passwordTeacher: String? = null,
    @SerialName("username_student") val usernameStudent: String? = null,
    @SerialName("password_student") val passwordStudent: String? = null,
    @SerialName("time_table_meta_id") val timeTableMetaId: Int? = null,
    @SerialName("substitution_plan_meta_id") val substitutionPlanMetaId: Int? = null
)

@Serializable
data class UpdateIntervalRequest(
    val name: String,
    val type: String? = null,
    val from: String,
    val to: String,
    @SerialName("editable_to") val editableTo: String,
    @SerialName("included_interval_id") val includedIntervalId: Int? = null
)

@Serializable
data class UpdateJournalLessonRequest(
    val status: String? = null,
    @SerialName("subject_id") val subjectId: Int? = null,
    val statuses: List<String>? = null,
    @SerialName("teacher_ids") val teacherIds: List<Int?>? = null,
    @SerialName("room_ids") val roomIds: List<Int?>? = null
)

@Serializable
data class UpdateJournalLessonStudentRequest(
    val present: Boolean? = null,
    @SerialName("too_early") val tooEarly: Int? = null,
    @SerialName("too_late") val tooLate: Int? = null,
    @SerialName("missing_homework") val missingHomework: Boolean? = null,
    @SerialName("missing_equipment") val missingEquipment: Boolean? = null,
    @SerialName("absence_id") val absenceId: Int? = null
)

@Serializable
data class UpdateJournalNoteRequest(
    @SerialName("type_id") val typeId: Int,
    @SerialName("for") val for_: String,
    val description: String
)

@Serializable
data class UpdateJournalNoteTypeRequest(
    val name: String,
    val color: String? = null,
    val default: Boolean,
    @SerialName("default_for") val defaultFor: String
)

@Serializable
data class UpdateLevelRequest(
    val name: String,
    @SerialName("interval_type") val intervalType: String? = null,
    @SerialName("time_type") val timeType: String? = null,
    @SerialName("best_grade") val bestGrade: Int? = null,
    @SerialName("worst_grade") val worstGrade: Int? = null
)

@Serializable
data class UpdateNoteRequest(
    val title: String? = null,
    val description: String? = null,
    @SerialName("for") val for_: String? = null,
    @SerialName("note_type_id") val noteTypeId: Int? = null,
    @SerialName("recorded_at") val recordedAt: String? = null
)

@Serializable
data class UpdateNoteTypeRequest(
    val color: String? = null,
    val name: String? = null,
    @SerialName("notable_type") val notableType: String? = null,
    val default: Boolean? = null,
    @SerialName("default_for") val defaultFor: String? = null
)

@Serializable
data class UpdateSchoolRequest(
    val name: String,
    val email: String,
    val customer: Boolean? = null,
    val type: String? = null,
    @SerialName("postal_second_line") val postalSecondLine: String? = null,
    val street: String,
    @SerialName("street_nr") val streetNr: String,
    @SerialName("postal_code") val postalCode: String,
    val city: String,
    val state: String,
    @SerialName("billing_name") val billingName: String? = null,
    @SerialName("billing_postal_second_line") val billingPostalSecondLine: String? = null,
    @SerialName("billing_street") val billingStreet: String? = null,
    @SerialName("billing_street_nr") val billingStreetNr: String? = null,
    @SerialName("billing_postal_code") val billingPostalCode: String? = null,
    @SerialName("billing_city") val billingCity: String? = null,
    val modules: List<String>? = null,
    val logo: ByteArray? = null,
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("admin_name") val adminName: String,
    @SerialName("admin_email") val adminEmail: String,
    @SerialName("headteacher_name") val headteacherName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UpdateSchoolRequest

        if (customer != other.customer) return false
        if (name != other.name) return false
        if (email != other.email) return false
        if (type != other.type) return false
        if (postalSecondLine != other.postalSecondLine) return false
        if (street != other.street) return false
        if (streetNr != other.streetNr) return false
        if (postalCode != other.postalCode) return false
        if (city != other.city) return false
        if (state != other.state) return false
        if (billingName != other.billingName) return false
        if (billingPostalSecondLine != other.billingPostalSecondLine) return false
        if (billingStreet != other.billingStreet) return false
        if (billingStreetNr != other.billingStreetNr) return false
        if (billingPostalCode != other.billingPostalCode) return false
        if (billingCity != other.billingCity) return false
        if (modules != other.modules) return false
        if (!logo.contentEquals(other.logo)) return false
        if (logoUrl != other.logoUrl) return false
        if (adminName != other.adminName) return false
        if (adminEmail != other.adminEmail) return false
        if (headteacherName != other.headteacherName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = customer?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (postalSecondLine?.hashCode() ?: 0)
        result = 31 * result + street.hashCode()
        result = 31 * result + streetNr.hashCode()
        result = 31 * result + postalCode.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + (billingName?.hashCode() ?: 0)
        result = 31 * result + (billingPostalSecondLine?.hashCode() ?: 0)
        result = 31 * result + (billingStreet?.hashCode() ?: 0)
        result = 31 * result + (billingStreetNr?.hashCode() ?: 0)
        result = 31 * result + (billingPostalCode?.hashCode() ?: 0)
        result = 31 * result + (billingCity?.hashCode() ?: 0)
        result = 31 * result + (modules?.hashCode() ?: 0)
        result = 31 * result + (logo?.contentHashCode() ?: 0)
        result = 31 * result + (logoUrl?.hashCode() ?: 0)
        result = 31 * result + adminName.hashCode()
        result = 31 * result + adminEmail.hashCode()
        result = 31 * result + headteacherName.hashCode()
        return result
    }
}

@Serializable
data class UpdateSubstitutionPlanRequest(
    val name: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String
)

@Serializable
data class UpdateTagRequest(
    @SerialName("local_id") val localId: String,
    val name: String,
    val hide: Boolean,
    @SerialName("for") val for_: String
)

@Serializable
data class UpdateTeacherRequest(
    val name: String,
    val forename: String,
    val role: String? = null,
    val deleted: Boolean? = null,
    @SerialName("local_id") val localId: String,
    @SerialName("tag_ids") val tagIds: List<Int>? = null
)

@Serializable
data class UpdateTimeTableRequest(
    val name: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    @SerialName("weeks") val weeks: String? = null,
    @SerialName("no_school_dates") val noSchoolDates: String? = null
)

@Serializable
data class UpdateTimeTableTimeLessonRequest(
    val nr: String,
    val from: String,
    val to: String
)

@Serializable
data class UpdateTimeTableTimeRequest(
    val name: String,
    val type: String? = null,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    val default: Boolean
)

@Serializable
data class UpdateUserRequest(
    val username: String? = null,
    val email: String? = null,
    @SerialName("email_private") val emailPrivate: String? = null,
    @SerialName("email_business") val emailBusiness: String? = null,
    @SerialName("phone_private") val phonePrivate: String? = null,
    @SerialName("phone_business") val phoneBusiness: String? = null
)

@Serializable
data class UpdateYearRequest(
    val name: String,
    val from: String,
    val to: String
)

@Serializable
data class User(
    val id: Int? = null,
    val username: String? = null,
    val email: String? = null,
    @SerialName("email_private") val emailPrivate: String? = null,
    @SerialName("email_business") val emailBusiness: String? = null,
    @SerialName("phone_private") val phonePrivate: String? = null,
    @SerialName("phone_business") val phoneBusiness: String? = null,
    val role: String? = null,
    val school: School? = null,
    val students: List<Student>? = null,
    val teachers: List<Teacher>? = null,
    val guardians: List<Guardian>? = null,
    val firebaseDevices: List<FirebaseDevice>? = null,
    val socialiteProviders: List<UserSocialite>? = null
)

@Serializable
data class UserSocialite(
    val id: Int,
    val provider: String,
    val providerId: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class Year(
    val id: Int,
    val ids: List<String>,
    val name: String,
    val from: String,
    val to: String,
    val intervals: List<Interval>? = null
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
data class DataWrapper<T>(
    val data: T,
    val meta: Meta? = null
)

@Serializable
data class ListDataWrapper<T>(
    val data: List<T>,
    val meta: Meta? = null
)

@Serializable
data class PaginatedDataWrapper<T>(
    val data: List<T>,
    val links: PaginationLinks,
    val meta: PaginationMeta
)

@Serializable
data class PaginationLinks(
    val first: String? = null,
    val last: String? = null,
    val prev: String? = null,
    val next: String? = null
)

@Serializable
data class PaginationMeta(
    @SerialName("current_page") val currentPage: Int,
    val from: Int? = null,
    @SerialName("last_page") val lastPage: Int,
    val links: List<PaginationMetaLink>,
    val path: String? = null,
    @SerialName("per_page") val perPage: Int,
    val to: Int? = null,
    val total: Int
)

@Serializable
data class PaginationMetaLink(
    val url: String? = null,
    val label: String,
    val active: Boolean
)

@Serializable
data class SimpleSuccessResponse(
    val success: String
)

@Serializable
data class SimplePasswordResponse(
    val password: String
)

@Serializable
data class SimpleResultResponse(
    val result: String
)

@Serializable
data class SimpleMessageResponse(
    val message: String
)

@Serializable
data class SimpleVerifiedResponse(
    val verified: Boolean
)

@Serializable
data class GroupSubjectOrderResponse(
    @SerialName("collection_type_order") val collectionTypeOrder: String,
    @SerialName("sort_by_main_group") val sortByMainGroup: String,
    @SerialName("sort_by_gender") val sortByGender: String,
    @SerialName("collection_label") val collectionLabel: String
)

class BesteSchuleApi(httpClient: HttpClient, authToken: String) {
    private val baseUrl = "https://beste.schule/api"

    private val client = httpClient.config {
        defaultRequest {
            bearerAuth(authToken)
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

    suspend fun journalWeekShow(nr: String, include: String? = null): DataWrapper<JournalWeek> {
        return client.get("$baseUrl/journal/weeks/$nr") {
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

@Serializable
data class SiteStatusResponse(
    @SerialName("message_text") val messageText: String,
    @SerialName("message_url") val messageUrl: String
)

@Serializable
data class SimpleSecretResponse(
    val secret: String
)

@Serializable
data class VerifyTwoFactorUserRequest(
    @SerialName("one_time_password") val oneTimePassword: String? = null
)

@Serializable
data class AddMembershipUserRequest(
    val token: String
)