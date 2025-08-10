package com.hansholz.bestenotenapp.utils

import android.content.Context
import androidx.activity.ComponentActivity
import java.lang.ref.WeakReference

object AndroidContext {
    var context: WeakReference<Context?> = WeakReference(null)

    fun init(activity: ComponentActivity) {
        context = WeakReference(activity.applicationContext)
    }
    fun init(context: Context) {
        this.context = WeakReference(context)
    }
}