package com.hansholz.bestenotenapp.api

actual object PlatformOidcConstants : OidcConstants {
    actual override val clientId: String = "141"
    actual override val redirectUrl: String = "bestenotenapp://callback"
}
