package com.hansholz.bestenotenapp.api

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.cache.*

actual fun createHttpClient() =
    HttpClient(Darwin) {
        commonHttpClientConfig(this)
        install(HttpCache)
    }
