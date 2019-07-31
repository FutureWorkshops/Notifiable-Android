/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal.storage


import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.futureworkshops.notifiable.rx.model.NotifiableDevice


/**
 * This local storage class uses [EncryptedSharedPreferences] to store data.
 */
class NotifiableSecureStorage(sharedPreferences: SharedPreferences) {
    init {
        if (sharedPreferences !is EncryptedSharedPreferences) {
            throw RuntimeException("Notifiable secure storage requires androidx.security.crypto.EncryptedSharedPreferences")
        }
    }

    fun saveNotifiableDevice(notifiableDevice: NotifiableDevice) {

    }

    fun removeNotifiableDevice() {

    }
}