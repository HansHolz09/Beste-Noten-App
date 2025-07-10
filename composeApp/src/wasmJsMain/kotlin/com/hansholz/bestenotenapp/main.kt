package com.hansholz.bestenotenapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.theme.FontFamilies
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class, ExperimentalResourceApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        FontFamilies.allFontResources().forEach {
            preloadFont(it)
        }
        App {
            window.bindToNavigation(it)
        }
    }
}