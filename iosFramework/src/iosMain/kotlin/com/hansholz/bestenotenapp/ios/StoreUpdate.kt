package com.hansholz.bestenotenapp.ios

import com.hansholz.bestenotenapp.updates.setStoreUpdatePrompt as appSetStoreUpdatePrompt

public fun setStoreUpdatePrompt(prompt: (String) -> Unit): Unit = appSetStoreUpdatePrompt(prompt)
