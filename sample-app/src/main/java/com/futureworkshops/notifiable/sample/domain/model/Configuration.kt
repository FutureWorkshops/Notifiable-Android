/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.domain.model

import com.futureworkshops.notifiable.sample.BuildConfig


/**
 * Configuration class used to read injected values from the CI.
 */
class Configuration {
    val logsEnabled = BuildConfig.DEBUG
    val notifiableEndpoint = BuildConfig.NOTIFIABLE_SERVER
    val notifiableClient = BuildConfig.NOTIFIABLE_CLIENT_ID
    val notifiableSecret = BuildConfig.NOTIFIABLE_CLIENT_SECRET

}