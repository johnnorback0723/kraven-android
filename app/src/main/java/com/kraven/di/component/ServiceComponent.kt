package com.kraven.di.component

import android.content.Context
import com.kraven.di.PerService
import com.kraven.di.module.ActivityModule
import com.kraven.services.LocationUpdateService

import dagger.BindsInstance
import dagger.Component


/**
 * Created by Android Developer on 24/12/18.
 */
@PerService
@Component(dependencies = arrayOf(ApplicationComponent::class), modules = arrayOf(ActivityModule::class))
interface ServiceComponent {
    @Component.Builder
    interface Builder {

        fun bindApplicationComponent(applicationComponent: ApplicationComponent): Builder

        @BindsInstance
        fun bindService(context: Context): Builder

        fun build(): ServiceComponent
    }

    fun inject(locationUpdateService: LocationUpdateService)

}