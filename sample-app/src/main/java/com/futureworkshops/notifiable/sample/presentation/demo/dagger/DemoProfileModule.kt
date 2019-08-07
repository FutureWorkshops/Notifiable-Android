/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.sample.presentation.demo.dagger


import com.futureworkshops.notifiable.sample.presentation.demo.DemoActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class DemoProfileModule {

    @ContributesAndroidInjector
    abstract fun contributeDemoActivity(): DemoActivity
}
