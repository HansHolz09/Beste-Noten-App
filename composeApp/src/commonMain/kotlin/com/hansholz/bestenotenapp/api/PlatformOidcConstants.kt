package com.hansholz.bestenotenapp.api

interface OidcConstants {
    val clientId: String
    val redirectUrl: String
}

expect object PlatformOidcConstants : OidcConstants {
    override val clientId: String
    override val redirectUrl: String
}
