package com.hansholz.bestenotenapp.utils

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings

@OptIn(ExperimentalSettingsImplementation::class)
class IosKeychainCredentialsStorage : CredentialsStorage {
    override fun getString(key: String): String? = KeychainSettings().getStringOrNull(key)
    override fun getInt(key: String): Int? = KeychainSettings().getIntOrNull(key)
    override fun getLong(key: String): Long? = KeychainSettings().getLongOrNull(key)
    override fun getBoolean(key: String): Boolean? = KeychainSettings().getBooleanOrNull(key)

    override fun setString(key: String, value: String) = KeychainSettings().putString(key, value)
    override fun setInt(key: String, value: Int) = KeychainSettings().putInt(key, value)
    override fun setLong(key: String, value: Long) = KeychainSettings().putLong(key, value)
    override fun setBoolean(key: String, value: Boolean) = KeychainSettings().putBoolean(key, value)
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class CredentialsStorageFactory {
    actual fun create(): CredentialsStorage =
        IosKeychainCredentialsStorage()
}