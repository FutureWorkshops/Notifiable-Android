/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.model

data class GenericResponse(
    val isSuccess: Boolean = false,
    val message: String? = ""
)