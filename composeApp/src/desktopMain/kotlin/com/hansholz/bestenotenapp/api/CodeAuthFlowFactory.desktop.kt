package com.hansholz.bestenotenapp.api

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.appsupport.JvmCodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.appsupport.webserver.SimpleKtorWebserver

@OptIn(ExperimentalOpenIdConnect::class)
actual val codeAuthFlowFactory: CodeAuthFlowFactory
    get() =
        JvmCodeAuthFlowFactory(
            webserverProvider = {
                SimpleKtorWebserver(
                    createResponse = {
                        call.respondText(
                            status = HttpStatusCode.OK,
                            text = SUCCESS_PAGE.trimIndent(),
                            contentType = ContentType.parse("text/html"),
                        )
                    },
                )
            },
        )

private const val SUCCESS_PAGE = """
<!DOCTYPE html>
<html lang="de">
  <head>
    <meta charset="utf-8">
    <meta name="color-scheme" content="light dark">

    <title>Anmeldung erfolgreich</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Keania+One&family=Sniglet:wght@400" rel="stylesheet">

    <script src="https://unpkg.com/@lottiefiles/dotlottie-wc@0.9.4/dist/dotlottie-wc.js" type="module"></script>
  </head>
  <body align="center">
    <img src="https://github.com/HansHolz09/Beste-Noten-App/blob/main/composeApp/src/commonMain/composeResources/drawable/logo.png?raw=true" alt="Beste-Noten-App Logo" style="width: 100px; height: auto; margin-top: 30px;">

    <h1 style="font-family: 'Keania One'">Anmeldung erfolgreich</h1>
    <h2 style="font-family: 'Sniglet'">Du kannst nun zur Beste-Noten-App zurückkehren</h2>
    
    <dotlottie-wc 
      src="https://lottie.host/2f719737-23a1-4bad-bf2b-ec6b6f870f2e/GVmGrHCwT5.lottie" 
      autoplay
      loop
      style="width: 200px; height: 200px; display: block; margin: 0 auto; margin-top: 40px;">
    </dotlottie-wc>
  </body>
</html>
"""
