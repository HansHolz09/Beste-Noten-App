package com.hansholz.bestenotenapp.api

import kotlinx.browser.window

actual object PlatformOidcConstants : OidcConstants {
    override val clientId: String = "140"
    override val redirectUrl: String = window.location.origin + window.location.pathname
}
