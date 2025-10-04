package com.hansholz.bestenotenapp.security

import com.microsoft.credentialstorage.SecretStore
import com.microsoft.credentialstorage.StorageProvider
import com.microsoft.credentialstorage.model.StoredCredential

class DesktopCredentialsStorage : CredentialsStorage {
    private var credentialsStorage: SecretStore<StoredCredential> =
        StorageProvider.getCredentialStorage(
            true,
            StorageProvider.SecureOption.REQUIRED,
        )

    private fun getValueOrNull(key: String): String? {
        val passwordArray = credentialsStorage.get(key)?.password ?: return null
        return passwordArray.concatToString()
    }

    private fun setValue(
        key: String,
        value: String,
    ) {
        if (credentialsStorage.get(key) != null) {
            credentialsStorage.delete(key)
        }

        credentialsStorage.add(key, StoredCredential(key, value.toCharArray()))
    }

    override fun getString(key: String): String? = getValueOrNull(key)

    override fun getInt(key: String): Int? = getValueOrNull(key)?.toIntOrNull()

    override fun getLong(key: String): Long? = getValueOrNull(key)?.toLongOrNull()

    override fun getBoolean(key: String): Boolean? = getValueOrNull(key)?.toBooleanStrictOrNull()

    override fun setString(
        key: String,
        value: String,
    ) = setValue(key, value)

    override fun setInt(
        key: String,
        value: Int,
    ) = setValue(key, value.toString())

    override fun setLong(
        key: String,
        value: Long,
    ) = setValue(key, value.toString())

    override fun setBoolean(
        key: String,
        value: Boolean,
    ) = setValue(key, value.toString())
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class CredentialsStorageFactory {
    actual fun create(): CredentialsStorage = DesktopCredentialsStorage()
}
