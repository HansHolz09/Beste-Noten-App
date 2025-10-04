package com.hansholz.bestenotenapp.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClient() =
    HttpClient(Darwin) {
        commonHttpClientConfig(this)
    }
