package com.hansholz.bestenotenapp.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5

actual fun createHttpClient() =
    HttpClient(Apache5) {
        commonHttpClientConfig(this)
    }
