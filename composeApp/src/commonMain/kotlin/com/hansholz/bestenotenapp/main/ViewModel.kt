@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.window.core.layout.WindowWidthSizeClass
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterState
import com.hansholz.bestenotenapp.api.BesteSchuleApi
import com.hansholz.bestenotenapp.api.codeAuthFlowFactory
import com.hansholz.bestenotenapp.api.createHttpClient
import com.hansholz.bestenotenapp.api.models.Finalgrade
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.api.models.JournalDay
import com.hansholz.bestenotenapp.api.models.JournalWeek
import com.hansholz.bestenotenapp.api.models.Student
import com.hansholz.bestenotenapp.api.models.Subject
import com.hansholz.bestenotenapp.api.models.User
import com.hansholz.bestenotenapp.api.models.Year
import com.hansholz.bestenotenapp.api.oidcClient
import com.hansholz.bestenotenapp.demo.DemoDataGenerator
import com.hansholz.bestenotenapp.security.AuthTokenManager
import com.hansholz.bestenotenapp.utils.weekOfYear
import com.russhwolf.settings.Settings
import dev.chrisbanes.haze.HazeState
import io.ktor.utils.io.CancellationException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.publicvalue.multiplatform.oidc.DefaultOpenIdConnectClient
import org.publicvalue.multiplatform.oidc.OpenIdConnectException

class ViewModel(toasterState: ToasterState) : ViewModel() {
    val toaster = toasterState
    val settings = Settings()

    private val httpClient = createHttpClient()

    val authTokenManager = AuthTokenManager()
    val authToken = mutableStateOf<String?>(null)
    private val authFlow = codeAuthFlowFactory.createAuthFlow(DefaultOpenIdConnectClient(httpClient, oidcClient.config))


    val studentId = mutableStateOf<String?>(null)
    private val api = BesteSchuleApi(httpClient, authToken, studentId)

    val hazeBackgroundState = HazeState()
    val hazeBackgroundState1 = HazeState()
    val hazeBackgroundState2 = HazeState()
    val hazeBackgroundState3 = HazeState()

    val compactDrawerState = mutableStateOf(DrawerState(DrawerValue.Closed))
    val mediumExpandedDrawerState = mutableStateOf(DrawerState(DrawerValue.Open))

    val user = mutableStateOf<User?>(null)
    val currentJournalDay = mutableStateOf<JournalDay?>(null)
    val journalWeeks = mutableStateListOf<Pair<String, JournalWeek>>()
    val finalGrades = mutableStateListOf<Finalgrade>()
    val subjects = mutableStateListOf<Subject>()

    val startGradeCollections = mutableStateListOf<GradeCollection>()
    val gradeCollections = mutableStateListOf<GradeCollection>()
    val allGradeCollectionsLoaded = mutableStateOf(false)
    val years = mutableStateListOf<Year>()

    val isDemoAccount = mutableStateOf(false)
    private var demoWeekPlan: List<List<Subject>> = emptyList()


    val isBesteSchuleNotReachable = mutableStateOf(false)
    private suspend fun couldReachBesteSchule() {
        if (isBesteSchuleNotReachable.value) init()
        isBesteSchuleNotReachable.value = false
    }
    private fun couldNotReachBesteSchule() {
        isBesteSchuleNotReachable.value = true
        toaster.show(
            Toast(
                message = "beste.schule konnte nicht erreicht werden",
                type = ToastType.Error
            )
        )
    }


    suspend fun getAccessToken(): Boolean {
        try {
            authToken.value = authFlow.getAccessToken().access_token
            return !authToken.value.isNullOrEmpty()
        } catch (e: OpenIdConnectException.UnsuccessfulTokenRequest) {
            try {
                @Serializable
                data class TokenResponse(@SerialName("access_token") val accessToken: String)

                val withUnknownKeys = Json { ignoreUnknownKeys = true }
                authToken.value = withUnknownKeys.decodeFromString<TokenResponse>(e.body ?: "").accessToken
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
    ) {
        isLoading(true)
        try {
            handleToken()
            val user = init()
            if (user?.role != "student") {
                toaster.show(
                    Toast(
                        message = "Es sind ausschließlich Schüler-Accounts zulässig",
                        type = ToastType.Error
                    )
                )
                isLoading(false)
            } else {
                user.students?.size?.let {
                    if (it > 1) {
                        chooseStudent(user.students) {
                            settings.putString("studentId", it)
                            studentId.value = it
                        }
                    } else {
                        settings.putString("studentId", user.students.first().id.toString())
                        studentId.value = user.students.first().id.toString()
                    }
                }
                if (stayLoggedIn) {
                    authTokenManager.saveToken(authToken.value!!)
                }
                onNavigateHome()
                toaster.show(
                    Toast(
                        message = "Angemeldet als ${user.username}",
                        type = ToastType.Success
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.show(
                Toast(
                    message = "Anmeldung fehlgeschlagen",
                    type = ToastType.Error
                )
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
            onCleared()
            val data = DemoDataGenerator.generateInitialData()
            years.addAll(data.years)
            subjects.addAll(data.subjects)
            gradeCollections.addAll(data.gradeCollections)
            allGradeCollectionsLoaded.value = true
            finalGrades.addAll(data.finalGrades)
            demoWeekPlan = data.weekPlan
            user.value = User(id = 0, username = "${data.student.forename} (Demo)", role = "student", students = listOf(data.student))
            studentId.value = data.student.id.toString()
            isDemoAccount.value = true
            onNavigateHome()
            toaster.show(
                Toast(
                    message = "Demo-Account aktiviert",
                    type = ToastType.Success
                )
            )
        } finally {
            isLoading(false)
        }
    }

    fun logout() {
        studentId.value = null
        settings.remove("studentId")
        authToken.value = null
        authTokenManager.deleteToken()
        isDemoAccount.value = false
        demoWeekPlan = emptyList()
        onCleared()
    }


    suspend fun closeOrOpenDrawer(windowWidthSizeClass: WindowWidthSizeClass) {
        if (windowWidthSizeClass == WindowWidthSizeClass.Companion.COMPACT) {
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
            user.value = api.userMe().data
        }
        return user.value
    }

    suspend fun getYears(): List<Year>? {
        if (isDemoAccount.value) {
            return years
        }
        try {
            val data = api.yearIndex().data
            couldReachBesteSchule()
            return data
        } catch (e: Exception) {
            e.printStackTrace()
            couldNotReachBesteSchule()
            return null
        }
    }

    suspend fun getCollections(filterYears: List<Year>? = null): List<GradeCollection>? {
        if (isDemoAccount.value) {
            return if (filterYears.isNullOrEmpty()) {
                gradeCollections
            } else {
                gradeCollections.filter { gc -> filterYears.any { it.id == gc.intervalId } }
            }
        }
        try {
            val includes = listOf("grades", "interval", "grades.histories")
            val collections = mutableStateListOf<GradeCollection>()
            collections.clear()
            if (filterYears.isNullOrEmpty()) {
                val collection = api.collectionsIndex(include = includes)
                collections.addAll(collection.data)
                if ((collection.meta?.lastPage ?: 0) > 1) {
                    for (i in 2..(collection.meta?.lastPage ?: 0)) {
                        collections.addAll(api.collectionsIndex(include = includes, page = i).data)
                    }
                }
            } else {
                filterYears.forEach {
                    val collection = api.collectionsIndex(include = includes, filterYear = it.id.toString())
                    collections.addAll(collection.data)
                    if ((collection.meta?.lastPage ?: 0) > 1) {
                        for (i in 2..(collection.meta?.lastPage ?: 0)) {
                            collections.addAll(api.collectionsIndex(include = includes, filterYear = it.id.toString(), page = i).data)
                        }
                    }
                }
            }
            couldReachBesteSchule()
            return collections
        } catch (e: Exception) {
            e.printStackTrace()
            couldNotReachBesteSchule()
            return null
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun getJournalWeek(date: LocalDate? = null, useCached: Boolean = true): JournalWeek? {
        if (isDemoAccount.value) {
            val targetDate = date ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val nr = "${targetDate.year}-${targetDate.weekOfYear}"
            val cachedWeek = if (useCached) journalWeeks.firstOrNull { it.first == nr }?.second else null
            val week = cachedWeek ?: DemoDataGenerator.generateJournalWeek(targetDate, demoWeekPlan)
            if (cachedWeek == null) {
                if (!useCached) journalWeeks.removeAll { it.first == nr }
                journalWeeks.add(nr to week)
            }
            return week
        }
        try {
            @OptIn(ExperimentalTime::class)
            val currentNr = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.let { "${it.year}-${it.weekOfYear}" }
            val nr = date?.let { "${it.year}-${it.weekOfYear}" } ?: currentNr
            val year = date?.let {
                years.firstOrNull { schoolYear ->
                    val fromDate = LocalDate.parse(schoolYear.from)
                    val toDate = LocalDate.parse(schoolYear.to)
                    date in fromDate..toDate
                }?.id.toString()
            }
            val cachedWeek = if (useCached) journalWeeks.firstOrNull { it.first == nr }?.second else null
            val week = cachedWeek ?: api.journalWeekShow(nr, year, true, "days.lessons").data
            if (cachedWeek == null) {
                if (!useCached) journalWeeks.removeAll { it.first == nr }
                journalWeeks.add(nr to week)
            }
            couldReachBesteSchule()
            return week
        } catch (e: CancellationException) {
            e.printStackTrace()
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            couldNotReachBesteSchule()
            return null
        }
    }

    suspend fun getFinalGrades(): List<Finalgrade>? {
        if (isDemoAccount.value) {
            return finalGrades
        }
        try {
            val data = api.finalgradesIndex().data
            couldReachBesteSchule()
            return data
        } catch (e: Exception) {
            e.printStackTrace()
            couldNotReachBesteSchule()
            return null
        }
    }

    suspend fun getSubjects(): List<Subject>? {
        if (isDemoAccount.value) {
            return subjects
        }
        try {
            val data = api.subjectsIndex().data
            couldReachBesteSchule()
            return data
        } catch (e: Exception) {
            e.printStackTrace()
            couldNotReachBesteSchule()
            return null
        }
    }

    init {
        viewModelScope.launch {
            try {
                studentId.value = settings.getStringOrNull("studentId")
                authToken.value = authTokenManager.getToken()
                init()
            } catch (e: Exception) {
                e.printStackTrace()
                toaster.show(
                    Toast(
                        message = "Fehler bei der Initialisierung",
                        type = ToastType.Error
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        user.value = null
        finalGrades.clear()
        subjects.clear()
        startGradeCollections.clear()
        gradeCollections.clear()
        allGradeCollectionsLoaded.value = false
        years.clear()
        journalWeeks.clear()
        currentJournalDay.value = null
    }
}


