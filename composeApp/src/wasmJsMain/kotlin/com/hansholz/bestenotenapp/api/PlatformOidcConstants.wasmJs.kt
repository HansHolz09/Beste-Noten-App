package com.hansholz.bestenotenapp.api

import bestenotenapp.composeApp.BuildConfig
import kotlinx.browser.window

actual object PlatformOidcConstants : OidcConstants {
    actual override val clientId: String = "140"
    actual override val redirectUrl: String = window.location.origin + window.location.pathname
    actual override val googleClientId: String = BuildConfig.GOOGLE_CLIENT_ID_WEB
    actual override val googleClientSecret: String = BuildConfig.GOOGLE_CLIENT_SECRET_WEB
    actual override val googleRedirectUrl: String = redirectUrl
}
