package com.hansholz.bestenotenapp.api

import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.getExactPlatform
import com.hansholz.bestenotenapp.utils.CredentialsStorageFactory

class AuthTokenManager() {
    private val credentialsStorage = try {
        if (getExactPlatform() != ExactPlatform.LINUX) {
            CredentialsStorageFactory().create()
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    val isAvailable = credentialsStorage != null

    fun saveToken(token: String) {
        if (isAvailable) {
            credentialsStorage?.setString(KEY, token)
        }
    }
    fun getToken(): String? {
        if (isAvailable) {
            val token = credentialsStorage?.getString(KEY)
            return if (token == "null") null else token
        } else {
            return null
        }
    }
    fun deleteToken() {
        if (isAvailable) {
            credentialsStorage?.setString(KEY, "null")
        }
    }

    companion object {
        const val KEY = "authToken"
    }
}