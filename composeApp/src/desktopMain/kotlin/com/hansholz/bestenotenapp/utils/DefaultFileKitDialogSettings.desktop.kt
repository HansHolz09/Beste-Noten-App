package com.hansholz.bestenotenapp.utils

import io.github.vinceglb.filekit.dialogs.FileKitDialogParent
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitMacOSSettings
import org.jetbrains.skiko.hostOs

actual fun defaultFileKitDialogSettings(title: String?): FileKitDialogSettings =
    FileKitDialogSettings(
        title = title,
        parent = if (hostOs.isWindows) FileKitDialogParent.windows(FileKitWindowObject.hwnd) else null,
        macOS = FileKitMacOSSettings(canCreateDirectories = true),
    )

object FileKitWindowObject {
    var hwnd: Long = 0L

    fun setHandle(value: Long) {
        require(value != 0L) { "No handle given" }
        hwnd = value
    }
}
