package com.hansholz.bestenotenapp.security

import eu.anifantakis.lib.ksafe.KSafe
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.context

class KSafe {
    val kSafe = KSafe(FileKit.context)
}

actual fun kSafe(): KSafe = KSafe().kSafe
