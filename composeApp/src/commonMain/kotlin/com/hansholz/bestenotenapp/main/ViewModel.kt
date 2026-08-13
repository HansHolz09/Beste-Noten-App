package com.hansholz.bestenotenapp.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import com.dokar.sonner.ToasterState
import com.hansholz.bestenotenapp.api.BesteSchuleApi
import com.hansholz.bestenotenapp.api.BesteSchuleAuth
import com.hansholz.bestenotenapp.api.codeAuthFlowFactory
import com.hansholz.bestenotenapp.api.createHttpClient
import com.hansholz.bestenotenapp.api.models.Absence
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.api.models.Interval
import com.hansholz.bestenotenapp.api.models.JournalDay
import com.hansholz.bestenotenapp.api.models.JournalDayStudentCount
import com.hansholz.bestenotenapp.api.models.JournalLessonStudentBySlot
import com.hansholz.bestenotenapp.api.models.JournalLessonStudentCount
import com.hansholz.bestenotenapp.api.models.JournalWeek
import com.hansholz.bestenotenapp.api.models.Level
import com.hansholz.bestenotenapp.api.models.SetCurrentYearRequest
import com.hansholz.bestenotenapp.api.models.Student
import com.hansholz.bestenotenapp.api.models.Subject
import com.hansholz.bestenotenapp.api.models.Teacher
import com.hansholz.bestenotenapp.api.models.TimeTable
import com.hansholz.bestenotenapp.api.models.TimeTableTime
import com.hansholz.bestenotenapp.api.models.User
import com.hansholz.bestenotenapp.api.models.Year
import com.hansholz.bestenotenapp.api.oidcClient
import com.hansholz.bestenotenapp.data.DemoDataGenerator
import com.hansholz.bestenotenapp.data.ExportData
import com.hansholz.bestenotenapp.data.besteSchuleCacheSize
import com.hansholz.bestenotenapp.data.clearBesteSchuleCache
import com.hansholz.bestenotenapp.homework.GoogleCalendarApi
import com.hansholz.bestenotenapp.homework.GoogleCalendarHomeworkSyncDataSource
import com.hansholz.bestenotenapp.homework.HomeworkEntry
import com.hansholz.bestenotenapp.homework.KSafeGoogleAuthProvider
import com.hansholz.bestenotenapp.homework.KSafeHomeworkRepository
import com.hansholz.bestenotenapp.homework.KSafeHomeworkSyncSettings
import com.hansholz.bestenotenapp.notifications.GradeNotifications
import com.hansholz.bestenotenapp.security.kSafe
import com.hansholz.bestenotenapp.security.kSafeProvider
import com.hansholz.bestenotenapp.utils.IO
import com.hansholz.bestenotenapp.utils.defaultFileKitDialogSettings
import com.hansholz.bestenotenapp.utils.parseGradeValue
import com.hansholz.bestenotenapp.utils.weekOfYear
import dev.chrisbanes.haze.HazeState
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readString
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.publicvalue.multiplatform.oidc.DefaultOpenIdConnectClient
import org.publicvalue.multiplatform.oidc.OpenIdConnectException
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import com.hansholz.bestenotenapp.data.readBesteSchuleCache as readStoredBesteSchuleCache
import com.hansholz.bestenotenapp.data.writeBesteSchuleCache as writeStoredBesteSchuleCache

class ViewModel(
    toasterState: ToasterState,
) : ViewModel() {
    val toaster = toasterState
    val kSafe = kSafe()

    private val json =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    private val cacheJson =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    val offlineCacheAvailable = getPlatform() != Platform.WEB

    private val httpClient = createHttpClient()

    val authToken = mutableStateOf<String?>(null)
    private val authClient = DefaultOpenIdConnectClient(httpClient, oidcClient.config)
    private val authFlow = codeAuthFlowFactory.createAuthFlow(authClient)
    private val besteSchuleAuth = BesteSchuleAuth(authClient, kSafe, authToken)

    val studentId = mutableStateOf<String?>(null)
    val isBesteSchuleNotReachable = mutableStateOf(false)
    val isUsingOfflineCache = mutableStateOf(false)
    private val api = BesteSchuleApi(httpClient, authToken, studentId, besteSchuleAuth::getValidAccessToken)
    val homeworkSyncSettings = KSafeHomeworkSyncSettings(kSafe)
    val homeworkRevision = mutableIntStateOf(0)
    private val googleAuthProvider = KSafeGoogleAuthProvider(kSafe)
    private val homeworkRepository =
        KSafeHomeworkRepository(
            kSafe,
            GoogleCalendarHomeworkSyncDataSource(GoogleCalendarApi(httpClient, googleAuthProvider)) { studentId.value },
            homeworkSyncSettings,
            canAutoSync = { !isUsingOfflineCache.value },
        )

    val hazeBackgroundState = HazeState()
    val hazeBackgroundState1 = HazeState()
    val hazeBackgroundState2 = HazeState()
    val hazeBackgroundState3 = HazeState()

    val compactDrawerState = mutableStateOf(DrawerState(DrawerValue.Closed))
    val mediumExpandedDrawerState = mutableStateOf(DrawerState(DrawerValue.Open))

    val user = mutableStateOf<User?>(null)
    val level = mutableStateOf<Level?>(null)
    val levelsByYear = mutableStateMapOf<Int, Level>()
    val currentJournalDay = mutableStateOf<JournalDay?>(null)
    val journalWeeks = mutableStateListOf<Pair<String, JournalWeek>>()
    val currentTimetable = mutableStateOf<TimeTable?>(null)
    val absences = mutableStateListOf<Pair<String, List<Absence>>>()
    val subjectsAndTeachers = mutableStateListOf<Pair<Subject?, List<Teacher>?>>()
    val teachersAndSubjects = mutableStateListOf<Pair<Teacher?, List<Subject?>>>()
    val subjects = mutableStateListOf<Subject>()

    val startGradeCollections = mutableStateListOf<GradeCollection>()
    val gradeCollections = mutableStateListOf<GradeCollection>()
    val allGradeCollectionsLoaded = mutableStateOf(false)
    val years = mutableStateListOf<Year>()
    val intervals = mutableStateListOf<Interval>()
    val times = mutableStateListOf<TimeTableTime>()
    val dayStudentCount = mutableStateOf<JournalDayStudentCount?>(null)
    val lessonStudentCount = mutableStateOf<JournalLessonStudentCount?>(null)
    val lessonStudentBySlot = mutableStateListOf<JournalLessonStudentBySlot>()
    val currentDayStudentCount = mutableStateOf<JournalDayStudentCount?>(null)
    val currentLessonStudentCount = mutableStateOf<JournalLessonStudentCount?>(null)
    val currentLessonStudentBySlot = mutableStateListOf<JournalLessonStudentBySlot>()

    val isDemoAccount = mutableStateOf(false)
    private var demoWeekPlan: List<List<Subject>> = emptyList()
    private var demoIntervalsByYear: Map<Int, List<Interval>> = emptyMap()
    private var demoAbsencesByYear: Map<Int, List<Absence>> = emptyMap()
    private var demoDayStudentCountsByYear: Map<Int, JournalDayStudentCount> = emptyMap()
    private var demoLessonStudentCountsByYear: Map<Int, JournalLessonStudentCount> = emptyMap()
    private var demoTotalDayStudentCount: JournalDayStudentCount? = null
    private var demoTotalLessonStudentCount: JournalLessonStudentCount? = null
    private var demoLessonStudentBySlotByYear: Map<Int, List<JournalLessonStudentBySlot>> = emptyMap()
    private var demoTotalLessonStudentBySlot: List<JournalLessonStudentBySlot> = emptyList()

    suspend fun getHomeworkForDate(date: LocalDate): List<HomeworkEntry> = homeworkRepository.getHomeworkForDate(date)

    suspend fun getHomeworkForLesson(
        timetableTimeLessonId: String,
        sourceDate: LocalDate,
    ): List<HomeworkEntry> = homeworkRepository.getHomeworkForLesson(timetableTimeLessonId, sourceDate)

    suspend fun hasUserHomeworkForDate(date: LocalDate): Boolean = homeworkSyncSettings.homeworkEnabled && homeworkRepository.hasUserDayNotes(date)

    suspend fun createHomework(entry: HomeworkEntry) {
        homeworkRepository.createHomework(entry)
        homeworkRevision.intValue++
    }

    suspend fun updateHomework(entry: HomeworkEntry) {
        homeworkRepository.updateHomework(entry)
        homeworkRevision.intValue++
    }

    suspend fun markHomeworkDone(
        localId: String,
        done: Boolean,
    ) {
        homeworkRepository.markHomeworkDone(localId, done)
        homeworkRevision.intValue++
    }

    suspend fun deleteHomework(localId: String) {
        homeworkRepository.deleteHomework(localId)
        homeworkRevision.intValue++
    }

    suspend fun syncHomeworkNow(showSuccessToast: Boolean = true) {
        homeworkRepository.syncNow()
        homeworkSyncSettings.lastSyncError?.let {
            toaster.show(
                Toast(
                    message = it,
                    type = ToastType.Error,
                    duration = ToasterDefaults.DurationLong,
                ),
            )
        } ?: run {
            homeworkRevision.intValue++
            if (showSuccessToast) {
                toaster.show(
                    Toast(
                        message = "Google Kalender wurde synchronisiert",
                        type = ToastType.Success,
                    ),
                )
            }
        }
    }

    suspend fun connectGoogleCalendarForHomework(): Boolean {
        try {
            googleAuthProvider.signIn()
            homeworkSyncSettings.googleSyncEnabled = true
            return true
        } catch (e: Exception) {
            homeworkSyncSettings.lastSyncError = e.message ?: "Google Kalender konnte nicht verbunden werden"
            homeworkSyncSettings.googleSyncEnabled = false
            toaster.show(
                Toast(
                    message = homeworkSyncSettings.lastSyncError!!,
                    type = ToastType.Error,
                    duration = ToasterDefaults.DurationLong,
                ),
            )
            return false
        }
    }

    suspend fun disconnectGoogleCalendarForHomework() {
        googleAuthProvider.signOut()
        homeworkSyncSettings.googleSyncEnabled = false
        homeworkSyncSettings.googleCalendarId = null
        homeworkSyncSettings.googleCalendarResolved = false
        homeworkSyncSettings.nextSyncToken = null
    }

    private fun couldReachBesteSchule() {
        isBesteSchuleNotReachable.value = false
        isUsingOfflineCache.value = false
    }

    private var besteSchuleRetryJob: Job? = null

    private fun useOfflineCache() {
        isBesteSchuleNotReachable.value = false
        isUsingOfflineCache.value = true
        if (besteSchuleRetryJob?.isActive == true) return
        besteSchuleRetryJob =
            viewModelScope.launch {
                while (isUsingOfflineCache.value && !authToken.value.isNullOrEmpty()) {
                    delay(5.seconds)
                    try {
                        user.value = api.userMe().data
                        user.value?.let { runCatching { writeBesteSchuleCache("user", it) } }
                        couldReachBesteSchule()
                        if (homeworkSyncSettings.googleSyncEnabled) {
                            homeworkRepository.syncNow()
                            homeworkRevision.intValue++
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        if (!e.isConnectionFailure()) {
                            couldNotReachBesteSchule()
                            return@launch
                        }
                    }
                }
            }
    }

    private fun couldNotReachBesteSchule() {
        isUsingOfflineCache.value = false
        if (isBesteSchuleNotReachable.value) return
        isBesteSchuleNotReachable.value = true
        toaster.show(
            Toast(
                message = "beste.schule konnte nicht erreicht werden",
                type = ToastType.Error,
            ),
        )
    }

    private suspend inline fun <reified T> readBesteSchuleCache(key: String): T? {
        if (!offlineCacheAvailable) return null
        val student = studentId.value ?: return null
        return readStoredBesteSchuleCache(student, key)?.let {
            runCatching { cacheJson.decodeFromString<T>(it) }.getOrNull()
        }
    }

    private suspend inline fun <reified T> writeBesteSchuleCache(
        key: String,
        value: T,
    ) {
        if (!offlineCacheAvailable) return
        val student = studentId.value ?: return
        writeStoredBesteSchuleCache(student, key, cacheJson.encodeToString(value))
    }

    private suspend inline fun <reified T> loadBesteSchuleData(
        key: String,
        crossinline request: suspend () -> T,
    ): T? {
        if (isUsingOfflineCache.value) return readBesteSchuleCache(key)
        return try {
            withTimeout(10.seconds) { request() }.also {
                runCatching { writeBesteSchuleCache(key, it) }
                couldReachBesteSchule()
            }
        } catch (_: TimeoutCancellationException) {
            readBesteSchuleCache<T>(key)?.also {
                useOfflineCache()
            } ?: run {
                if (!isUsingOfflineCache.value) couldNotReachBesteSchule()
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            if (e.isConnectionFailure()) {
                readBesteSchuleCache<T>(key)?.also {
                    useOfflineCache()
                } ?: run {
                    if (!isUsingOfflineCache.value) couldNotReachBesteSchule()
                    null
                }
            } else {
                couldNotReachBesteSchule()
                null
            }
        }
    }

    suspend fun clearOfflineCache() {
        try {
            clearBesteSchuleCache()
            toaster.show(
                Toast(
                    message = "Offline-Daten wurden geleert",
                    type = ToastType.Success,
                ),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.show(
                Toast(
                    message = "Offline-Daten konnten nicht geleert werden",
                    type = ToastType.Error,
                ),
            )
        }
    }

    suspend fun offlineCacheSize() = besteSchuleCacheSize()

    suspend fun getAccessToken(): Boolean {
        try {
            besteSchuleAuth.setTokenResponse(authFlow.getAccessToken())
            return !authToken.value.isNullOrEmpty()
        } catch (e: OpenIdConnectException.UnsuccessfulTokenRequest) {
            try {
                val withUnknownKeys = Json { ignoreUnknownKeys = true }
                besteSchuleAuth.setTokenResponse(withUnknownKeys.decodeFromString<AccessTokenResponse>(e.body ?: ""))
                return !authToken.value.isNullOrEmpty()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
    }

    suspend fun login(
        stayLoggedIn: Boolean,
        isLoading: (Boolean) -> Unit,
        onNavigateHome: () -> Unit,
        chooseStudent: suspend (List<Student>, (String) -> Unit) -> Unit,
        handleToken: suspend () -> Unit,
    ) = kSafeProvider(kSafe) {
        isLoading(true)
        try {
            handleToken()
            val user = init()
            if (user?.role !in listOf("student", "guardian")) {
                toaster.show(
                    Toast(
                        message = "Es sind ausschließlich Schüler/Eltern-Accounts zulässig",
                        type = ToastType.Error,
                    ),
                )
                isLoading(false)
            } else {
                user!!.students!!.size.let {
                    if (it > 1) {
                        chooseStudent(user.students) {
                            put("studentId", it)
                            studentId.value = it
                            GradeNotifications.onLogin()
                        }
                    } else {
                        put(
                            "studentId",
                            user.students
                                .first()
                                .id
                                .toString(),
                        )
                        studentId.value =
                            user.students
                                .first()
                                .id
                                .toString()
                        GradeNotifications.onLogin()
                    }
                }
                loadCurrentLevel()
                writeBesteSchuleCache("user", user)
                setCurrentYear()
                if (stayLoggedIn) {
                    besteSchuleAuth.persist()
                }
                onNavigateHome()
                toaster.show(
                    Toast(
                        message = "Angemeldet als ${user.username}",
                        type = ToastType.Success,
                    ),
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.show(
                Toast(
                    message = "Anmeldung fehlgeschlagen",
                    type = ToastType.Error,
                ),
            )
            isLoading(false)
        }
    }

    fun loginDemo(
        isLoading: (Boolean) -> Unit,
        onNavigateHome: () -> Unit,
    ) {
        isLoading(true)
        try {
            GradeNotifications.onLogout()
            onCleared()
            val data = DemoDataGenerator.generateInitialData()
            years.addAll(data.years)
            subjects.addAll(data.subjects)
            gradeCollections.addAll(data.gradeCollections)
            allGradeCollectionsLoaded.value = true
            currentTimetable.value = data.timeTable
            demoWeekPlan = data.weekPlan
            demoIntervalsByYear = data.intervalsByYear
            demoAbsencesByYear = data.absencesByYear
            demoDayStudentCountsByYear = data.dayStudentCountsByYear
            demoLessonStudentCountsByYear = data.lessonStudentCountsByYear
            demoLessonStudentBySlotByYear = data.lessonStudentBySlotByYear
            demoTotalDayStudentCount = data.totalDayStudentCount
            demoTotalLessonStudentCount = data.totalLessonStudentCount
            demoTotalLessonStudentBySlot = data.totalLessonStudentBySlot
            level.value = data.level
            levelsByYear.putAll(data.levelsByYear)
            user.value = data.user
            studentId.value = data.student.id.toString()
            isDemoAccount.value = true
            onNavigateHome()
            toaster.show(
                Toast(
                    message = "Demo-Account aktiviert",
                    type = ToastType.Success,
                ),
            )
        } finally {
            isLoading(false)
        }
    }

    suspend fun openGradesFromJson(onNavigateToGrades: () -> Unit) =
        withContext(Dispatchers.IO) {
            try {
                val file =
                    FileKit.openFilePicker(
                        type = FileKitType.File("json"),
                        dialogSettings = defaultFileKitDialogSettings("JSON mit Notendaten wählen"),
                    )
                file?.let { file ->
                    val data = json.decodeFromString<ExportData>(file.readString())
                    if (data.containsGradeYears != true || data.gradeYears?.first.isNullOrEmpty()) {
                        toaster.show(
                            Toast(
                                message = "JSON-Datei enthält keine Noten",
                                type = ToastType.Error,
                            ),
                        )
                        return@withContext
                    }
                    gradeCollections.clear()
                    years.clear()
                    levelsByYear.clear()
                    gradeCollections.addAll(data.gradeYears.first)
                    years.addAll(data.gradeYears.second)
                    levelsByYear.putAll(data.gradeLevels ?: inferLevels(data.gradeYears.first, data.gradeYears.second))
                    level.value = levelsByYear[years.lastOrNull()?.id]
                    allGradeCollectionsLoaded.value = true
                    onNavigateToGrades()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toaster.show(
                    Toast(
                        message = "Beim Öffnen ist ein Fehler aufgetreten",
                        type = ToastType.Error,
                    ),
                )
            }
        }

    fun clearOpenedGrades() {
        gradeCollections.clear()
        years.clear()
        levelsByYear.clear()
        level.value = null
        allGradeCollectionsLoaded.value = false
    }

    fun logout() {
        studentId.value = null
        kSafe.deleteDirect("studentId")
        besteSchuleAuth.clear()
        isDemoAccount.value = false
        GradeNotifications.onLogout()
        onCleared()
    }

    suspend fun closeOrOpenDrawer(isCompactWindow: Boolean) {
        if (isCompactWindow) {
            if (compactDrawerState.value.isClosed) {
                compactDrawerState.value.open()
            } else {
                compactDrawerState.value.close()
            }
        } else {
            if (mediumExpandedDrawerState.value.isClosed) {
                mediumExpandedDrawerState.value.open()
            } else {
                mediumExpandedDrawerState.value.close()
            }
        }
    }

    private suspend fun init(): User? {
        if (isDemoAccount.value) {
            return user.value
        }
        if (!authToken.value.isNullOrEmpty()) {
            user.value =
                loadBesteSchuleData("user") {
                    if (studentId.value != null) {
                        api.usersShow(studentId.value).data
                    } else {
                        api.userMe().data
                    }
                }
            loadCurrentLevel()
        }
        return user.value
    }

    suspend fun getYears(): List<Year>? {
        if (isDemoAccount.value) {
            return years.toList()
        }
        return loadBesteSchuleData<List<Year>>("years") { api.yearIndex().data }?.also { loadLevels(it) }
    }

    fun levelFor(collection: GradeCollection): Level? = collection.interval?.yearId?.let { levelsByYear[it] } ?: if (collection.interval == null) level.value else null

    private suspend fun loadLevels(years: List<Year>) {
        if (years.isEmpty()) return
        val yearIds = years.map { it.id }.sorted()
        val groups =
            loadBesteSchuleData("meta_groups_${yearIds.joinToString("-")}") {
                api
                    .groupsIndex(
                        filterMeta = true,
                        filterYear = yearIds.joinToString(","),
                    ).data
            } ?: return
        val yearGroups =
            years
                .filter { it.id !in levelsByYear }
                .mapNotNull { year ->
                    groups
                        .firstOrNull { group -> group.yearId == year.id }
                        ?.let { year.id to it.id }
                }
        coroutineScope {
            yearGroups
                .map { (yearId, groupId) -> async { yearId to loadLevel(groupId, yearId) } }
                .awaitAll()
                .forEach { (yearId, loadedLevel) -> loadedLevel?.let { levelsByYear[yearId] = it } }
        }
    }

    private suspend fun loadCurrentLevel() {
        val currentYear = user.value?.year
        val currentYearId = user.value?.config?.yearId ?: currentYear?.id
        user.value
            ?.students
            ?.find { it.id.toString() == studentId.value }
            ?.metaGroups
            ?.let { groups -> groups.firstOrNull { it.yearId == currentYearId } ?: groups.lastOrNull() }
            ?.let { group ->
                loadLevel(group.id)?.let {
                    currentYearId?.let { yearId -> levelsByYear[yearId] = it }
                    level.value = it
                }
            }
    }

    private suspend fun loadLevel(
        groupId: Int,
        yearId: Int? = null,
    ): Level? =
        loadBesteSchuleData("level_$groupId") {
            api.groupsShow(groupId, listOf("level"), yearId).data.level
        }

    private fun inferLevels(
        collections: List<GradeCollection>,
        years: List<Year>,
    ): Map<Int, Level> =
        years.associate { year ->
            val isSecondaryTwo =
                collections
                    .asSequence()
                    .filter { it.interval?.yearId == year.id }
                    .flatMap { it.grades.orEmpty().asSequence() }
                    .mapNotNull { parseGradeValue(it.value) }
                    .any { it == 0f || it > 6f }
            year.id to
                Level(
                    id = year.id,
                    name = year.name,
                    intervalType = if (isSecondaryTwo) "Sek 2" else "Sek 1",
                    timeType = if (isSecondaryTwo) "Sek 2" else "Sek 1",
                    bestGrade = if (isSecondaryTwo) 15 else 1,
                    worstGrade = if (isSecondaryTwo) 0 else 6,
                )
        }

    suspend fun getIntervals(): List<Interval>? {
        if (isDemoAccount.value) {
            delay(250.milliseconds)
            return years.lastOrNull()?.let { demoIntervalsByYear[it.id] }.orEmpty()
        }
        return loadBesteSchuleData("intervals") { api.studentsShow(studentId.value!!, listOf("intervals")).data.intervals }
    }

    suspend fun getTimes(): List<TimeTableTime>? = loadBesteSchuleData("times") { api.timeTableTimesIndex().data }

    suspend fun getDayStudentCount(year: Year? = null): JournalDayStudentCount? {
        if (isDemoAccount.value) {
            delay(250.milliseconds)
            return year?.let { demoDayStudentCountsByYear[it.id] } ?: demoTotalDayStudentCount
        }
        return loadBesteSchuleData("dayStudentCount_${year?.id ?: "all"}") {
            year
                ?.let {
                    api.journalDayStudentStatisticsCount(filterRange = "${it.from},${it.to}").data.firstOrNull()
                }
                ?: api.journalDayStudentStatisticsCount().data.firstOrNull()
                ?: return@loadBesteSchuleData null
        }
    }

    suspend fun getLessonStudentCount(year: Year? = null): JournalLessonStudentCount? {
        if (isDemoAccount.value) {
            delay(250.milliseconds)
            return year?.let { demoLessonStudentCountsByYear[it.id] } ?: demoTotalLessonStudentCount
        }
        return loadBesteSchuleData("lessonStudentCount_${year?.id ?: "all"}") {
            year
                ?.let {
                    api.journalLessonStudentStatisticsCount(filterRange = "${it.from},${it.to}").data.firstOrNull()
                }
                ?: api.journalLessonStudentStatisticsCount().data.firstOrNull()
                ?: return@loadBesteSchuleData null
        }
    }

    suspend fun getLessonStudentBySlot(year: Year? = null): List<JournalLessonStudentBySlot>? {
        if (isDemoAccount.value) {
            delay(250.milliseconds)
            return if (year == null) demoTotalLessonStudentBySlot else demoLessonStudentBySlotByYear[year.id].orEmpty()
        }
        return loadBesteSchuleData("lessonStudentBySlot_${year?.id ?: "all"}") {
            year
                ?.let {
                    api.journalLessonStudentStatisticsBySlot(filterRange = "${it.from},${it.to}").data
                }
                ?: api.journalLessonStudentStatisticsBySlot().data
        }
    }

    suspend fun getCollections(filterYears: List<Year>? = null): List<GradeCollection>? {
        if (isDemoAccount.value) {
            delay(1.seconds)
            val filterYearIds = filterYears.orEmpty().mapTo(mutableSetOf()) { it.id }
            return if (filterYears.isNullOrEmpty()) {
                gradeCollections.toList()
            } else {
                gradeCollections.filter { it.interval?.yearId in filterYearIds || it.intervalId in filterYearIds }
            }
        }
        filterYears?.let { loadLevels(it) }
        return loadBesteSchuleData(
            "collections_${filterYears.orEmpty().map { it.id }.sorted().joinToString("-").ifBlank { "all" }}",
        ) {
            val includes = listOf("grades", "interval", "grades.histories", "histories")
            if (filterYears.isNullOrEmpty()) {
                getCollectionsPages(includes)
            } else {
                coroutineScope {
                    filterYears
                        .map { year ->
                            async {
                                getCollectionsPages(
                                    includes = includes,
                                    filterYear = year.id.toString(),
                                )
                            }
                        }.awaitAll()
                        .flatten()
                }
            }
        }
    }

    suspend fun getJournalWeek(
        date: LocalDate? = null,
        useCached: Boolean = true,
        getAbsences: Boolean = false,
    ): JournalWeek? {
        if (isDemoAccount.value) {
            val targetDate =
                date ?: Clock.System
                    .now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            val nr = "${targetDate.year}-${targetDate.weekOfYear}"
            val cachedWeek = if (useCached) journalWeeks.firstOrNull { it.first == nr }?.second else null
            val week = cachedWeek ?: DemoDataGenerator.generateJournalWeek(targetDate, demoWeekPlan)
            if (cachedWeek == null) {
                delay(1.seconds)
                if (!useCached) journalWeeks.removeAll { it.first == nr }
                journalWeeks.add(nr to week)
            }
            return week
        }
        val currentNr =
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
                .let { "${it.year}-${it.weekOfYear}" }
        val nr = date?.let { "${it.year}-${it.weekOfYear}" } ?: currentNr
        val year =
            date?.let {
                years
                    .firstOrNull { schoolYear ->
                        val fromDate = LocalDate.parse(schoolYear.from)
                        val toDate = LocalDate.parse(schoolYear.to)
                        date in fromDate..toDate
                    }?.id
                    ?.toString()
            }
        if (getAbsences && year != null && absences.none { it.first == year }) {
            getAbsences(year)?.let { absences.add(year to it) }
        }
        val cachedWeek = if (useCached) journalWeeks.firstOrNull { it.first == nr }?.second else null
        val week =
            cachedWeek ?: loadBesteSchuleData("journalWeek_$nr") {
                api.journalWeekShow(nr, year, true, "days.lessons").data
            } ?: return null
        if (cachedWeek == null) {
            if (!useCached) journalWeeks.removeAll { it.first == nr }
            journalWeeks.add(nr to week)
        }
        return week
    }

    suspend fun getSubjectsAndTeachers(): List<Pair<Subject?, List<Teacher>?>>? {
        if (isDemoAccount.value) delay(500.milliseconds)
        val timetable = getCurrentTimetable() ?: return null
        return timetable.lessons
            ?.groupBy { it.subject }
            ?.map {
                it.key to
                    it.value
                        .flatMap { lesson -> lesson.teachers.orEmpty() }
                        .toSet()
                        .toList()
            }
    }

    suspend fun getTeachersAndSubjects(): List<Pair<Teacher?, List<Subject?>>>? {
        if (isDemoAccount.value) delay(500.milliseconds)
        val timetable = getCurrentTimetable() ?: return null
        return timetable.lessons
            ?.groupBy { it.teachers }
            ?.map {
                it.key?.firstOrNull() to
                    it.value
                        .map { lesson -> lesson.subject }
                        .toSet()
                        .toList()
            }
    }

    suspend fun getSubjects(): List<Subject>? {
        if (isDemoAccount.value) {
            delay(500.milliseconds)
            return subjects.toList()
        }
        return loadBesteSchuleData("subjects") { api.subjectsIndex().data }
    }

    suspend fun getAbsences(filterYear: String? = null): List<Absence>? {
        if (isDemoAccount.value) {
            delay(500.milliseconds)
            return if (filterYear != null) {
                demoAbsencesByYear[filterYear.toIntOrNull()] ?: emptyList()
            } else {
                demoAbsencesByYear.values.flatten()
            }
        }
        return loadBesteSchuleData("absences_${filterYear ?: "all"}") {
            api.absencesIndex(filterYear = filterYear).data
        }
    }

    private suspend fun getCurrentTimetable(): TimeTable? {
        currentTimetable.value?.let { return it }
        currentTimetable.value =
            loadBesteSchuleData("timetable") {
                api.timeTablesIndex().data.lastOrNull()?.id?.let {
                    api.timeTablesShow(it).data
                }
            }
        return currentTimetable.value
    }

    private suspend fun getCollectionsPages(
        includes: List<String>,
        filterYear: String? = null,
    ): List<GradeCollection> =
        coroutineScope {
            val firstPage = api.collectionsIndex(include = includes, filterYear = filterYear)
            val lastPage = (firstPage.meta?.lastPage ?: 1).coerceAtLeast(1)
            if (lastPage == 1) {
                firstPage.data
            } else {
                firstPage.data +
                    (2..lastPage)
                        .map { page ->
                            async {
                                api.collectionsIndex(include = includes, filterYear = filterYear, page = page).data
                            }
                        }.awaitAll()
                        .flatten()
            }
        }

    suspend fun setCurrentYear(year: Int? = null) {
        api.yearSetCurrent(SetCurrentYearRequest(year))
    }

    suspend fun reload() {
        onCleared()
        init()
    }

    init {
        viewModelScope.launch {
            try {
                kSafeProvider(kSafe) {
                    studentId.value = get<String?>("studentId", null)
                }
                besteSchuleAuth.restore()
                init()
                GradeNotifications.onLogin()
                if (!isUsingOfflineCache.value) syncHomeworkNow(false)
            } catch (e: Exception) {
                e.printStackTrace()
                toaster.show(
                    Toast(
                        message = "Fehler bei der Initialisierung",
                        type = ToastType.Error,
                    ),
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        user.value = null
        subjects.clear()
        startGradeCollections.clear()
        gradeCollections.clear()
        allGradeCollectionsLoaded.value = false
        years.clear()
        journalWeeks.clear()
        currentTimetable.value = null
        absences.clear()
        subjectsAndTeachers.clear()
        teachersAndSubjects.clear()
        currentJournalDay.value = null
        level.value = null
        levelsByYear.clear()
        intervals.clear()
        dayStudentCount.value = null
        lessonStudentCount.value = null
        lessonStudentBySlot.clear()
        currentDayStudentCount.value = null
        currentLessonStudentCount.value = null
        currentLessonStudentBySlot.clear()
        demoWeekPlan = emptyList()
        demoIntervalsByYear = emptyMap()
        demoAbsencesByYear = emptyMap()
        demoDayStudentCountsByYear = emptyMap()
        demoLessonStudentCountsByYear = emptyMap()
        demoLessonStudentBySlotByYear = emptyMap()
        demoTotalDayStudentCount = null
        demoTotalLessonStudentCount = null
        demoTotalLessonStudentBySlot = emptyList()
        isBesteSchuleNotReachable.value = false
        isUsingOfflineCache.value = false
    }
}

private fun Throwable.isConnectionFailure(): Boolean {
    var current: Throwable? = this
    while (current != null) {
        if (
            current is HttpRequestTimeoutException ||
            current is ConnectTimeoutException ||
            current is SocketTimeoutException ||
            current is IOException
        ) {
            return true
        }
        current = current.cause
    }
    return false
}
