package com.hansholz.bestenotenapp.main

enum class Platform {
    ANDROID,
    IOS,
    DESKTOP,
    WEB,
}

enum class ExactPlatform {
    WINDOWS,
    MACOS,
    LINUX,
    ANDROID,
    IOS,
    IPADOS,
    VISIONOS,
    WEBWASM,
    UNKNOWN,
}

fun isApplePlatform() =
    listOf(
        ExactPlatform.MACOS,
        ExactPlatform.IOS,
        ExactPlatform.IPADOS,
        ExactPlatform.VISIONOS,
    ).contains(getExactPlatform())

expect fun getPlatform(): Platform

expect fun getExactPlatform(): ExactPlatform

expect fun getPlatformVersion(): String?
