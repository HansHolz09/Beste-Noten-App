package com.hansholz.bestenotenapp.api

import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory

actual val codeAuthFlowFactory: CodeAuthFlowFactory
    get() = AndroidCodeAuthFlowFactory(useWebView = true)