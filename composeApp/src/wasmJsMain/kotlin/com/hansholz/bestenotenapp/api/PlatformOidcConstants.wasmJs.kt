package com.hansholz.bestenotenapp.api

import kotlinx.browser.window

actual object PlatformOidcConstants : OidcConstants {
    actual override val clientId: String = "140"
    actual override val redirectUrl: String = window.location.origin + window.location.pathname
}
