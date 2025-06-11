package com.hansholz.bestenotenapp.main

import android.os.Build

actual fun getPlatform(): Platform = Platform.ANDROID

actual fun getExactPlatform(): ExactPlatform = ExactPlatform.ANDROID

actual fun getPlatformVersion(): String? = Build.VERSION.SDK_INT.toString()
