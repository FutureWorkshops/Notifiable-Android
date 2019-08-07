/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal.storage


import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.futureworkshops.notifiable.rx.model.NotifiableDevice
import com.google.gson.Gson
import timber.log.Timber


/**
 * This local storage class uses [EncryptedSharedPreferences] to store data.
 */
class NotifiableSecureStorage(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()

    init {
        if (sharedPreferences !is EncryptedSharedPreferences) {
            throw RuntimeException("Notifiable secure storage requires androidx.security.crypto.EncryptedSharedPreferences")
        }
    }

    fun saveNotifiableDevice(notifiableDevice: NotifiableDevice) {
        sharedPreferences.edit()
            .putString(NOTIFIABLE_DEVICE_KEY, gson.toJson(notifiableDevice))
            .apply()
    }

    fun removeNotifiableDevice() {
        sharedPreferences.edit().remove(NOTIFIABLE_DEVICE_KEY).apply()
    }

    fun getRegisteredDevice(): NotifiableDevice? {
        val json = sharedPreferences.getString(NOTIFIABLE_DEVICE_KEY, "")

        json?.let {
            return try {
                gson.fromJson(it, NotifiableDevice::class.java)
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }

        return null
    }

    companion object {
        private const val NOTIFIABLE_DEVICE_KEY = "notifiable_device"
    }
}