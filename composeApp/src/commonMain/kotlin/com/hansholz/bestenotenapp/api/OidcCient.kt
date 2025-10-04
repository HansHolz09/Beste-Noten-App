package com.hansholz.bestenotenapp.api

import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
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

        clientId =
            if (getPlatform() == Platform.DESKTOP) {
                "138"
            } else if (getPlatform() == Platform.WEB) {
                "140"
            } else {
                "141"
            }
        clientSecret = null
        scope = null
        codeChallengeMethod = CodeChallengeMethod.S256
        redirectUri =
            if (getPlatform() == Platform.DESKTOP) {
                "http://127.0.0.1:8080/callback"
            } else if (getPlatform() == Platform.WEB) {
                ""
            } else {
                "bestenotenapp://callback"
            }
        postLogoutRedirectUri = null

        disableNonce = true
    }
