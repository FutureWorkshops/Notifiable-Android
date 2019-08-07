/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.notification.dagger

import com.futureworkshops.notifiable.sample.presentation.notification.NotificationActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Suppress("unused")
@Module
abstract class NotificationModule {

    @ContributesAndroidInjector
    abstract fun contributeNotificationActivity(): NotificationActivity
}