/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.model

data class GenericResponse(val isSuccess: Boolean = false,
                           val message: String? = "")