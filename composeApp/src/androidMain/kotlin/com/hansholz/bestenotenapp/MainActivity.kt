package com.hansholz.bestenotenapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.hansholz.bestenotenapp.api.androidCodeAuthFlowFactory
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.utils.AndroidContext
import com.hansholz.bestenotenapp.notifications.GradeNotifications

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AndroidContext.init(this)
        GradeNotifications.initialize(this)
        androidCodeAuthFlowFactory.registerActivity(this)
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}