/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.model

import java.io.Serializable

/**
 * Created by stelian on 01/04/2016.
 */
data class NotifiableMessage(
    val notificationId: Int,
    val title: String,
    val message: String,
    val deviceProperties: Map<String, String> = emptyMap()
) : Serializable




