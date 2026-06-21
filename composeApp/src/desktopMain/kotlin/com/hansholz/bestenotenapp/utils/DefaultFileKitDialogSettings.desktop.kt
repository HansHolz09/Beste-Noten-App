package com.hansholz.bestenotenapp.utils

import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitMacOSSettings

actual fun defaultFileKitDialogSettings(title: String?): FileKitDialogSettings =
    FileKitDialogSettings(
        title = title,
        macOS = FileKitMacOSSettings(canCreateDirectories = true),
    )
