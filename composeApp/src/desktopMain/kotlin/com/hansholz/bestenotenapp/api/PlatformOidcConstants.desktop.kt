package com.hansholz.bestenotenapp.api

actual object PlatformOidcConstants : OidcConstants {
    actual override val clientId: String = "138"
    actual override val redirectUrl: String = "http://127.0.0.1:8080/callback"
}
