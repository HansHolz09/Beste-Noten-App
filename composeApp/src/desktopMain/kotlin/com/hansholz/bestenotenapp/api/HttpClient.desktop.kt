package com.hansholz.bestenotenapp.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

actual fun createHttpClient() =
    HttpClient(CIO) {
        commonHttpClientConfig(this)
    }
