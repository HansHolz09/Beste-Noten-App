package com.hansholz.bestenotenapp.security

import kotlinx.browser.localStorage

class JsLocalCredentialsStorage : CredentialsStorage {
    override fun getString(key: String): String? =
        localStorage.getItem(key)?.takeIf { it.isNotEmpty() }

    override fun getInt(key: String): Int? = getString(key)?.toIntOrNull()
    override fun getLong(key: String): Long? = getString(key)?.toLongOrNull()
    override fun getBoolean(key: String): Boolean? = getString(key)?.toBooleanStrictOrNull()

    override fun setString(key: String, value: String) {
        localStorage.setItem(key, value)
    }
    override fun setInt(key: String, value: Int) = setString(key, value.toString())
    override fun setLong(key: String, value: Long) = setString(key, value.toString())
    override fun setBoolean(key: String, value: Boolean) = setString(key, value.toString())
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class CredentialsStorageFactory {
    actual fun create(): CredentialsStorage = JsLocalCredentialsStorage()
}
