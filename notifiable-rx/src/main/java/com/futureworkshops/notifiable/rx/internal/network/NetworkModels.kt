/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal.network

import com.google.gson.annotations.SerializedName


data class NotifiableDeviceRequestBody(
    @SerializedName("name") var deviceName: String,
    var token: String,
    var provider: String,
    @SerializedName("user_alias") var user: String?,
    var language: String,
    var country: String

)

data class NotifiableRegisterRequesBody(
    @SerializedName("device_token") var deviceRequestBody: NotifiableDeviceRequestBody
)