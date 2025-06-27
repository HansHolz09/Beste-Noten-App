package com.hansholz.bestenotenapp

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.main.LocalNavigationDrawerTopPadding
import com.hansholz.bestenotenapp.utils.isInWindowMode

fun MainViewController() = ComposeUIViewController {
    CompositionLocalProvider(
        LocalNavigationDrawerTopPadding provides if (isInWindowMode()) 50.dp else null
    ) {
        App()
    }
}