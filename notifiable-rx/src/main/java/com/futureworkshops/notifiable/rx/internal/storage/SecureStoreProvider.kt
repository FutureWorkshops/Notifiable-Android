/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

import timber.log.Timber

/**
 * Wrapper over the [SecuredPreferenceStore] and [NotifiableSecureStorage] initialization
 * mechanisms in order to mock the inside tests.
 */
class SecureStoreProvider(private val context: Context) {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

    fun getSecureKeyStore(): NotifiableSecureStorage {
        try {

            val sharedPreferences = EncryptedSharedPreferences
                .create(
                    NOTIFIABLE_SECURE_PREFERENCES,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

            return NotifiableSecureStorage(sharedPreferences)
        } catch (e: Exception) {
            Timber.e(e)
            throw RuntimeException(e)
        }
    }

    companion object {
        const val NOTIFIABLE_SECURE_PREFERENCES = "nspp"
    }
}
