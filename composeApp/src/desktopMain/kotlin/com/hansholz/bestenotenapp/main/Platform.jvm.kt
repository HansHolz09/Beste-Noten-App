package com.hansholz.bestenotenapp.main

import org.jetbrains.skiko.hostOs

actual fun getPlatform(): Platform = Platform.DESKTOP

actual fun getExactPlatform(): ExactPlatform =
    when {
        hostOs.isWindows -> ExactPlatform.WINDOWS
        hostOs.isMacOS -> ExactPlatform.MACOS
        hostOs.isLinux -> ExactPlatform.LINUX
        else -> ExactPlatform.UNKNOWN
    }

actual fun getPlatformVersion(): String? = System.getProperty("os.version")