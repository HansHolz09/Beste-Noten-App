package com.hansholz.bestenotenapp.api

interface OidcConstants {
    val clientId: String
    val redirectUrl: String
}

expect object PlatformOidcConstants : OidcConstants
