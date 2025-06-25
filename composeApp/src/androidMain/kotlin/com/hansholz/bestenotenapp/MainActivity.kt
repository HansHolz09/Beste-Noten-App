package com.hansholz.bestenotenapp

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsetsController.APPEARANCE_LIGHT_CAPTION_BARS
import android.view.WindowInsetsController.APPEARANCE_TRANSPARENT_CAPTION_BAR_BACKGROUND
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hansholz.bestenotenapp.main.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(
                isDark = { isDark ->
                    if (window is Window && Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                        window.insetsController?.setSystemBarsAppearance(
                            APPEARANCE_TRANSPARENT_CAPTION_BAR_BACKGROUND,
                            APPEARANCE_TRANSPARENT_CAPTION_BAR_BACKGROUND
                        )
                        val lightIcons = if (isDark) 0 else APPEARANCE_LIGHT_CAPTION_BARS
                        window.insetsController?.setSystemBarsAppearance(
                            lightIcons,
                            APPEARANCE_LIGHT_CAPTION_BARS
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}