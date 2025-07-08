package com.hansholz.bestenotenapp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.hansholz.bestenotenapp.main.App
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class, ExperimentalSharedTransitionApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        App()
    }
}