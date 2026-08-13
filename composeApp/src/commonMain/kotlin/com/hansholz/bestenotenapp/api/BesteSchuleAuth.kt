package com.hansholz.bestenotenapp.api

import androidx.compose.runtime.MutableState
import com.hansholz.bestenotenapp.security.kSafeProvider
import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse
import kotlin.time.Clock

class BesteSchuleAuth(
    private val client: OpenIdConnectClient,
    private val kSafe: KSafe,
    private val authTokenState: MutableState<String?>,
) {
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var accessTokenExpiresAt = 0L
    private var isPersisted = false
    private var retryRefreshAt = 0L

    fun restore() =
        kSafeProvider(kSafe) {
            accessToken = get<String?>(ACCESS_TOKEN_KEY, null)
            refreshToken = get<String?>(REFRESH_TOKEN_KEY, null)
            accessTokenExpiresAt = get(ACCESS_TOKEN_EXPIRES_AT_KEY, 0L)
            isPersisted = !accessToken.isNullOrBlank()
            authTokenState.value = accessToken
        }

    fun setTokenResponse(response: AccessTokenResponse) {
        isPersisted = false
        updateFromResponse(response, response.refresh_token)
    }

    fun persist() {
        val token = accessToken ?: return
        kSafeProvider(kSafe) {
            putSecure(ACCESS_TOKEN_KEY, token)
            refreshToken?.let { putSecure(REFRESH_TOKEN_KEY, it) } ?: kSafe.deleteDirect(REFRESH_TOKEN_KEY)
            put(ACCESS_TOKEN_EXPIRES_AT_KEY, accessTokenExpiresAt)
        }
        isPersisted = true
    }

    fun clear() {
        accessToken = null
        refreshToken = null
        accessTokenExpiresAt = 0L
        isPersisted = false
        retryRefreshAt = 0L
        authTokenState.value = null
        listOf(ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY, ACCESS_TOKEN_EXPIRES_AT_KEY).forEach(kSafe::deleteDirect)
    }

    suspend fun getValidAccessToken(): String? {
        adoptDirectlyAssignedToken()
        val now = Clock.System.now().epochSeconds
        if (!shouldRefresh(now)) return accessToken

        return refreshMutex.withLock {
            adoptDirectlyAssignedToken()
            if (isPersisted) adoptNewerStoredToken()

            val lockedNow = Clock.System.now().epochSeconds
            if (!shouldRefresh(lockedNow) || lockedNow < retryRefreshAt) return@withLock accessToken

            val tokenForRefresh = refreshToken ?: return@withLock accessToken
            try {
                val response = client.refreshToken(tokenForRefresh)
                updateFromResponse(response, response.refresh_token ?: tokenForRefresh)
                if (isPersisted) persist()
                accessToken
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                retryRefreshAt = lockedNow + REFRESH_RETRY_DELAY_SECONDS
                accessToken
            }
        }
    }

    private fun shouldRefresh(now: Long): Boolean =
        !refreshToken.isNullOrBlank() &&
            accessTokenExpiresAt > 0L &&
            now >= accessTokenExpiresAt - REFRESH_EARLY_SECONDS

    private fun updateFromResponse(
        response: AccessTokenResponse,
        effectiveRefreshToken: String?,
    ) {
        accessToken = response.access_token
        refreshToken = effectiveRefreshToken?.takeIf { it.isNotBlank() }
        accessTokenExpiresAt =
            response.expires_in
                ?.let { response.received_at + it }
                ?: 0L
        retryRefreshAt = 0L
        authTokenState.value = accessToken
    }

    private fun adoptDirectlyAssignedToken() {
        if (authTokenState.value == accessToken) return
        accessToken = authTokenState.value?.takeIf { it.isNotBlank() }
        refreshToken = null
        accessTokenExpiresAt = 0L
        isPersisted = false
        retryRefreshAt = 0L
    }

    private fun adoptNewerStoredToken() =
        kSafeProvider(kSafe) {
            val storedAccessToken = get<String?>(ACCESS_TOKEN_KEY, null)
            val storedExpiresAt = get(ACCESS_TOKEN_EXPIRES_AT_KEY, 0L)
            if (!storedAccessToken.isNullOrBlank() && storedExpiresAt > accessTokenExpiresAt) {
                accessToken = storedAccessToken
                refreshToken = get<String?>(REFRESH_TOKEN_KEY, null)
                accessTokenExpiresAt = storedExpiresAt
                authTokenState.value = storedAccessToken
                retryRefreshAt = 0L
            }
        }

    companion object {
        const val ACCESS_TOKEN_KEY = "authToken"
        const val REFRESH_TOKEN_KEY = "authRefreshToken"
        const val ACCESS_TOKEN_EXPIRES_AT_KEY = "authTokenExpiresAt"

        private const val REFRESH_EARLY_SECONDS = 60L
        private const val REFRESH_RETRY_DELAY_SECONDS = 30L
        private val refreshMutex = Mutex()
    }
}
