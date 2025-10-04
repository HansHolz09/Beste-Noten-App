@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.security

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.hansholz.bestenotenapp.utils.AndroidContext

class AndroidEncryptedPrefsCredentials(
    context: Context,
) : CredentialsStorage {
    private var masterKey: MasterKey =
        MasterKey
            .Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private var prefs: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            "credentials_storage",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    override fun getString(key: String): String? = prefs.getString(key, null)

    override fun getInt(key: String): Int? = prefs.getString(key, null)?.toIntOrNull()

    override fun getLong(key: String): Long? = prefs.getString(key, null)?.toLongOrNull()

    override fun getBoolean(key: String): Boolean? = prefs.getString(key, null)?.toBooleanStrictOrNull()

    override fun setString(
        key: String,
        value: String,
    ) = prefs.edit { putString(key, value) }

    override fun setInt(
        key: String,
        value: Int,
    ) = prefs.edit { putString(key, value.toString()) }

    override fun setLong(
        key: String,
        value: Long,
    ) = prefs.edit { putString(key, value.toString()) }

    override fun setBoolean(
        key: String,
        value: Boolean,
    ) = prefs.edit { putString(key, value.toString()) }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class CredentialsStorageFactory {
    actual fun create(): CredentialsStorage = AndroidEncryptedPrefsCredentials(AndroidContext.context.get()!!)
}
