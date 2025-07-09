package com.hansholz.bestenotenapp.utils

interface CredentialsStorage {
    fun getString(key: String): String?
    fun getInt(key: String): Int?
    fun getLong(key: String): Long?
    fun getBoolean(key: String): Boolean?
    fun setString(key: String, value: String)
    fun setInt(key: String, value: Int)
    fun setLong(key: String, value: Long)
    fun setBoolean(key: String, value: Boolean)
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class CredentialsStorageFactory() {
    fun create(): CredentialsStorage
}