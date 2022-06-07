package com.kraven.di.component

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.content.res.Resources
import com.kraven.data.repository.UserRepository
import com.kraven.application.KravenCustomer
import com.kraven.core.Session
import com.kraven.core.Validator
import com.kraven.data.repository.AuthenticationRepository
import com.kraven.data.repository.CommonRepository
import com.kraven.data.repository.GoogleRepository

import com.kraven.di.module.ApplicationModule
import com.kraven.di.module.NetModule
import com.kraven.di.module.ServiceModule
import com.kraven.di.module.ViewModelModule
import com.kraven.fcm.MyFirebaseMessagingService
import com.kraven.location.NewLocationManager
import com.kraven.utils.LocationManager
import com.kraven.utils.Locator

import dagger.BindsInstance
import dagger.Component
import java.io.File
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by hlink21 on 9/5/16.
 */
@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class, NetModule::class, ServiceModule::class])
interface ApplicationComponent {

    fun context(): Context

    @Named("cache")
    fun provideCacheDir(): File

    fun provideResources(): Resources

    fun provideCurrentLocale(): Locale

    fun provideViewModelFactory(): ViewModelProvider.Factory

    fun inject(kravenCustomerShell: KravenCustomer)

    fun provideUserRepository(): UserRepository

    fun provideAuthenticationRepository(): AuthenticationRepository

    fun provideCommonRepository(): CommonRepository

    fun provideValidator(): Validator

    fun provideSession(): Session

    fun provideLocationManager(): LocationManager

    fun provideLocationManagers(): NewLocationManager


    fun provideLocator():Locator
    fun provideGoogleRepository(): GoogleRepository
    fun inject(kravenCustomerShell: MyFirebaseMessagingService)

    @Component.Builder
    interface ApplicationComponentBuilder {

        @BindsInstance
        fun apiKey(@Named("api-key") apiKey: String): ApplicationComponentBuilder

        @BindsInstance
        fun application(application: Application): ApplicationComponentBuilder

        fun build(): ApplicationComponent
    }

}
