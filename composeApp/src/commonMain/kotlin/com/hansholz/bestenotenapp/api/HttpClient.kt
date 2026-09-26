package com.hansholz.bestenotenapp.api

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun createHttpClient(forLogin: Boolean = false): HttpClient

fun commonHttpClientConfig(
    config: HttpClientConfig<*>,
    forLogin: Boolean = false,
): HttpClientConfig<*> {
    config.apply {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                },
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 10000
        }
        if (forLogin) {
            followRedirects = false
            install(HttpCookies)
        } else {
            install(HttpCache)
        }
    }
    return config
}
