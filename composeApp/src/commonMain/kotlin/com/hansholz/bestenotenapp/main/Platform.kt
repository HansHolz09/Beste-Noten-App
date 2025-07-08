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

expect fun getPlatform(): Platform

expect fun getExactPlatform(): ExactPlatform

expect fun getPlatformVersion(): String?