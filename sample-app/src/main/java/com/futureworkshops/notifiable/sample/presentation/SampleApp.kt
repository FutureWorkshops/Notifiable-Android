/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation

import android.app.Application
import com.futureworkshops.notifiable.sample.BuildConfig
import com.futureworkshops.notifiable.sample.timber.ReleaseLogTree
import timber.log.Timber

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /* Injection happens before this is called */
        initTimber()

    }


    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseLogTree())

        }
    }
}