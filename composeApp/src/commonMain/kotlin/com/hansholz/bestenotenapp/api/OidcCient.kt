package com.hansholz.bestenotenapp.api

import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.types.CodeChallengeMethod

val oidcClient =
    OpenIdConnectClient {
        endpoints {
            tokenEndpoint = "https://beste.schule/oauth/token"
            authorizationEndpoint = "https://beste.schule/oauth/authorize"
            userInfoEndpoint = null
            endSessionEndpoint = null
        }

        clientId = PlatformOidcConstants.clientId
        clientSecret = null
        scope = null
        codeChallengeMethod = CodeChallengeMethod.S256
        redirectUri = PlatformOidcConstants.redirectUrl
        postLogoutRedirectUri = null

        disableNonce = true
    }
