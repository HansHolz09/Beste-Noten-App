package com.hansholz.bestenotenapp.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

actual fun createHttpClient(forLogin: Boolean): HttpClient =
    HttpClient(Js) {
        commonHttpClientConfig(this, forLogin)
    }
