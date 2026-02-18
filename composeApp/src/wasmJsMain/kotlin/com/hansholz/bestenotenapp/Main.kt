package com.hansholz.bestenotenapp

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.security.kSafe
import com.hansholz.bestenotenapp.theme.FontFamilies
import io.ktor.http.Url
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.appsupport.PlatformCodeAuthFlow

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalBrowserHistoryApi::class,
    ExperimentalResourceApi::class,
    ExperimentalOpenIdConnect::class,
)
fun main() {
    ComposeViewport(document.body!!) {
        FontFamilies.allFontResources().forEach {
            preloadFont(it)
        }
        if (!Url(window.location.href).parameters.isEmpty()) {
            LaunchedEffect(Unit) {
                PlatformCodeAuthFlow.handleRedirect()
            }
        } else {
            var cacheReady by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                kSafe.awaitCacheReady()
                cacheReady = true
            }
            if (cacheReady) {
                App {
                    it.bindToBrowserNavigation()
                }
            }
        }
    }
}
