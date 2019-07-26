/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.model

interface NotifiableCallback<S> {

    fun onSuccess(ret: S)

    fun onError(error: String)
}
