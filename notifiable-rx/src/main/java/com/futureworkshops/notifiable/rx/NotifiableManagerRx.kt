/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx

import android.content.Context
import android.os.Build
import com.futureworkshops.notifiable.rx.internal.network.NotifiableApiImpl
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


    init {
        this.context = builder.context
        this.debug = builder.debug

        this.notifiableApi = NotifiableApiImpl(
            builder.endpoint,
            builder.notifiableClient,
            builder.notifiableSecret,
            this.debug
        )

    }

    /**
     * Register a device to receive notifications from the Notifiable server.
     *
     * The Firebase Cloud Messaging token is obtained automatically.
     *
     * @param deviceName the name of the device, can be anything
     * @param userAlias  the name of the user you want to associate the device to
     * @param locale     locale of the device
     * @param customProperties  Set of properties to be associated to the device
     */
    fun registerDevice(
        deviceName: String? = null,
        userAlias: String? = null,
        locale: Locale? = null,
        customProperties: HashMap<String, Any>? = null
    ): Single<NotifiableDevice> {

        val safeDeviceName = deviceName ?: Build.DEVICE

        return hasPlayServices()
            .flatMap {
                getFirebaseToken()
            }
            .flatMap { fcmToken ->
                notifiableApi.registerDevice(
                    safeDeviceName,
                    fcmToken,
                    locale,
                    userAlias,
                    FCM_NOTIFICATION_PROVIDER,
                    customProperties
                )
            }
            .subscribeOn(Schedulers.io())

//            .map { incompleteDevice ->
//                NotifiableDevice(
//                    incompleteDevice.id,
//                    deviceName,
//                    userAlias ?: "",
//                    deviceToken,
//                    locale,
//                    incompleteDevice.customProperties
//                )
//            }
    }

    /**
     * Unregister the given device.
     *
     * @param deviceId  id returned by the server after registering the device
     */
    fun unregisterDevice(deviceId: String): Completable {
        return notifiableApi.unregisterToken(deviceId)
    }

    /**
     * This will notify the `Notifiable` application that a notification has been received on a device.
     *
     * @param notificationId id of the received notification
     * @param deviceId       id returned by the server after registering the device
     */
    fun markNotificationReceived(notificationId: String, deviceId: String): Completable {
        return notifiableApi.markNotificationAsReceived(deviceId, notificationId)
    }

    /**
     * This will notify the `Notifiable` application that a notification has been opened from a device.
     *
     * @param notificationId id of the received notification
     * @param deviceId       id returned by the server after registering the device
     */
    fun markNotificationOpened(notificationId: String, deviceId: String): Completable {
        return notifiableApi.markNotificationAsOpened(deviceId, notificationId)
    }

    /**
     * Check if the device has Google Play services.
     *
     * If any error is thrown, you can use GoogleApiAvailability to show an error dialog:
     *
     * ```
     * val apiAvailability = GoogleApiAvailability.getInstance()
     * if (apiAvailability.isUserResolvableError(resultCode)) {
    apiAvailability.getErrorDialog(activity,resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
    .show()
    }
     * ```
     */
    private fun hasPlayServices(): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            val apiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)

            if (resultCode != ConnectionResult.SUCCESS) {
                emitter.onError(RuntimeException("Google Play Services error: $resultCode"))
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


    companion object {
        const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        const val FCM_NOTIFICATION_PROVIDER = "fcm"
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