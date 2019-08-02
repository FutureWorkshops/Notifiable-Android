/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.demo

import com.futureworkshops.notifiable.rx.model.NotifiableDevice
import java.util.*

data class DemoState(
    val isCheckingNotifiableState: Boolean = false,
    val isUpdating: Boolean = false,
    val deviceRegistered: Boolean = false,
    val deviceNotRegistered: Boolean = false,
    val hasError: Boolean = false,
    val error: Error = Error.None,
    val notifiableDevice: RegisteredDevice? = null
) {


    sealed class Error(message: String? = null) {
        object GoogleServicesError : Error()
        object UserNotFound : Error()
        object None : Error()
    }

    data class RegisteredDevice(
        var name: String,
        var user: String,
        var locale: Locale,
        var customProperties: Map<String, String> = emptyMap()
    ) {
        constructor(notifiableDevice: NotifiableDevice) : this(
            notifiableDevice.name,
            notifiableDevice.user,
            notifiableDevice.locale
        )
    }

}