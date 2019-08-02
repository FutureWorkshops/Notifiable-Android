/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx

import android.content.Context
import android.os.Build
import com.futureworkshops.notifiable.rx.internal.GooglePlayServicesException
import com.futureworkshops.notifiable.rx.internal.network.NotifiableApiImpl
import com.futureworkshops.notifiable.rx.internal.storage.NotifiableSecureStorage
import com.futureworkshops.notifiable.rx.internal.storage.SecureStoreProvider
import com.futureworkshops.notifiable.rx.model.NotifiableDevice
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

class NotifiableManagerRx private constructor(builder: Builder) {
    private var context: Context
    private var debug: Boolean = false
    private var notifiableApi: NotifiableApiImpl
    private var notifiableSecureStorage: NotifiableSecureStorage
    private var registeredDevice: NotifiableDevice?

    init {
        this.context = builder.context
        this.debug = builder.debug

        this.notifiableApi = NotifiableApiImpl(
            builder.endpoint,
            builder.notifiableClient,
            builder.notifiableSecret,
            this.debug
        )

        this.notifiableSecureStorage = SecureStoreProvider(this.context).getSecureKeyStore()

        this.registeredDevice = notifiableSecureStorage.getRegisteredDevice()
    }

    /**
     * Register a device to receive notifications from the Notifiable server.
     *
     * The Firebase Cloud Messaging token is obtained automatically.
     *
     * @param deviceName the name of the device, defaults to [Build.DEVICE]
     * @param userAlias  the name of the user you want to associate the device to
     * @param locale     locale of the device
     * @param customProperties  Set of properties to be associated to the device
     *
     * @throws [GooglePlayServicesException] if play services are not available
     */
    fun registerDevice(
        deviceName: String? = null,
        userAlias: String? = null,
        locale: Locale? = null,
        customProperties: HashMap<String, String>? = null
    ): Single<NotifiableDevice> {

        val safeDeviceName = if (deviceName.isNullOrEmpty()) {
            Build.MODEL
        } else {
            deviceName
        }

        val tempDevice = NotifiableDevice(
            -1,
            safeDeviceName,
            userAlias ?: "",
            "",
            locale ?: Locale.getDefault(),
            customProperties ?: emptyMap()
        )

        return hasPlayServices()
            .flatMap {
                getFirebaseToken()
            }
            .flatMap { fcmToken ->
                tempDevice.token = fcmToken

                notifiableApi.registerDevice(
                    safeDeviceName,
                    fcmToken,
                    locale,
                    userAlias,
                    FCM_NOTIFICATION_PROVIDER,
                    customProperties
                )
            }
            .doOnError {
                // delete local device if registration failed
                registeredDevice = null
                notifiableSecureStorage.removeNotifiableDevice()
            }
            .map { incompleteDevice ->
                tempDevice.id = incompleteDevice.id
                registeredDevice = tempDevice
                notifiableSecureStorage.saveNotifiableDevice(registeredDevice!!)
                registeredDevice!!
            }
            .subscribeOn(Schedulers.io())

    }

    /**
     * Get the registered [NotifiableDevice].
     *
     * @return the [NotifiableDevice] or a [RuntimeException] if there is no device
     */
    fun getRegisteredDevice(): Single<NotifiableDevice> {
        return registeredDevice?.let { Single.just(it) }
            ?: Single.error(RuntimeException("No registered device found"))
    }

    /**
     * Update Notifiable device information. You need to specify at least one parameter .
     *
     * @param token update the FCM token
     * @param userName update the name of the user associated with the device
     * @param deviceName update the name of the device(default value is [Build.DEVICE])
     * @param locale update the device locale
     * @param customProperties update one or more custom device properties
     */
    fun updateDeviceInformation(
        token: String? = null,
        userName: String? = null,
        deviceName: String? = null,
        locale: Locale? = null,
        customProperties: Map<String, String>? = null
    ): Completable {
        if (token.isNullOrBlank() &&
            userName.isNullOrBlank() &&
            deviceName.isNullOrBlank() &&
            locale == null
            && customProperties.isNullOrEmpty()
        ) {
            return Completable.error(RuntimeException("You need to specify at least one parameter to update"))
        } else {
            return callOnRegisteredDevice { deviceId ->
                notifiableApi.updateDeviceInformation(
                    deviceId,
                    token,
                    userName,
                    deviceName,
                    locale,
                    customProperties
                ).doOnComplete {
                    token?.let { registeredDevice!!.token = token }
                    userName?.let { registeredDevice!!.user = userName }
                    deviceName?.let { registeredDevice!!.name = deviceName }
                    locale?.let { registeredDevice!!.locale = locale }
                    customProperties?.let { registeredDevice!!.customProperties = customProperties }
                    notifiableSecureStorage.saveNotifiableDevice(registeredDevice!!)
                }
            }
        }
    }

    /**
     * Unregister the given device.
     *
     * @param deviceId  id returned by the server after registering the device
     */
    fun unregisterDevice(): Completable {
        return callOnRegisteredDevice { deviceId ->
            notifiableApi.unregisterToken(deviceId)
                .doOnComplete { notifiableSecureStorage.removeNotifiableDevice() }
        }
    }

    /**
     * This will notify the `Notifiable` application that a notification has been received on a device.
     *
     * @param notificationId id of the received notification
     * @param deviceId       id returned by the server after registering the device
     */
    fun markNotificationReceived(notificationId: String): Completable {
        return callOnRegisteredDevice { deviceId ->
            notifiableApi.markNotificationAsReceived(deviceId, notificationId)
        }
    }

    /**
     * This will notify the `Notifiable` application that a notification has been opened from a device.
     *
     * @param notificationId id of the received notification
     */
    fun markNotificationOpened(notificationId: String): Completable {
        return callOnRegisteredDevice { deviceId ->
            notifiableApi.markNotificationAsOpened(deviceId, notificationId)
        }
    }

    /**
     * Check if the device has Google Play services.
     *
     * @throws [GooglePlayServicesException]
     */
    private fun hasPlayServices(): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            val apiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)

            if (resultCode != ConnectionResult.SUCCESS) {
                emitter.onError(GooglePlayServicesException(resultCode))
            } else {
                emitter.onSuccess(true)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())

    }

    private fun getFirebaseToken(): Single<String> {
        return Single.create<String> { emitter ->
            FirebaseInstanceId.getInstance().instanceId
                .addOnSuccessListener { instanceIdResult ->
                    emitter.onSuccess(instanceIdResult.token)
                }
                .addOnFailureListener { t -> emitter.onError(t) }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    /**
     * Check that a registered device is available before calling the supplied code block.
     *
     * @return the result of the code block or [RuntimeException] if there's no registered device
     */
    private fun callOnRegisteredDevice(code: (String) -> Completable): Completable {
        return registeredDevice?.let { code(it.id.toString()) }
            ?: Completable.error(RuntimeException("No registered device found"))
    }


    companion object {
        const val FCM_NOTIFICATION_PROVIDER = "gcm"
    }

    class Builder(val context: Context) {
        lateinit var endpoint: String
            private set

        lateinit var notifiableClient: String
            private set

        lateinit var notifiableSecret: String
            private set

        var debug: Boolean = false
            private set

        fun endpoint(endpoint: String) = apply { this.endpoint = endpoint }

        fun credentials(client: String, secret: String) = apply {
            this.notifiableClient = client
            this.notifiableSecret = secret
        }

        /**
         * Enable or disable debug logs.
         */
        fun debug(debug: Boolean) = apply { this.debug = debug }

        fun build(): NotifiableManagerRx {

            if (endpoint.isNullOrBlank()) {
                // try to get a saved URL
                throw RuntimeException("Notifiable endpoint can not be null")
            }

            return NotifiableManagerRx(this)
        }
    }
}