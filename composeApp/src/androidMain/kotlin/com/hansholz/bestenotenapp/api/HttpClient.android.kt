package com.hansholz.bestenotenapp.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual fun createHttpClient(forLogin: Boolean) =
    HttpClient(OkHttp) {
        commonHttpClientConfig(this, forLogin)
    }
