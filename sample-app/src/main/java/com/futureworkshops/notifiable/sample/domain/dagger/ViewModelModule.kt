/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.domain.dagger


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.futureworkshops.notifiable.sample.presentation.demo.DemoViewModel
import com.futureworkshops.notifiable.sample.presentation.notification.NotificationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(DemoViewModel::class)
    abstract fun bindDemoViewModel(demoViewModel: DemoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel::class)
    abstract fun bindNotificationViewModel(notificationViewModel: NotificationViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: NotifiableViewModelFactory): ViewModelProvider.Factory
}