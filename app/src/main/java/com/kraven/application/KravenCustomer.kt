package com.kraven.application

import android.app.Application
import android.content.Context
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient

import com.google.android.libraries.places.api.Places

import com.kraven.R
import com.kraven.core.AppPreferences
import com.kraven.di.Injector
import com.kraven.ui.home.model.Promocode


import javax.inject.Inject


class KravenCustomer : Application() {

    @Inject
    lateinit var appPreferences: AppPreferences

    companion object {
        lateinit var appContext: Context
        var tempPromoCode: Promocode? = null
        var isWaitingPlaceOrder = false
    }

    override fun onCreate() {
        super.onCreate()

        //LeakCanary.config = LeakCanary.config.copy(true)
        Places.initialize(applicationContext, getString(R.string.google_api_key))
        Injector.INSTANCE.initAppComponent(this, getString(R.string.API_KEY))
        appContext = this

        AWSMobileClient.getInstance().credentialsProvider = object : AWSCredentialsProvider {
            override fun getCredentials(): AWSCredentials {
                return object : AWSCredentials {
                    override fun getAWSAccessKeyId(): String {
                        return getString(R.string.access_key)
                    }

                    override fun getAWSSecretKey(): String {
                        return getString(R.string.secret_access_key)
                    }
                }
            }

            override fun refresh() {}
        }
    }
}
