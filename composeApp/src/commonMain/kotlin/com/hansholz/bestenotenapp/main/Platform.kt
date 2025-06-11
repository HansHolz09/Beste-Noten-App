package com.hansholz.bestenotenapp.main

enum class Platform {
    ANDROID,
    IOS,
    DESKTOP,
}

enum class ExactPlatform {
    WINDOWS,
    MACOS,
    LINUX,
    ANDROID,
    IOS,
    IPADOS,
    VISIONOS,
    UNKNOWN,
}

expect fun getPlatform(): Platform

expect fun getExactPlatform(): ExactPlatform

expect fun getPlatformVersion(): String?