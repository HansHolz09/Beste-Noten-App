package com.hansholz.bestenotenapp.api

import com.hansholz.bestenotenapp.utils.CredentialsStorageFactory

class AuthTokenManager() {
    private val credentialsStorage = CredentialsStorageFactory().create()

    fun saveToken(token: String) {
        credentialsStorage.setString(KEY, token)
    }
    fun getToken(): String? {
        val token = credentialsStorage.getString(KEY)
        return if (token == "null") null else token
    }
    fun deleteToken() {
        credentialsStorage.setString(KEY, "null")
    }

    companion object {
        const val KEY = "authToken"
    }
}