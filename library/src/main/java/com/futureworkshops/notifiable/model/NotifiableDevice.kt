/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.model

import com.google.gson.annotations.SerializedName

import java.util.HashMap
import java.util.Locale

/**
 * Created by stelian on 22/03/2016.
 */
class NotifiableDevice(
    var id: Int,
    var name: String,
    var user: String,
    var token: String,
    var locale: Locale,
    var customProperties: Map<String, Any> = emptyMap(),
    @SerializedName("created_at") var createdAt: String = ""
)

