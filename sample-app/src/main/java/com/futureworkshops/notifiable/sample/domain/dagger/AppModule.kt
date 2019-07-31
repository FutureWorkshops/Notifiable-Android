/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

import android.content.Context
import com.futureworkshops.notifiable.rx.NotifiableManagerRx
import com.futureworkshops.notifiable.sample.domain.dagger.ViewModelModule
import com.futureworkshops.notifiable.sample.domain.model.Configuration
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 *
 * By using Dagger Android we do not need to pass our Application instance to any module,
 * we simply need to expose our Application as Context.
 * One of the advantages of Dagger.Android is that your
 * Application & Activities are provided into your graph for you (via subcomponents)
 */
@Module(includes = [ViewModelModule::class])
class AppModule {

    /**
     * Provide a [Configuration] object across the whole app.
     */
    @Provides
    @Singleton
    fun provideConfiguration(): Configuration = Configuration()

    /**
     * Provide a [com.futureworkshops.notifiable.rx.NotifiableManagerRx] object across the whole app.
     */
    @Provides
    @Singleton
    fun provideNotifiableManager(context: Context, config: Configuration): NotifiableManagerRx =
        NotifiableManagerRx.Builder(context)
            .endpoint(config.notifiableEndpoint)
            .credentials(config.notifiableClient, config.notifiableSecret)
            .debug(config.logsEnabled)
            .build()


}