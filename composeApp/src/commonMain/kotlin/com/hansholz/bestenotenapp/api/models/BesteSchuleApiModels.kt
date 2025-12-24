@file:Suppress("unused")

package com.hansholz.bestenotenapp.api.models

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
    val histories: List<History>? = null,
    @SerialName("lesson_students") val lessonStudents: List<JournalLessonStudent>? = null,
    @SerialName("lesson_students_count") val lessonStudentsCount: String? = null,
    val lessons: List<JournalLessonStudent>? = null,
    @SerialName("lessons_count") val lessonsCount: String? = null,
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
    @SerialName("lessons_count") val lessonsCount: String? = null,
)

@Serializable
data class AbsenceType(
    val id: Int,
    val name: String,
    val default: Int,
    @SerialName("editable_as") val editableAs: String,
    @SerialName("default_present") val defaultPresent: Int? = null,
    val absences: List<Absence>? = null,
)

@Serializable
data class AbsenceVerification(
    val id: String? = null,
    val confirmed: Boolean? = null,
    @SerialName("local_id") val localId: String? = null,
    val note: String? = null,
    @SerialName("recorded_at") val recordedAt: String? = null,
    val teacher: Teacher? = null,
    val absence: Absence? = null,
)

@Serializable
data class AddFirebaseDeviceUserRequest(
    val token: String,
    val name: String? = null,
    val language: String? = null,
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
    val teacher: Teacher? = null,
)

@Serializable
data class AnnouncementType(
    val id: Int,
    val name: String,
    val color: String? = null,
    val default: Int,
    @SerialName("default_for") val defaultFor: String,
    val announcements: List<Announcement>? = null,
)

@Serializable
data class ArrayCollection(
    val data: JsonArray,
    val meta: Meta,
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
    @SerialName("import_action") val importAction: String,
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
    @SerialName("year_id") val yearId: String,
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
    val students: List<String>? = null,
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
    val importAction: String,
)

@Serializable
data class BatchSubjectRequest(
    val id: Int? = null,
    val name: String,
    @SerialName("local_id") val localId: String,
    val tags: List<String>? = null,
    val importAction: String,
)

@Serializable
data class BatchTeacherRequest(
    val id: Int? = null,
    val name: String? = null,
    val forename: String? = null,
    @SerialName("local_id") val localId: String,
    val tags: List<String>? = null,
    val importAction: String,
)

@Serializable
data class BatchTokenGuardianRequest(
    val ids: List<String>,
    val mode: String? = null,
)

@Serializable
data class BatchTokenGuardianStudentRequest(
    val ids: List<String>,
    val mode: String? = null,
)

@Serializable
data class BatchTokenStudentRequest(
    val ids: List<String>,
    val mode: String? = null,
)

@Serializable
data class BatchTokenTeacherRequest(
    val ids: List<String>,
    val mode: String? = null,
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
    val student: Student? = null,
)

@Serializable
data class ChangeSchoolUserRequest(
    val id: Int,
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
    val teacher: Teacher? = null,
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
    val tags: List<Tag>? = null,
)

@Serializable
data class ChecklistType(
    val id: Int,
    val name: String,
    val checklists: List<Checklist>? = null,
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
    val histories: List<History>? = null,
)

@Serializable
data class ExecuteNotificationActionRequest(
    val action: String,
)

@Serializable
data class Favorite(
    val id: Int,
    val student: Student,
    val group: Group,
    val subject: Subject,
)

@Serializable
data class FinalCertificate(
    val id: Int,
    @SerialName("certificate_type") val certificateType: String? = null,
    @SerialName("certificate_date") val certificateDate: String,
    val custom: String? = null,
    val student: Student? = null,
    val year: Year? = null,
    val years: List<Year>? = null,
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
    val histories: List<History>? = null,
)

@Serializable
data class FirebaseDevice(
    val id: Int,
    val name: String,
    val language: String,
    @SerialName("user_id") val userId: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
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
    val histories: List<History>? = null,
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
    val tags: List<Tag>? = null,
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
    val user: User? = null,
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
    @SerialName("local_id") val localId: String? = null,
    val forename: String?,
    val name: String?,
    val tags: List<String>? = emptyList(),
)

@Serializable
data class Importer(
    val id: Int,
    val active: Int,
    @SerialName("secret_student") val secretStudent: String? = null,
    @SerialName("secret_teacher") val secretTeacher: String? = null,
    @SerialName("webhook_secret") val webhookSecret: String? = null,
    val logs: List<ImporterLog>? = null,
)

@Serializable
data class ImporterLog(
    val type: String,
    val message: String,
    @SerialName("created_at") val createdAt: String,
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
    val logs: List<ImporterLog>? = null,
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
    @SerialName("not_present_with_absence_count") val notPresentWithAbsenceCount: String? = null,
)

@Serializable
data class JournalDay(
    val id: String,
    val date: String,
    @SerialName("time_name") val timeName: String? = null,
    val lessons: List<JournalLesson>? = null,
    val notes: List<JournalNote>? = null,
    val week: JournalWeek? = null,
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
    val day: JournalDay? = null,
)

@Serializable
data class JournalDayStudentCount(
    val count: Int? = null,
    @SerialName("lessons_count") val lessonsCount: String? = null,
    @SerialName("not_present_count") val notPresentCount: Int? = null,
    @SerialName("not_present_with_absence_count") val notPresentWithAbsenceCount: Int? = null,
    @SerialName("not_present_without_absence_count") val notPresentWithoutAbsenceCount: Int? = null,
    @SerialName("not_present_mixed_absence_count") val notPresentMixedAbsenceCount: Int? = null,
    @SerialName("lessons_not_present_count") val lessonsNotPresentCount: String? = null,
    @SerialName("lessons_not_present_with_absence_count") val lessonsNotPresentWithAbsenceCount: String? = null,
    @SerialName("lessons_not_present_without_absence_count") val lessonsNotPresentWithoutAbsenceCount: String? = null,
    @SerialName("lessons_mixed_not_present_with_absence_count") val lessonsMixedNotPresentWithAbsenceCount: String? = null,
    @SerialName("lessons_mixed_not_present_without_absence_count") val lessonsMixedNotPresentWithoutAbsenceCount: String? = null,
    @SerialName("too_late_with_absence_count") val tooLateWithAbsenceCount: String? = null,
    @SerialName("too_late_without_absence_count") val tooLateWithoutAbsenceCount: String? = null,
    @SerialName("too_early_with_absence_count") val tooEarlyWithAbsenceCount: String? = null,
    @SerialName("too_early_without_absence_count") val tooEarlyWithoutAbsenceCount: String? = null,
    @SerialName("student_id") val studentId: Int? = null,
    @SerialName("absence_mixed") val absenceMixed: String? = null,
    val student: Student? = null,
)

@Serializable
data class JournalLesson(
    val id: String? = null,
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
    val owner: Teacher? = null,
)

@Serializable
data class JournalLessonStudent(
    val id: String? = null,
    val student: Student? = null,
    val ids: String? = null,
    val present: Int? = null,
    @SerialName("too_early") val tooEarly: Int? = null,
    @SerialName("too_late") val tooLate: Int? = null,
    @SerialName("missing_homework") val missingHomework: Int? = null,
    @SerialName("missing_equipment") val missingEquipment: Int? = null,
    val absence: Absence? = null,
    val teacher: Teacher? = null,
    val lesson: JournalLesson? = null,
    @SerialName("journal_lesson_ids") val journalLessonIds: String? = null,
    val notes: List<JournalNote>? = null,
)

@Serializable
data class JournalLessonStudentCount(
    val count: Int? = null,
    @SerialName("not_present_count") val notPresentCount: Int? = null,
    @SerialName("not_present_with_absence_count") val notPresentWithAbsenceCount: Int? = null,
    @SerialName("too_late_sum") val tooLateSum: Int? = null,
    @SerialName("too_late_with_absence_sum") val tooLateWithAbsenceSum: Int? = null,
    @SerialName("too_early_sum") val tooEarlySum: Int? = null,
    @SerialName("too_early_with_absence_sum") val tooEarlyWithAbsenceSum: Int? = null,
    @SerialName("missing_equipment_sum") val missingEquipmentSum: Int? = null,
    @SerialName("missing_homework_sum") val missingHomeworkSum: Int? = null,
    @SerialName("student_id") val studentId: Int? = null,
    val student: Student? = null,
)

@Serializable
data class JournalNote(
    val id: String? = null,
    val ids: String? = null,
    val description: String? = null,
    @SerialName("for") val for_: String? = null,
    val type: JournalNoteType? = null,
    val group: Group? = null,
    val source: String? = null,
    val notable: JournalLessonStudent? = null,
    val teacher: Teacher? = null,
    @SerialName("notable_type") val notableType: String? = null,
)

@Serializable
data class JournalNoteType(
    val id: Int? = null,
    val name: String,
    val color: String? = null,
    val default: Int? = null,
    @SerialName("default_for") val defaultFor: String? = null,
    @SerialName("journal_notable_type") val journalNotableType: String? = null,
)

@Serializable
data class JournalWeek(
    val id: String,
    @SerialName("calendar_year") val calendarYear: Int,
    val nr: Int,
    @SerialName("year_id") val yearId: String? = null,
    val days: List<JournalDay>? = null,
    val notes: List<JournalNote>? = null,
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
    val times: List<TimeTableTime>? = null,
)

@Serializable
data class MarkReadAnnouncementRequest(
    @SerialName("guardian_id") val guardianId: Int? = null,
    @SerialName("student_id") val studentId: Int? = null,
    @SerialName("read_at") val readAt: String? = null,
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
    val group: Group? = null,
)

@Serializable
data class NoteType(
    val id: Int,
    val color: String? = null,
    val name: String,
    @SerialName("notable_type") val notableType: String,
    val default: Int,
    @SerialName("default_for") val defaultFor: String,
)

@Serializable
data class Notification(
    val id: String,
    @SerialName("notification_type") val notificationType: String,
    @SerialName("available_actions") val availableActions: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("read_at") val readAt: String,
    @SerialName("notifiable_type") val notifiableType: String,
    val data: String,
)

@Serializable
data class PostPasswordUserRequest(
    @SerialName("old_password") val oldPassword: String,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String,
)

@Serializable
data class PushImporterRequest(
    val data: List<String>,
    val meta: PushImporterMeta,
)

@Serializable
data class PushImporterMeta(
    val version: String,
    val type: String,
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
    @SerialName("teacher_id") val teacherId: Int? = null,
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
    @SerialName("hasUnverifiedAbsence") val hasUnverifiedAbsence: String? = null,
)

@Serializable
data class ReportDataProcessing(
    val message: String,
    val hash: String,
)

@Serializable
data class ReportMetaSuccess(
    val status: String,
    @SerialName("generated_at") val generatedAt: String,
)

@Serializable
data class ReportMetaProcessing(
    val status: String,
    @SerialName("generated_at") val generatedAt: String? = null,
)

@Serializable
sealed interface ReportResult {
    @Serializable
    data class Success(
        val data: List<String>,
        val meta: ReportMetaSuccess,
    ) : ReportResult

    @Serializable
    data class Processing(
        val data: ReportDataProcessing,
        val meta: ReportMetaProcessing,
    ) : ReportResult

    @Serializable
    data class Error(
        val message: String,
    ) : ReportResult
}

@Serializable
data class Room(
    val id: Int,
    @SerialName("local_id") val localId: String,
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
    val seats: List<SeatingPlanStudent>? = null,
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
    val tags: List<Tag>? = null,
)

@Serializable
data class SetCurrentYearRequest(
    val id: Int? = null,
)

@Serializable
data class SetSubjectCalculationStudentRequest(
    val calculation: String,
    val grade: String? = null,
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
    val verification: StoreAbsenceVerificationRequest? = null,
)

@Serializable
data class StoreAbsenceTypeRequest(
    val name: String,
    val default: Boolean,
    @SerialName("editable_as") val editableAs: String? = null,
    @SerialName("default_present") val defaultPresent: Boolean? = null,
)

@Serializable
data class StoreAbsenceVerificationRequest(
    val confirmed: Boolean? = null,
    @SerialName("local_id") val localId: String? = null,
    val note: String? = null,
    @SerialName("recorded_at") val recordedAt: String? = null,
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
    @SerialName("group_ids") val groupIds: List<Int>,
)

@Serializable
data class StoreAnnouncementTypeRequest(
    val name: String,
    val color: String? = null,
    val default: Boolean,
    @SerialName("default_for") val defaultFor: String,
)

@Serializable
data class StoreCertificateGradeRequest(
    val value: String,
    val type: String,
    val name: String? = null,
    @SerialName("given_at") val givenAt: String? = null,
    @SerialName("student_id") val studentId: Int,
    @SerialName("subject_id") val subjectId: String,
    @SerialName("year_id") val yearId: String,
)

@Serializable
data class StoreChecklistRequest(
    val name: String,
    val date: String,
    val description: String? = null,
    @SerialName("for") val for_: String,
    @SerialName("type_id") val typeId: Int? = null,
    @SerialName("group_ids") val groupIds: List<Int>,
    val checks: StoreChecklistCheck? = null,
)

@Serializable
data class StoreChecklistCheck(
    val checked: Boolean,
    @SerialName("checked_at") val checkedAt: String? = null,
    val note: String? = null,
)

@Serializable
data class StoreChecklistTypeRequest(
    val name: String,
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
    @SerialName("group_id") val groupId: Int,
)

@Serializable
data class StoreFavoriteRequest(
    @SerialName("student_id") val studentId: Int? = null,
    @SerialName("subject_id") val subjectId: Int? = null,
    @SerialName("group_id") val groupId: Int? = null,
)

@Serializable
data class StoreFinalCertificateRequest(
    @SerialName("student_id") val studentId: String,
    @SerialName("year_id") val yearId: String,
    @SerialName("certificate_type") val certificateType: String? = null,
    @SerialName("certificate_date") val certificateDate: String? = null,
    val custom: List<String>? = null,
    @SerialName("year_ids") val yearIds: List<String>? = null,
)

@Serializable
data class StoreFinalgradeRequest(
    val value: String? = null,
    @SerialName("student_id") val studentId: Int,
    @SerialName("subject_id") val subjectId: Int,
    @SerialName("interval_id") val intervalId: Int,
    @SerialName("calculation_rule") val calculationRule: String? = null,
    @SerialName("calculation_verbal") val calculationVerbal: String? = null,
    @SerialName("calculation_for") val calculationFor: String,
)

@Serializable
data class StoreForDayJournalNoteRequest(
    @SerialName("type_id") val typeId: Int,
    val description: String,
    @SerialName("for") val for_: String,
    @SerialName("group_id") val groupId: Int,
)

@Serializable
data class StoreForLessonJournalNoteRequest(
    @SerialName("type_id") val typeId: Int,
    @SerialName("for") val for_: String,
    val description: String,
)

@Serializable
data class StoreForLessonStudentJournalNoteRequest(
    @SerialName("type_id") val typeId: Int,
    @SerialName("for") val for_: String,
    val description: String,
)

@Serializable
data class StoreForWeekJournalNoteRequest(
    @SerialName("type_id") val typeId: Int,
    val description: String,
    @SerialName("for") val for_: String,
    @SerialName("group_id") val groupId: Int,
)

@Serializable
data class StoreGradeRequest(
    val value: String,
    @SerialName("given_at") val givenAt: String? = null,
    @SerialName("student_id") val studentId: Int,
    @SerialName("collection_id") val collectionId: Int,
)

@Serializable
data class StoreGroupRequest(
    val name: String,
    val meta: Boolean,
    val custom: String? = null,
    @SerialName("level_id") val levelId: Int? = null,
    @SerialName("local_id") val localId: String,
    @SerialName("year_id") val yearId: String,
    @SerialName("tag_ids") val tagIds: List<Int>? = null,
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
    @SerialName("tag_ids") val tagIds: List<Int>? = null,
)

@Serializable
data class StoreImporterRequest(
    val active: Boolean,
    @SerialName("secret_student") val secretStudent: String? = null,
    @SerialName("secret_teacher") val secretTeacher: String? = null,
    @SerialName("webhook_secret") val webhookSecret: String? = null,
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
    @SerialName("substitution_plan_meta_id") val substitutionPlanMetaId: Int? = null,
)

@Serializable
data class StoreIntervalRequest(
    val name: String,
    val type: String? = null,
    val from: String,
    val to: String,
    @SerialName("editable_to") val editableTo: String,
    @SerialName("included_interval_id") val includedIntervalId: Int? = null,
    @SerialName("year_id") val yearId: Int,
)

@Serializable
data class StoreJournalDayStudentRequest(
    val present: Boolean,
    @SerialName("student_id") val studentId: Int,
    @SerialName("absence_id") val absenceId: Int? = null,
    @SerialName("journal_day_id") val journalDayId: String,
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
    val note: StoreJournalLessonNote? = null,
)

@Serializable
data class StoreJournalLessonNote(
    val description: String,
    @SerialName("for") val for_: String,
    @SerialName("type_id") val typeId: Int,
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
    @SerialName("journal_lesson_id") val journalLessonId: String,
)

@Serializable
data class StoreJournalNoteRequest(
    @SerialName("journal_notable_id") val journalNotableId: String,
    @SerialName("journal_notable_type") val journalNotableType: String,
    @SerialName("type_id") val typeId: Int,
    @SerialName("for") val for_: String,
    val description: String,
)

@Serializable
data class StoreJournalNoteTypeRequest(
    val name: String,
    val color: String? = null,
    val default: Boolean,
    @SerialName("default_for") val defaultFor: String,
    @SerialName("journal_notable_type") val journalNotableType: String,
)

@Serializable
data class StoreLevelRequest(
    val name: String,
    @SerialName("interval_type") val intervalType: String? = null,
    @SerialName("time_type") val timeType: String? = null,
    @SerialName("best_grade") val bestGrade: Int? = null,
    @SerialName("worst_grade") val worstGrade: Int? = null,
)

@Serializable
data class StoreNoteRequest(
    val title: String,
    val description: String,
    @SerialName("for") val for_: String,
    @SerialName("notable_type") val notableType: String,
    @SerialName("notable_id") val notableId: String,
    @SerialName("note_type_id") val noteTypeId: Int,
    @SerialName("recorded_at") val recordedAt: String,
)

@Serializable
data class StoreNoteTypeRequest(
    val color: String? = null,
    @SerialName("notable_type") val notableType: String,
    val name: String,
    val default: Boolean,
    @SerialName("default_for") val defaultFor: String,
)

@Serializable
data class StoreOrUpdateJournalDayRequest(
    @SerialName("time_name") val timeName: String? = null,
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
    @SerialName("headteacher_name") val headteacherName: String,
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
    @SerialName("tag_ids") val tagIds: List<Int>? = null,
)

@Serializable
data class StoreSubjectRequest(
    val name: String,
    @SerialName("local_id") val localId: String,
    @SerialName("for") val for_: String,
    @SerialName("tag_ids") val tagIds: List<Int>? = null,
)

@Serializable
data class StoreSubstitutionPlanRequest(
    val name: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    @SerialName("importer_stundenplan24_id") val importerStundenplan24Id: Int? = null,
)

@Serializable
data class StoreTagRequest(
    @SerialName("local_id") val localId: String,
    val name: String,
    val hide: Boolean,
    @SerialName("for") val for_: String,
    val taggable: String,
)

@Serializable
data class StoreTeacherRequest(
    val name: String,
    val forename: String,
    val role: String? = null,
    @SerialName("local_id") val localId: String,
    @SerialName("tag_ids") val tagIds: List<Int>? = null,
)

@Serializable
data class StoreTimeTableRequest(
    val name: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    @SerialName("weeks") val weeks: String? = null,
    @SerialName("no_school_dates") val noSchoolDates: String? = null,
)

@Serializable
data class StoreTimeTableTimeLessonRequest(
    val nr: String,
    val from: String,
    val to: String,
    @SerialName("time_table_time_id") val timeTableTimeId: Int,
)

@Serializable
data class StoreTimeTableTimeRequest(
    val name: String,
    val type: String? = null,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    val default: Boolean,
)

@Serializable
data class StoreUpdateIntervalStudentRequest(
    @SerialName("certificate_type") val certificateType: String? = null,
    @SerialName("certificate_date") val certificateDate: String? = null,
    val custom: String? = null,
    @SerialName("not_present_without_absence_count") val notPresentWithoutAbsenceCount: String? = null,
    @SerialName("not_present_with_absence_count") val notPresentWithAbsenceCount: String? = null,
)

@Serializable
data class StoreYearRequest(
    val name: String,
    val from: String,
    val to: String,
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
    @SerialName("is_adult") val isAdult: Int? = null,
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
    val tags: List<Tag>? = null,
)

@Serializable
data class Subject(
    val id: Int? = null,
    @SerialName("local_id") val localId: String? = null,
    val name: String? = null,
    @SerialName("for") val for_: String? = null,
    val collections: List<GradeCollection>? = null,
    val groups: List<GradeCollection>? = null,
    val finalgrades: List<Finalgrade>? = null,
    val tags: List<Tag>? = null,
    val teachers: List<Teacher>? = null,
    @SerialName("collection_type_order") val collectionTypeOrder: String? = null,
    @SerialName("sort_by_main_group") val sortByMainGroup: String? = null,
    @SerialName("sort_by_gender") val sortByGender: String? = null,
    @SerialName("collection_label") val collectionLabel: String? = null,
    @SerialName("group_id") val groupId: String? = null,
)

@Serializable
data class SubstitutionPlan(
    val id: String,
    val name: String,
    @SerialName("valid_from") val validFrom: String? = null,
    @SerialName("valid_to") val validTo: String? = null,
    val stundenplan24: String? = null,
    val days: List<SubstitutionPlanDay>? = null,
)

@Serializable
data class SubstitutionPlanDay(
    val id: Int,
    val date: String,
    val notes: String,
    val lessons: List<SubstitutionPlanLesson>? = null,
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
    val rooms: List<Room>? = null,
)

@Serializable
data class Tag(
    val id: Int,
    @SerialName("local_id") val localId: String,
    val name: String,
    val hide: Int,
    @SerialName("for") val for_: String,
    val taggable: String,
)

@Serializable
data class Teacher(
    val id: Int? = null,
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
    val notes: String? = null,
)

@Serializable
data class TimeTable(
    val id: String,
    val name: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    val stundenplan24: String? = null,
    val weeks: List<TimeTableWeek>?,
    @SerialName("no_school_dates") val noSchoolDates: List<String>?,
    val lessons: List<TimeTableLesson>? = null,
)

@Serializable
data class TimeTableWeek(
    val nr: Int,
    val year: String,
    val types: List<String>,
)

@Serializable
data class TimeTableLesson(
    val id: Int,
    val weekday: Int,
    val nr: String,
    val subject: Subject? = null,
    val group: Group? = null,
    val teachers: List<Teacher>? = null,
    val rooms: List<Room>? = null,
)

@Serializable
data class TimeTableTime(
    val id: Int,
    val name: String,
    val type: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    val default: Int,
    val lessons: List<TimeTableTimeLesson>,
)

@Serializable
data class TimeTableTimeLesson(
    val id: String? = null,
    val nr: String? = null,
    val to: String? = null,
    val from: String? = null,
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
    val verification: UpdateAbsenceVerificationRequest? = null,
)

@Serializable
data class UpdateAbsenceTypeRequest(
    val name: String,
    val default: Boolean,
    @SerialName("editable_as") val editableAs: String? = null,
    @SerialName("default_present") val defaultPresent: Boolean? = null,
)

@Serializable
data class UpdateAbsenceVerificationRequest(
    val id: Double? = null,
    val confirmed: Boolean? = null,
    @SerialName("local_id") val localId: String? = null,
    val note: String? = null,
    @SerialName("recorded_at") val recordedAt: String? = null,
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
    @SerialName("group_ids") val groupIds: List<Int>? = null,
)

@Serializable
data class UpdateAnnouncementTypeRequest(
    val name: String,
    val color: String? = null,
    val default: Boolean,
    @SerialName("default_for") val defaultFor: String,
)

@Serializable
data class UpdateCertificateGradeRequest(
    val value: String? = null,
    val name: String? = null,
    @SerialName("given_at") val givenAt: String? = null,
    val type: String? = null,
)

@Serializable
data class UpdateChecklistRequest(
    val name: String,
    val date: String,
    val description: String? = null,
    @SerialName("for") val for_: String,
    @SerialName("type_id") val typeId: Int? = null,
    @SerialName("group_ids") val groupIds: List<Int?>? = null,
    val checks: UpdateChecklistCheck? = null,
)

@Serializable
data class UpdateChecklistCheck(
    val checked: Boolean,
    @SerialName("checked_at") val checkedAt: String? = null,
    val note: String? = null,
)

@Serializable
data class UpdateChecklistTypeRequest(
    val name: String,
)

@Serializable
data class UpdateCollectionRequest(
    val type: String,
    val name: String,
    val weighting: Int? = null,
    @SerialName("given_at") val givenAt: String,
    @SerialName("visible_from") val visibleFrom: String,
    @SerialName("interval_id") val intervalId: Int,
)

@Serializable
data class UpdateFavoriteRequest(
    @SerialName("student_id") val studentId: Int? = null,
    @SerialName("subject_id") val subjectId: Int? = null,
    @SerialName("group_id") val groupId: Int? = null,
)

@Serializable
data class UpdateFinalCertificateRequest(
    @SerialName("certificate_type") val certificateType: String? = null,
    @SerialName("certificate_date") val certificateDate: String? = null,
    val custom: List<String>? = null,
    @SerialName("year_ids") val yearIds: List<String>? = null,
)

@Serializable
data class UpdateFinalgradeRequest(
    val value: String? = null,
    @SerialName("calculation_rule") val calculationRule: String? = null,
    @SerialName("calculation_verbal") val calculationVerbal: String? = null,
    @SerialName("calculation_for") val calculationFor: String? = null,
)

@Serializable
data class UpdateGradeRequest(
    val value: String,
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
    @SerialName("tag_ids") val tagIds: List<Int>? = null,
)

@Serializable
data class UpdateImporterRequest(
    val active: Boolean,
    @SerialName("secret_student") val secretStudent: String? = null,
    @SerialName("secret_teacher") val secretTeacher: String? = null,
    @SerialName("webhook_secret") val webhookSecret: String? = null,
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
    @SerialName("substitution_plan_meta_id") val substitutionPlanMetaId: Int? = null,
)

@Serializable
data class UpdateIntervalRequest(
    val name: String,
    val type: String? = null,
    val from: String,
    val to: String,
    @SerialName("editable_to") val editableTo: String,
    @SerialName("included_interval_id") val includedIntervalId: Int? = null,
)

@Serializable
data class UpdateJournalLessonRequest(
    val status: String? = null,
    @SerialName("subject_id") val subjectId: Int? = null,
    val statuses: List<String>? = null,
    @SerialName("teacher_ids") val teacherIds: List<Int?>? = null,
    @SerialName("room_ids") val roomIds: List<Int?>? = null,
)

@Serializable
data class UpdateJournalLessonStudentRequest(
    val present: Boolean? = null,
    @SerialName("too_early") val tooEarly: Int? = null,
    @SerialName("too_late") val tooLate: Int? = null,
    @SerialName("missing_homework") val missingHomework: Boolean? = null,
    @SerialName("missing_equipment") val missingEquipment: Boolean? = null,
    @SerialName("absence_id") val absenceId: Int? = null,
)

@Serializable
data class UpdateJournalNoteRequest(
    @SerialName("type_id") val typeId: Int,
    @SerialName("for") val for_: String,
    val description: String,
)

@Serializable
data class UpdateJournalNoteTypeRequest(
    val name: String,
    val color: String? = null,
    val default: Boolean,
    @SerialName("default_for") val defaultFor: String,
)

@Serializable
data class UpdateLevelRequest(
    val name: String,
    @SerialName("interval_type") val intervalType: String? = null,
    @SerialName("time_type") val timeType: String? = null,
    @SerialName("best_grade") val bestGrade: Int? = null,
    @SerialName("worst_grade") val worstGrade: Int? = null,
)

@Serializable
data class UpdateNoteRequest(
    val title: String? = null,
    val description: String? = null,
    @SerialName("for") val for_: String? = null,
    @SerialName("note_type_id") val noteTypeId: Int? = null,
    @SerialName("recorded_at") val recordedAt: String? = null,
)

@Serializable
data class UpdateNoteTypeRequest(
    val color: String? = null,
    val name: String? = null,
    @SerialName("notable_type") val notableType: String? = null,
    val default: Boolean? = null,
    @SerialName("default_for") val defaultFor: String? = null,
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
    @SerialName("headteacher_name") val headteacherName: String,
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
    @SerialName("valid_to") val validTo: String,
)

@Serializable
data class UpdateTagRequest(
    @SerialName("local_id") val localId: String,
    val name: String,
    val hide: Boolean,
    @SerialName("for") val for_: String,
)

@Serializable
data class UpdateTeacherRequest(
    val name: String,
    val forename: String,
    val role: String? = null,
    val deleted: Boolean? = null,
    @SerialName("local_id") val localId: String,
    @SerialName("tag_ids") val tagIds: List<Int>? = null,
)

@Serializable
data class UpdateTimeTableRequest(
    val name: String,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    @SerialName("weeks") val weeks: String? = null,
    @SerialName("no_school_dates") val noSchoolDates: String? = null,
)

@Serializable
data class UpdateTimeTableTimeLessonRequest(
    val nr: String,
    val from: String,
    val to: String,
)

@Serializable
data class UpdateTimeTableTimeRequest(
    val name: String,
    val type: String? = null,
    @SerialName("valid_from") val validFrom: String,
    @SerialName("valid_to") val validTo: String,
    val default: Boolean,
)

@Serializable
data class UpdateUserRequest(
    val username: String? = null,
    val email: String? = null,
    @SerialName("email_private") val emailPrivate: String? = null,
    @SerialName("email_business") val emailBusiness: String? = null,
    @SerialName("phone_private") val phonePrivate: String? = null,
    @SerialName("phone_business") val phoneBusiness: String? = null,
)

@Serializable
data class UpdateYearRequest(
    val name: String,
    val from: String,
    val to: String,
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
    val socialiteProviders: List<UserSocialite>? = null,
)

@Serializable
data class UserSocialite(
    val id: Int,
    val provider: String,
    val providerId: String,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class Year(
    val id: Int,
    val ids: List<String>,
    val name: String,
    val from: String,
    val to: String,
    val intervals: List<Interval>? = null,
)

@Serializable
data class Meta(
    @SerialName("current_page") val currentPage: Int? = null,
    val from: Int? = null,
    @SerialName("last_page") val lastPage: Int? = null,
    val path: String? = null,
    @SerialName("per_page") val perPage: Int? = null,
    val to: Int? = null,
    val total: Int? = null,
)

@Serializable
data class DataWrapper<T>(
    val data: T,
    val meta: Meta? = null,
)

@Serializable
data class ListDataWrapper<T>(
    val data: List<T>,
    val meta: Meta? = null,
)

@Serializable
data class PaginatedDataWrapper<T>(
    val data: List<T>,
    val links: PaginationLinks,
    val meta: PaginationMeta,
)

@Serializable
data class PaginationLinks(
    val first: String? = null,
    val last: String? = null,
    val prev: String? = null,
    val next: String? = null,
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
    val total: Int,
)

@Serializable
data class PaginationMetaLink(
    val url: String? = null,
    val label: String,
    val active: Boolean,
)

@Serializable
data class SimpleSuccessResponse(
    val success: String,
)

@Serializable
data class SimplePasswordResponse(
    val password: String,
)

@Serializable
data class SimpleResultResponse(
    val result: String,
)

@Serializable
data class SimpleMessageResponse(
    val message: String,
)

@Serializable
data class SimpleVerifiedResponse(
    val verified: Boolean,
)

@Serializable
data class GroupSubjectOrderResponse(
    @SerialName("collection_type_order") val collectionTypeOrder: String,
    @SerialName("sort_by_main_group") val sortByMainGroup: String,
    @SerialName("sort_by_gender") val sortByGender: String,
    @SerialName("collection_label") val collectionLabel: String,
)

@Serializable
data class SiteStatusResponse(
    @SerialName("message_text") val messageText: String,
    @SerialName("message_url") val messageUrl: String,
)

@Serializable
data class SimpleSecretResponse(
    val secret: String,
)

@Serializable
data class VerifyTwoFactorUserRequest(
    @SerialName("one_time_password") val oneTimePassword: String? = null,
)

@Serializable
data class AddMembershipUserRequest(
    val token: String,
)
