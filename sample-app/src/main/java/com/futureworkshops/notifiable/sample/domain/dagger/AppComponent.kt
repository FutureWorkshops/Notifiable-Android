/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

import android.app.Application
import com.futureworkshops.notifiable.sample.presentation.SampleApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import javax.inject.Singleton


@Singleton
@Component(
    modules = [AppModule::class,
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class
        /*SplashActivityModule::class,
        MetricsProfileModule::class*/]
)
interface AppComponent : AndroidInjector<DaggerApplication> {

    fun inject(application: SampleApp)

    /**
     * This interface is used to provide parameters for modules.
     *
     *  Every method annotated with [BindsInstance] will link the method return type
     * to the method input type for the entire dependency graph - this means that we can't have
     * 2 methods with [BindsInstance] that accept the same type of parameters !!
     */
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): AppComponent.Builder

//        /**
//         * Specify the [SchedulersProvider] to be used across the AppComponent graph.
//         */
//        @BindsInstance
//        fun schedulerProvider(schedulersProvider: SchedulersProvider): AppComponent.Builder

        fun build(): AppComponent
    }
}
