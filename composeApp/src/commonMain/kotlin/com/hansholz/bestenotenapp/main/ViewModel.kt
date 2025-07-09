@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.window.core.layout.WindowWidthSizeClass
import com.hansholz.bestenotenapp.api.AuthTokenManager
import com.hansholz.bestenotenapp.api.BesteSchuleApi
import com.hansholz.bestenotenapp.api.Finalgrade
import com.hansholz.bestenotenapp.api.GradeCollection
import com.hansholz.bestenotenapp.api.Subject
import com.hansholz.bestenotenapp.api.User
import com.hansholz.bestenotenapp.api.Year
import com.hansholz.bestenotenapp.api.codeAuthFlowFactory
import com.hansholz.bestenotenapp.api.createHttpClient
import com.hansholz.bestenotenapp.api.oidcClient
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.publicvalue.multiplatform.oidc.DefaultOpenIdConnectClient
import org.publicvalue.multiplatform.oidc.OpenIdConnectException

class ViewModel : ViewModel() {
    private val httpClient = createHttpClient()

    val authTokenManager = AuthTokenManager()
    val authToken = mutableStateOf<String?>(null)
    private val authFlow = codeAuthFlowFactory.createAuthFlow(DefaultOpenIdConnectClient(httpClient, oidcClient.config))


    val api = BesteSchuleApi(httpClient, authToken)

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

    fun stayLoggedIn(token: String? = null) {
        authTokenManager.saveToken(token ?: authToken.value!!)
    }

    fun logout() {
        authToken.value = null
        authTokenManager.deleteToken()
    }

    suspend fun init() {
        if (!authToken.value.isNullOrEmpty()) {
            user.value = api.userMe().data
        }
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

    suspend fun getCollections(years: List<Year>? = null): List<GradeCollection> {
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
    }

    init {
        viewModelScope.launch {
            authToken.value = authTokenManager.getToken()
            init()
        }
    }
}


