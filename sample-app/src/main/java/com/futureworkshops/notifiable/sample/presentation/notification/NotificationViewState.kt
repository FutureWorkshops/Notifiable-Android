/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.notification

import com.futureworkshops.notifiable.rx.model.NotifiableMessage

data class NotificationViewState(
    val displayNotification: Boolean = false,
    val isMarkingNotification: Boolean = false,
    val isNotificationMarked: Boolean = false,
    val notification: NotifiableMessage? = null,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)
