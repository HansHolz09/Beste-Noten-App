@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
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
import com.hansholz.bestenotenapp.api.AuthTokenManager
import com.hansholz.bestenotenapp.api.BesteSchuleApi
import com.hansholz.bestenotenapp.api.models.Finalgrade
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.api.models.Subject
import com.hansholz.bestenotenapp.api.models.User
import com.hansholz.bestenotenapp.api.models.Year
import com.hansholz.bestenotenapp.api.codeAuthFlowFactory
import com.hansholz.bestenotenapp.api.createHttpClient
import com.hansholz.bestenotenapp.api.oidcClient
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.publicvalue.multiplatform.oidc.DefaultOpenIdConnectClient
import org.publicvalue.multiplatform.oidc.OpenIdConnectException

class ViewModel(toasterState: ToasterState) : ViewModel() {
    val toaster = toasterState

    private val httpClient = createHttpClient()

    val authTokenManager = AuthTokenManager()
    val authToken = mutableStateOf<String?>(null)
    private val authFlow = codeAuthFlowFactory.createAuthFlow(DefaultOpenIdConnectClient(httpClient, oidcClient.config))


    private val api = BesteSchuleApi(httpClient, authToken)

    val hazeBackgroundState = HazeState()
    val hazeBackgroundState1 = HazeState()
    val hazeBackgroundState2 = HazeState()
    val hazeBackgroundState3 = HazeState()

    val compactDrawerState = mutableStateOf(DrawerState(DrawerValue.Closed))
    val mediumExpandedDrawerState = mutableStateOf(DrawerState(DrawerValue.Open))

    val user = mutableStateOf<User?>(null)
    val finalGrades = mutableStateListOf<Finalgrade>()
    val subjects = mutableStateListOf<Subject>()

    val startGradeCollections = mutableStateListOf<GradeCollection>()
    val gradeCollections = mutableStateListOf<GradeCollection>()
    val allGradeCollectionsLoaded = mutableStateOf(false)
    val years = mutableStateListOf<Year>()


    private fun couldNotReachBesteSchule() {
        toaster.show(
            Toast(
                message = "beste.schule konnte nicht erreicht werden",
                icon = Icons.Outlined.Error,
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
                data class TokenResponse(val access_token: String)

                val withUnknownKeys = Json { ignoreUnknownKeys = true }
                authToken.value = withUnknownKeys.decodeFromString<TokenResponse>(e.body ?: "").access_token
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
        handleToken: suspend () -> Unit,
    ) {
        isLoading(true)
        try {
            handleToken()
            val user = init()
            if (stayLoggedIn) {
                authTokenManager.saveToken(authToken.value!!)
            }
            onNavigateHome()
            toaster.show(
                Toast(
                    message = "Angemeldet als ${user?.username}",
                    type = ToastType.Success
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.show(
                Toast(
                    message = "Anmeldung fehlgeschlagen",
                    icon = Icons.Outlined.Error,
                    type = ToastType.Error
                )
            )
            isLoading(false)
        }
    }

    fun logout() {
        authToken.value = null
        authTokenManager.deleteToken()
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
        if (!authToken.value.isNullOrEmpty()) {
            user.value = api.userMe().data
        }
        return user.value
    }

    suspend fun getYears(): List<Year>? {
        try {
            return api.yearIndex().data
        } catch (e: Exception) {
            e.printStackTrace()
            couldNotReachBesteSchule()
            return null
        }
    }

    suspend fun getCollections(years: List<Year>? = null): List<GradeCollection>? {
        try {
            val includes = listOf("grades", "interval", "grades.histories")
            val collections = mutableStateListOf<GradeCollection>()
            collections.clear()
            if (years.isNullOrEmpty()) {
                val collection = api.collectionsIndex(include = includes)
                collections.addAll(collection.data)
                if (collection.meta?.lastPage!! > 1) {
                    for (i in 2..collection.meta.lastPage) {
                        collections.addAll(api.collectionsIndex(include = includes, page = i).data)
                    }
                }
            } else {
                years.forEach {
                    val collection = api.collectionsIndex(include = includes, filterYear = it.id.toString())
                    collections.addAll(collection.data)
                    if (collection.meta?.lastPage!! > 1) {
                        for (i in 2..collection.meta.lastPage) {
                            collections.addAll(api.collectionsIndex(include = includes, filterYear = it.id.toString(), page = i).data)
                        }
                    }
                }
            }
            return collections
        } catch (e: Exception) {
            e.printStackTrace()
            couldNotReachBesteSchule()
            return null
        }
    }

    suspend fun getFinalGrades(): List<Finalgrade>? {
        try {
            return api.finalgradesIndex().data
        } catch (e: Exception) {
            e.printStackTrace()
            couldNotReachBesteSchule()
            return null
        }
    }

    suspend fun getSubjects(): List<Subject>? {
        try {
            return api.subjectsIndex().data
        } catch (e: Exception) {
            e.printStackTrace()
            couldNotReachBesteSchule()
            return null
        }
    }

    init {
        viewModelScope.launch {
            try {
                authToken.value = authTokenManager.getToken()
                init()
            } catch (e: Exception) {
                e.printStackTrace()
                toaster.show(
                    Toast(
                        message = "Fehler bei der Initialisierung",
                        icon = Icons.Outlined.Error,
                        type = ToastType.Error
                    )
                )
            }
        }
    }
}


