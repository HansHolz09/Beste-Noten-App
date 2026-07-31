package com.hansholz.bestenotenapp.homework

import com.hansholz.bestenotenapp.api.PlatformOidcConstants
import com.hansholz.bestenotenapp.api.codeAuthFlowFactory
import com.hansholz.bestenotenapp.security.kSafeProvider
import eu.anifantakis.lib.ksafe.KSafe
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.types.CodeChallengeMethod
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse
import kotlin.time.Clock

interface GoogleAuthProvider {
    suspend fun signIn()

    suspend fun getAccessToken(): String?

    suspend fun signOut()
}

class MissingGoogleAuthException(
    message: String = "Google-Kalender ist nicht verbunden.",
) : IllegalStateException(message)

class KSafeGoogleAuthProvider(
    private val kSafe: KSafe,
    private val factory: CodeAuthFlowFactory = codeAuthFlowFactory,
) : GoogleAuthProvider {
    private val scopes =
        "https://www.googleapis.com/auth/calendar.app.created " +
            "https://www.googleapis.com/auth/calendar.calendarlist.readonly"
    private val googleClient =
        OpenIdConnectClient {
            endpoints {
                authorizationEndpoint = "https://accounts.google.com/o/oauth2/v2/auth"
                tokenEndpoint = "https://oauth2.googleapis.com/token"
            }

            clientId = PlatformOidcConstants.googleClientId
            clientSecret = PlatformOidcConstants.googleClientSecret.ifBlank { null }
            scope = scopes
            codeChallengeMethod = CodeChallengeMethod.S256
            redirectUri = PlatformOidcConstants.googleRedirectUrl

            disableNonce = true
        }

    override suspend fun signIn() {
        val flow = factory.createAuthFlow(googleClient)
        return try {
            val response =
                flow.getAccessToken(
                    configureAuthUrl = {
                        parameters.append("access_type", "offline")
                        parameters.append("prompt", "consent")
                        parameters.append("include_granted_scopes", "true")
                    },
                    configureTokenExchange = null,
                )
            if (response.scope != null && scopes.all { response.scope!!.contains(it) }) {
                save(response)
            } else {
                error("Nicht alle benötigten Berechtigungen erteilt.")
            }
        } catch (e: Exception) {
            throw MissingGoogleAuthException("Google-Kalender konnte nicht verbunden werden: ${e.message}")
        }
    }

    override suspend fun getAccessToken(): String? {
        val accessToken = kSafeProvider(kSafe) { get("homeworkGoogleAccessToken", "") }.takeIf { it.isNotBlank() }
        val expiresAt = kSafeProvider(kSafe) { get("homeworkGoogleAccessTokenExpiresAt", 0L) }
        if (accessToken != null && Clock.System.now().epochSeconds < expiresAt - 60) {
            return accessToken
        }

        val refreshToken = kSafeProvider(kSafe) { get("homeworkGoogleRefreshToken", "") }.takeIf { it.isNotBlank() } ?: return null
        return try {
            val response = googleClient.refreshToken(refreshToken)
            save(response, refreshToken)
            response.access_token
        } catch (_: Exception) {
            null
        }
    }

    private fun save(
        response: AccessTokenResponse,
        previousRefreshToken: String? = null,
    ) {
        kSafeProvider(kSafe) {
            putSecure("homeworkGoogleAccessToken", response.access_token)
            putSecure("homeworkGoogleRefreshToken", response.refresh_token ?: previousRefreshToken.orEmpty())
            put("homeworkGoogleAccessTokenExpiresAt", Clock.System.now().epochSeconds + (response.expires_in ?: 3600))
        }
    }

    override suspend fun signOut() {
        listOf(
            "homeworkGoogleAccessToken",
            "homeworkGoogleRefreshToken",
            "homeworkGoogleAccessTokenExpiresAt",
        ).forEach { kSafe.deleteDirect(it) }
    }
}
