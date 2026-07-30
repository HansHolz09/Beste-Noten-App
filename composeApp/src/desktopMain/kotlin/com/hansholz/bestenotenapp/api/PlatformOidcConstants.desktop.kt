package com.hansholz.bestenotenapp.api

import bestenotenapp.composeApp.BuildConfig

actual object PlatformOidcConstants : OidcConstants {
    actual override val clientId: String = "138"
    actual override val redirectUrl: String = "http://127.0.0.1:8080/callback"
    actual override val googleClientId: String = BuildConfig.GOOGLE_CLIENT_ID_DESKTOP
    actual override val googleClientSecret: String = BuildConfig.GOOGLE_CLIENT_SECRET_DESKTOP
    actual override val googleRedirectUrl: String = redirectUrl
}
