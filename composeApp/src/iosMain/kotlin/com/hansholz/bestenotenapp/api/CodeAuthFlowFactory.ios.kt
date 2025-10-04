package com.hansholz.bestenotenapp.api

import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.appsupport.IosCodeAuthFlowFactory

actual val codeAuthFlowFactory: CodeAuthFlowFactory
    get() = IosCodeAuthFlowFactory()
