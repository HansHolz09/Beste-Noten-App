package com.hansholz.bestenotenapp.api

import bestenotenapp.composeApp.BuildConfig

actual object PlatformOidcConstants : OidcConstants {
    actual override val clientId: String = "141"
    actual override val redirectUrl: String = "bestenotenapp://callback"
    actual override val googleClientId: String = BuildConfig.GOOGLE_CLIENT_ID_ANDROID
    actual override val googleClientSecret: String = ""
    actual override val googleRedirectUrl: String = "com.hansholz.bestenotenapp:/callback"
}
