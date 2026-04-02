package com.pedro.thingscontroller.data.datasource.impl.datastore

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pedro.thingscontroller.domain.repository.TokenProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class DataStoreTokenProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenProvider {

    private val Context.dataStore by preferencesDataStore(name = "secure_prefs")

    // Encrypt/decrypt using Android Keystore
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    private fun getOrCreateKey(): SecretKey {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
                .apply {
                    init(
                        KeyGenParameterSpec.Builder(
                            KEY_ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                        )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                    )
                }.generateKey()
        }
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }
        val encrypted = cipher.doFinal(value.toByteArray())
        val combined = cipher.iv + encrypted  // prepend IV for decryption later
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    private fun decrypt(value: String): String {
        val combined = Base64.decode(value, Base64.DEFAULT)
        val iv = combined.copyOfRange(0, 12)
        val encrypted = combined.copyOfRange(12, combined.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
        }
        return String(cipher.doFinal(encrypted))
    }

    override suspend fun getToken(): String? =
        context.dataStore.data
            .map { it[ACCESS_TOKEN_KEY] }
            .firstOrNull()
            ?.let { decrypt(it) }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { it[ACCESS_TOKEN_KEY] = encrypt(token) }
    }

    override suspend fun clearToken() {
        context.dataStore.edit { it.remove(ACCESS_TOKEN_KEY) }
    }

    companion object {
        private const val KEY_ALIAS = "token_key"
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }
}