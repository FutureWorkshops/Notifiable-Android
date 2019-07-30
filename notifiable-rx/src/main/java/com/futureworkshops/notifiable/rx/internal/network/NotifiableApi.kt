/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal.network

import com.futureworkshops.notifiable.rx.model.NotifiableDevice
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

interface NotifiableApi {


    fun registerDevice(
        deviceName: String,
        deviceToken: String,
        locale: Locale?,
        userAlias: String?,
        provider: String,
        customProperties: Map<String, Any>?
    ): Single<NotifiableDevice>


    fun updateDeviceInformation(
        deviceId: String,
        token: String?,
        username: String?,
        deviceName: String?,
        locale: Locale?,
        customProperties: Map<String, Any>?
    ): Single<NotifiableDevice>

    fun unregisterToken(
        deviceId: String
    ): Completable

    fun markNotificationAsReceived(
        deviceId: String,
        notificationId: String
    ): Completable

    fun markNotificationAsOpened(
        deviceId: String,
        notificationId: String
    ): Completable
}