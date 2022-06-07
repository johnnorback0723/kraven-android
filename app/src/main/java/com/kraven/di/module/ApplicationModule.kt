package com.kraven.di.module

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kraven.R
import com.kraven.core.AppSession
import com.kraven.core.Session
import dagger.Module
import dagger.Provides
import java.io.File
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by hlink21 on 9/5/16.
 */
@Module
class ApplicationModule {

    @Provides
    @Named("cache")
    internal fun provideCacheDir(application: Application): File {
        return application.cacheDir
    }

    @Provides
    @Singleton
    internal fun provideResources(application: Application): Resources {
        return application.resources
    }

    @Provides
    @Singleton
    internal fun provideCurrentLocale(resources: Resources): Locale {

        val locale: Locale

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = resources.configuration.locales.get(0)
        } else {
            locale = resources.configuration.locale
        }

        return locale
    }

    @Provides
    @Singleton
    internal fun provideApplicationContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    internal fun getSession(session: AppSession): Session = session


    @Provides
    @Singleton
    @Named("aes-key")
    internal fun provideAESKey(context: Context): String {
        return context.getString(R.string.aes_key)
    }
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
                .setPrettyPrinting()
                .setVersion(1.0)
                .create()
    }
}
