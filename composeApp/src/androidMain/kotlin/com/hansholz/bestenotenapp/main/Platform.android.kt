package com.hansholz.bestenotenapp.main

import android.os.Build
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.context

actual fun isStoreDistribution(): Boolean = FileKit.context.packageName == "dev.hansholz.bestenotenapp"

actual fun getPlatform(): Platform = Platform.ANDROID

actual fun getExactPlatform(): ExactPlatform = ExactPlatform.ANDROID

actual fun getPlatformVersion(): String? = Build.VERSION.SDK_INT.toString()
