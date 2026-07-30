package com.hansholz.bestenotenapp.api

interface OidcConstants {
    val clientId: String
    val redirectUrl: String
    val googleClientId: String
    val googleClientSecret: String
    val googleRedirectUrl: String
}

expect object PlatformOidcConstants : OidcConstants {
    override val clientId: String
    override val redirectUrl: String
    override val googleClientId: String
    override val googleClientSecret: String
    override val googleRedirectUrl: String
}
