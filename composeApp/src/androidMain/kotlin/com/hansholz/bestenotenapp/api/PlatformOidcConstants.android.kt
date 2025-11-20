package com.hansholz.bestenotenapp.api

actual object PlatformOidcConstants : OidcConstants {
    override val clientId: String = "141"
    override val redirectUrl: String = "bestenotenapp://callback"
}
