package com.hansholz.bestenotenapp.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual fun createHttpClient() =
    HttpClient(CIO) {
        commonHttpClientConfig(this)
    }