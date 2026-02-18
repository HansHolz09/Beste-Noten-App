package com.hansholz.bestenotenapp.security

import com.hansholz.bestenotenapp.utils.AndroidContext
import eu.anifantakis.lib.ksafe.KSafe

class KSafe {
    val kSafe = KSafe(AndroidContext.context.get()!!)
}

actual fun kSafe(): KSafe = KSafe().kSafe
