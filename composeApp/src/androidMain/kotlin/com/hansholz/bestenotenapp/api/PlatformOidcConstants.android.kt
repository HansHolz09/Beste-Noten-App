package com.hansholz.bestenotenapp.api

import bestenotenapp.composeApp.BuildConfig
import com.hansholz.bestenotenapp.main.isStoreDistribution

actual object PlatformOidcConstants : OidcConstants {
    actual override val clientId: String = "141"
    actual override val redirectUrl: String = if (isStoreDistribution()) "dev.hansholz.bestenotenapp://callback" else "bestenotenapp://callback"
    actual override val googleClientId: String = if (isStoreDistribution()) BuildConfig.GOOGLE_CLIENT_ID_ANDROID_PLAY else BuildConfig.GOOGLE_CLIENT_ID_ANDROID
    actual override val googleClientSecret: String = ""
    actual override val googleRedirectUrl: String = if (isStoreDistribution()) "dev.hansholz.bestenotenapp:/callback" else "com.hansholz.bestenotenapp:/callback"
}
