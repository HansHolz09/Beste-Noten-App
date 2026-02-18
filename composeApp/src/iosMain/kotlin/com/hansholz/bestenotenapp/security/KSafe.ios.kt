package com.hansholz.bestenotenapp.security

import eu.anifantakis.lib.ksafe.KSafe

val kSafe = KSafe()

actual fun kSafe(): KSafe = kSafe
