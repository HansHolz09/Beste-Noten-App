/** AI generated Generates random demo data for grades and timetable **/
package com.hansholz.bestenotenapp.demo

import com.hansholz.bestenotenapp.api.models.Grade
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.api.models.JournalDay
import com.hansholz.bestenotenapp.api.models.JournalLesson
import com.hansholz.bestenotenapp.api.models.JournalWeek
import com.hansholz.bestenotenapp.api.models.Subject
import com.hansholz.bestenotenapp.api.models.Teacher
import com.hansholz.bestenotenapp.api.models.TimeTableTimeLesson
import com.hansholz.bestenotenapp.api.models.Year
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

object DemoDataGenerator {
    data class DemoInitData(
        val years: List<Year>,
        val subjects: List<Subject>,
        val gradeCollections: List<GradeCollection>,
        val currentGrade: Int,
        val weekPlan: List<List<Subject>>
    )

    private val timeSlots = listOf(
        "07:35" to "08:20",
        "08:25" to "09:10",
        "09:30" to "10:15",
        "10:20" to "11:05",
        "11:15" to "12:00",
        "12:40" to "13:25",
        "13:30" to "14:15",
        "14:20" to "15:05"
    )

    private val firstNames = listOf("Anna", "Ben", "Clara", "David", "Eva", "Finn", "Julia", "Lukas", "Mia", "Noah")
    private val lastNames = listOf("Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer")

    private val subjectsByGrade: Map<Int, List<Pair<String, String>>> = mapOf(
        4 to listOf("Deutsch" to "DE", "Mathematik" to "MA", "Sachkunde" to "SU", "Englisch" to "EN", "Kunst" to "KU", "Musik" to "MU", "Sport" to "SP"),
        5 to listOf("Deutsch" to "DE", "Mathematik" to "MA", "Englisch" to "EN", "Biologie" to "BI", "Erdkunde" to "EK", "Geschichte" to "GE", "Kunst" to "KU", "Musik" to "MU", "Sport" to "SP"),
        6 to listOf("Deutsch" to "DE", "Mathematik" to "MA", "Englisch" to "EN", "Biologie" to "BI", "Erdkunde" to "EK", "Geschichte" to "GE", "Kunst" to "KU", "Musik" to "MU", "Sport" to "SP"),
        7 to listOf("Deutsch" to "DE", "Mathematik" to "MA", "Englisch" to "EN", "Biologie" to "BI", "Chemie" to "CH", "Physik" to "PH", "Geschichte" to "GE", "Erdkunde" to "EK", "Kunst" to "KU", "Musik" to "MU", "Sport" to "SP"),
        8 to listOf("Deutsch" to "DE", "Mathematik" to "MA", "Englisch" to "EN", "Biologie" to "BI", "Chemie" to "CH", "Physik" to "PH", "Geschichte" to "GE", "Erdkunde" to "EK", "Sozialkunde" to "SK", "Kunst" to "KU", "Musik" to "MU", "Sport" to "SP"),
        9 to listOf("Deutsch" to "DE", "Mathematik" to "MA", "Englisch" to "EN", "Biologie" to "BI", "Chemie" to "CH", "Physik" to "PH", "Geschichte" to "GE", "Erdkunde" to "EK", "Sozialkunde" to "SK", "Kunst" to "KU", "Musik" to "MU", "Sport" to "SP"),
        10 to listOf("Deutsch" to "DE", "Mathematik" to "MA", "Englisch" to "EN", "Biologie" to "BI", "Chemie" to "CH", "Physik" to "PH", "Geschichte" to "GE", "Erdkunde" to "EK", "Sozialkunde" to "SK", "Kunst" to "KU", "Musik" to "MU", "Sport" to "SP")
    )

    fun generateInitialData(): DemoInitData {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val numYears = Random.nextInt(3, 7)
        val startGrade = Random.nextInt(4, 11 - numYears)
        val years = mutableListOf<Year>()
        val subjects = mutableListOf<Subject>()
        val gradeCollections = mutableListOf<GradeCollection>()
        val subjectMap = mutableMapOf<String, Subject>()
        var collectionId = 1
        var gradeId = 1
        var subjectId = 1
        val weekPlan = mutableListOf<List<Subject>>()

        for (i in 0 until numYears) {
            val grade = startGrade + i
            val startYear = now.year - (numYears - 1 - i)
            val from = LocalDate(startYear, 8, 1)
            val to = LocalDate(startYear + 1, 7, 31)
            years.add(
                Year(
                    id = i + 1,
                    ids = listOf((i + 1).toString()),
                    name = "${startYear}/${startYear + 1}",
                    from = from.toString(),
                    to = to.toString(),
                    intervals = null
                )
            )
            val gradeSubjects = subjectsByGrade[grade] ?: subjectsByGrade[10]!!
            gradeSubjects.forEach { (name, local) ->
                if (subjectMap[name] == null) {
                    val teacher = Teacher(
                        id = subjectId,
                        forename = firstNames.random(),
                        name = lastNames.random()
                    )
                    val subject = Subject(
                        id = subjectId,
                        localId = local,
                        name = name,
                        teachers = listOf(teacher)
                    )
                    subjectMap[name] = subject
                    subjects.add(subject)
                    subjectId++
                }
            }
            val collectionsCount = Random.nextInt(30, 51)
            repeat(collectionsCount) {
                val subject = gradeSubjects.random().let { subjectMap[it.first]!! }
                val teacher = subject.teachers?.first()
                val daysBetween = from.daysUntil(to)
                val gradeDate = from.plus(Random.nextInt(daysBetween), DateTimeUnit.DAY)
                val gradeValue = when (Random.nextInt(100)) {
                    in 0..4 -> "1"
                    in 5..24 -> "2"
                    in 25..74 -> "3"
                    in 75..89 -> "4"
                    in 90..97 -> "5"
                    else -> "6"
                }
                val grade = Grade(
                    id = gradeId++,
                    value = gradeValue,
                    givenAt = gradeDate.toString()
                )
                gradeCollections.add(
                    GradeCollection(
                        id = collectionId++,
                        type = "exam",
                        weighting = Random.nextInt(1, 4),
                        name = "${subject.name} Test",
                        givenAt = gradeDate.toString(),
                        intervalId = i + 1,
                        subjectId = subject.id ?: 0,
                        teacherId = teacher?.id ?: 0,
                        subject = subject,
                        teacher = teacher,
                        grades = listOf(grade)
                    )
                )
            }
            if (i == numYears - 1) {
                repeat(5) {
                    val lessonsCount = Random.nextInt(5, 9)
                    val subjectsForDay = mutableListOf<Subject>()
                    repeat(lessonsCount) {
                        val subj = gradeSubjects.random().let { subjectMap[it.first]!! }
                        subjectsForDay.add(subj)
                    }
                    weekPlan.add(subjectsForDay)
                }
            }
        }
        return DemoInitData(years, subjects, gradeCollections, startGrade + numYears - 1, weekPlan)
    }

    fun generateJournalWeek(date: LocalDate, weekPlan: List<List<Subject>>): JournalWeek {
        val monday = date.minus(date.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
        val nowDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val days = mutableListOf<JournalDay>()
        weekPlan.forEachIndexed { dayIndex, plan ->
            val dayDate = monday.plus(dayIndex, DateTimeUnit.DAY)
            val lessons = plan.mapIndexed { lessonIndex, subject ->
                val slot = timeSlots[lessonIndex]
                val status = if (dayDate < nowDate) {
                    if (Random.nextInt(100) < 80) "hold" else "canceled"
                } else if (dayDate > nowDate) {
                    if (Random.nextInt(100) < 80) "planned" else "canceled"
                } else {
                    if (Random.nextInt(100) < 50) "hold" else if (Random.nextInt(100) < 20) "canceled" else "planned"
                }
                JournalLesson(
                    id = "demo-${dayIndex}-${lessonIndex}",
                    nr = (lessonIndex + 1).toString(),
                    status = status,
                    times = listOf(
                        TimeTableTimeLesson(
                            id = "time-${lessonIndex + 1}",
                            nr = (lessonIndex + 1).toString(),
                            from = slot.first,
                            to = slot.second
                        )
                    ),
                    time = TimeTableTimeLesson(
                        id = "time-${lessonIndex + 1}",
                        nr = (lessonIndex + 1).toString(),
                        from = slot.first,
                        to = slot.second
                    ),
                    subject = subject,
                    teachers = subject.teachers
                )
            }
            days.add(
                JournalDay(
                    id = "day-${dayIndex}",
                    date = dayDate.toString(),
                    lessons = lessons
                )
            )
        }
        val weekId = "${monday.year}-${monday.weekOfYear}"
        return JournalWeek(
            id = weekId,
            calendarYear = monday.year,
            nr = monday.weekOfYear,
            days = days
        )
    }
}

