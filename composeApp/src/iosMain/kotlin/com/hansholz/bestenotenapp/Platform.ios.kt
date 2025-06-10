package com.hansholz.bestenotenapp

import platform.UIKit.UIDevice

actual fun getPlatform(): Platform = Platform.IOS

actual fun getExactPlatform(): ExactPlatform =
    when (UIDevice.currentDevice.systemName) {
        "iOS" -> ExactPlatform.IOS
        "iPadOS" -> ExactPlatform.IPADOS
        "visionOS" -> ExactPlatform.VISIONOS
        else -> ExactPlatform.UNKNOWN
    }

actual fun getPlatformVersion(): String? = UIDevice.currentDevice.systemVersion
