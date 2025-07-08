package com.hansholz.bestenotenapp.api

import io.ktor.client.*
import io.ktor.client.engine.js.*

actual fun createHttpClient(): HttpClient =
    HttpClient(Js) {
        commonHttpClientConfig(this)
    }