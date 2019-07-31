/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation

import DaggerAppComponent
import com.futureworkshops.notifiable.sample.domain.dagger.AppInjector
import com.futureworkshops.notifiable.sample.domain.model.Configuration
import com.futureworkshops.notifiable.sample.domain.timber.ReleaseLogTree
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

class SampleApp : DaggerApplication() {

    @Inject
    lateinit var configuration: Configuration

    override fun onCreate() {
        super.onCreate()

        /* Injection happens before this is called */
        initTimber()

    }


    private fun initTimber() {
        if (configuration.logsEnabled) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseLogTree())

        }
    }


    /**
     * Implementations should return an [AndroidInjector] for the concrete [ ]. Typically, that injector is a [dagger.Component].
     */
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val daggerAppComponent = DaggerAppComponent.builder()
            .application(this)
//            .schedulerProvider(WorkerSchedulerProvider())
            .build()

        daggerAppComponent.inject(this)

        AppInjector.init(this)

        return daggerAppComponent
    }
}