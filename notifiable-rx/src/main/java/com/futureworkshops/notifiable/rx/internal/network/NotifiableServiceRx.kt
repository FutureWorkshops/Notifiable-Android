/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal.network

import com.futureworkshops.notifiable.rx.model.NotifiableDevice
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface NotifiableServiceRx {

    @Headers("Content-Type: application/json")
    @POST(USER_API + VERSION + DEVICE_TOKENS)
    fun registerDevice(@Body body: NotifiableRegisterRequesBody): Single<NotifiableDevice>

    @Headers("Content-Type: application/json")
    @PATCH("$USER_API$VERSION$DEVICE_TOKENS/$DEVICE_ID_PATH_PARAM")
    fun updateDeviceInfo(
        @Path(DEVICE_ID) notifiableDeviceId: String,
        @Body deviceInfo: NotifiableRegisterRequesBody
    ): Completable

    @Headers("Content-Type: application/json")
    @PATCH("$USER_API$VERSION$DEVICE_TOKENS/$DEVICE_ID_PATH_PARAM")
    fun unregisterToken(
        @Path(DEVICE_ID) notifiableDeviceId: String,
        @Body userAlias: String
    ): Completable

    @Headers("Content-Type: application/json")
    @POST("$USER_API$VERSION$NOTIFICATIONS/$ID$STATUS_DELIVERED")
    fun markNotificationAsReceived(@Path("id") notificationId: String, @Body body: String): Completable

    @Headers("Content-Type: application/json")
    @POST("$USER_API$VERSION$NOTIFICATIONS/$ID$STATUS_OPENED")
    fun markNotificationAsOpened(@Path("id") notificationId: String, @Body body: String): Completable

    companion object {

        const val USER_API = "/api"
        const val VERSION = "/v1"
        const val DEVICE_TOKENS = "/device_tokens"
        const val NOTIFICATIONS = "/notifications"
        const val ID = "{id}"
        const val STATUS_OPENED = "/opened"
        const val STATUS_DELIVERED = "/delivered"

        const val DEVICE_ID_PATH_PARAM = "{device_token_id}"
        const val DEVICE_ID = "device_token_id"

        const val USER_ALIAS = "user_alias"
    }

}