/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.domain.dagger


import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module


@Suppress("unused")
@Module
abstract class ViewModelModule {


    @Binds
    abstract fun bindViewModelFactory(factory: NotifiableViewModelFactory): ViewModelProvider.Factory
}
