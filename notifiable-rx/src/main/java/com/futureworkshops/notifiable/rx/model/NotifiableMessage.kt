/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.model

import java.io.Serializable


data class NotifiableMessage(
    val notificationId: Int,
    val title: String,
    val message: String,
    val deviceProperties: Map<String, String> = emptyMap()
) : Serializable




