package com.hansholz.bestenotenapp.updates

import dev.nucleusframework.core.runtime.ExecutableRuntime
import dev.nucleusframework.core.runtime.ExecutableType
import org.jetbrains.skiko.hostOs

internal actual fun updateAssetTarget(): UpdateAssetTarget? {
    val platform =
        when {
            hostOs.isWindows -> "windows"
            hostOs.isMacOS -> "macos"
            hostOs.isLinux -> "linux"
            else -> return null
        }
    val architecture =
        when (System.getProperty("os.arch").lowercase()) {
            "aarch64", "arm64" -> "arm64"
            "amd64", "x86_64", "x64" -> "x64"
            else -> return null
        }

    return UpdateAssetTarget(
        platform = platform,
        architecture = architecture,
        extension = ExecutableRuntime.type().assetExtension(platform),
    )
}

private fun ExecutableType.assetExtension(platform: String): String {
    val defaultExtension =
        when (platform) {
            "windows" -> ".exe"
            "macos" -> ".dmg"
            else -> ".deb"
        }
    return when (this) {
        ExecutableType.EXE, ExecutableType.NSIS, ExecutableType.NSIS_WEB, ExecutableType.PORTABLE -> ".exe"
        ExecutableType.MSI -> ".msi"
        ExecutableType.APPX -> ".appx"
        ExecutableType.DMG -> ".dmg"
        ExecutableType.PKG -> ".pkg"
        ExecutableType.DEB -> ".deb"
        ExecutableType.RPM -> ".rpm"
        ExecutableType.SNAP -> ".snap"
        ExecutableType.FLATPAK -> ".flatpak"
        ExecutableType.APPIMAGE -> ".appimage"
        ExecutableType.PACMAN -> ".pkg.tar.zst"
        ExecutableType.ZIP -> ".zip"
        ExecutableType.TAR -> ".tar.gz"
        ExecutableType.SEVEN_Z -> ".7z"
        ExecutableType.DEV -> defaultExtension
    }
}
