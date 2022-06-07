package com.kraven.di.module

import com.kraven.data.URLFactory
import com.kraven.data.datasource.*
import com.kraven.data.repository.*
import com.kraven.data.service.*
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton


@Module
class ServiceModule {

    @Provides
    @Singleton
    fun provideAuthenticationRepository(authenticationLiveDataSource: AuthenticationLiveDataSource): AuthenticationRepository {
        return authenticationLiveDataSource
    }

    @Provides
    @Singleton
    fun provideAuthenticationService(retrofit: Retrofit): AuthenticationService {
        return retrofit.create(AuthenticationService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userLiveDataSource: UserLiveDataSource): UserRepository {
        return userLiveDataSource
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun providePlaceRepository(placeOrderLiveDataSource: PlaceOrderLiveDataSource): PlaceOrderRepository {
        return placeOrderLiveDataSource
    }

    @Provides
    @Singleton
    fun providePlaceService(@Named("place_order")retrofit: Retrofit): PlaceOrderService {
        return retrofit.create(PlaceOrderService::class.java)
    }

    @Provides
    @Singleton
    fun provideCommonRepository(commonDataSource: CommonDataSource): CommonRepository {
        return commonDataSource
    }

    @Provides
    @Singleton
    fun provideCommonService(retrofit: Retrofit): CommonService {
        return retrofit.create(CommonService::class.java)
    }

    @Provides
    @Singleton
    fun provideGoogleRetrofitService(@Named("provideGoogleRetrofit") okHttpClient: OkHttpClient, retrofit: Retrofit,
                                     httpLoggingInterceptor: HttpLoggingInterceptor): GoogleService {
        return retrofit.newBuilder()
                .client(okHttpClient.newBuilder()
                        .build())
                .baseUrl(URLFactory.Method.GOOGLE_BASE_URL)
                .build()
                .create(GoogleService::class.java)
    }

    @Provides
    @Singleton
    fun provideGoogleRepository(googleDataSource: GoogleDataSource): GoogleRepository {
        return googleDataSource
    }
}