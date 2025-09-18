package com.hansholz.bestenotenapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.hansholz.bestenotenapp.notifications.ensureWebNotificationsInitialized
import kotlinx.browser.document
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class, ExperimentalResourceApi::class)
fun main() {
    ensureWebNotificationsInitialized()
    ComposeViewport(document.body!!) {
        FontFamilies.allFontResources().forEach {
            preloadFont(it)
        }
        App {
            it.bindToBrowserNavigation()
        }
    }
}