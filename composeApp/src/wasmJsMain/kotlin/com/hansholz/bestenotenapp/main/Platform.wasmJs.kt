package com.hansholz.bestenotenapp.main

actual fun isStoreDistribution(): Boolean = false

actual fun getPlatform(): Platform = Platform.WEB

actual fun getExactPlatform(): ExactPlatform = ExactPlatform.WEBWASM

actual fun getPlatformVersion(): String? = "unknown"
