package com.hansholz.bestenotenapp.api

import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.appsupport.JvmCodeAuthFlowFactory

@OptIn(ExperimentalOpenIdConnect::class)
actual val codeAuthFlowFactory: CodeAuthFlowFactory
    get() = JvmCodeAuthFlowFactory()
