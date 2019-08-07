/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.domain.dagger

import com.futureworkshops.notifiable.sample.presentation.service.MyFirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    internal abstract fun contributeMyService(): MyFirebaseMessagingService
}