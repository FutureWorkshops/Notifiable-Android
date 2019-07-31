/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.model

import com.google.gson.annotations.SerializedName
import java.util.*


data class NotifiableDevice(
    var id: Int,
    var name: String,
    var user: String,
    var token: String,
    var locale: Locale,
    var customProperties: Map<String, String> = emptyMap(),
    @SerializedName("created_at") var createdAt: String = ""
)

