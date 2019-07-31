/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal.network

import com.google.gson.annotations.SerializedName

/**
 * All parameters are optional because we use the same data class when we want to update a [NotifiableDevice].
 */
data class NotifiableDeviceRequestBody(
    @SerializedName("name") var deviceName: String? = null,
    var token: String? = null,
    var provider: String? = null,
    @SerializedName("user_alias") var user: String? = null,
    var language: String,
    var country: String,
    var properties: Map<String, String>? = null

)

data class NotifiableRegisterRequesBody(
    @SerializedName("device_token") var deviceRequestBody: NotifiableDeviceRequestBody
)