package com.hansholz.bestenotenapp.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual fun createHttpClient() =
    HttpClient(OkHttp) {
        commonHttpClientConfig(this)
    }
