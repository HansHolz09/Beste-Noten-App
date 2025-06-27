package com.hansholz.bestenotenapp.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun createHttpClient(): HttpClient

fun commonHttpClientConfig(config: HttpClientConfig<*>): HttpClientConfig<*> {
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
            requestTimeoutMillis = 600000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 600000
        }
        install(HttpCache)
    }
    return config
}