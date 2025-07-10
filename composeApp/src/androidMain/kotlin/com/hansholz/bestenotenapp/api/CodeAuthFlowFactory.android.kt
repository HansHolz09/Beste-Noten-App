package com.hansholz.bestenotenapp.api

import android.annotation.SuppressLint
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory

@SuppressLint("StaticFieldLeak")
val androidCodeAuthFlowFactory = AndroidCodeAuthFlowFactory(useWebView = true)

actual val codeAuthFlowFactory: CodeAuthFlowFactory
    get() = androidCodeAuthFlowFactory