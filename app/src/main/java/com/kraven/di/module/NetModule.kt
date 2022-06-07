package com.kraven.di.module


import com.kraven.core.AESCryptoInterceptor
import com.kraven.core.Session
import com.kraven.data.URLFactory
import com.throdle.exception.AuthenticationException
import com.throdle.exception.ServerError
import dagger.Module
import dagger.Provides
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Created by hlink21 on 11/5/17.
 */


@Module(includes = [(ApplicationModule::class)])
class NetModule {

    @Singleton
    @Provides
    internal fun provideClient(@Named("header") headerInterceptor: Interceptor,
                               @Named("pre_validation") networkInterceptor: Interceptor,
                               @Named("aes") aesInterceptor: Interceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(aesInterceptor)
                .addNetworkInterceptor(networkInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()
    }

    @Provides
    @Singleton
    @Named("place_order")
    internal fun provideRetrofitOrder(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(URLFactory.provideHttpUrl())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Singleton
    @Provides
    @Named("place_order")
    internal fun provideClientOrder(@Named("header") headerInterceptor: Interceptor,
                                    @Named("pre_validation") networkInterceptor: Interceptor,
                                    @Named("aes") aesInterceptor: Interceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1
        return OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addInterceptor(headerInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(aesInterceptor)
                .retryOnConnectionFailure(false)
                .addNetworkInterceptor(networkInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .cache(null)
                .build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(URLFactory.provideHttpUrl())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Singleton
    @Provides
    @Named("provideGoogleRetrofit")
    internal fun provideGoogleRetrofit(@Named("header") headerInterceptor: Interceptor,
                                       @Named("pre_validation") networkInterceptor: Interceptor,
                                       @Named("aes") aesInterceptor: Interceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(networkInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()
    }

    @Provides
    @Singleton
    @Named("header")
    internal fun provideHeaderInterceptor(session: Session): Interceptor {

        val tz = TimeZone.getDefault()
        val cal = GregorianCalendar.getInstance(tz)
        cal.timeZone = TimeZone.getTimeZone("UTC")
        val offsetInMillis = tz.getOffset(cal.timeInMillis)
        var offset = String.format(Locale.US,"%02d:%02d", abs(offsetInMillis / 3600000), abs(offsetInMillis / 60000 % 60))
        offset = "UTC" + (if (offsetInMillis >= 0) "+" else "-") + offset
        return Interceptor { chain ->
            val build = chain.request().newBuilder()
                    .addHeader(Session.API_KEY, session.apiKey)
                    .addHeader(Session.USER_SESSION, session.userSession)
                    .addHeader("gmt_offset", offset)
                    .addHeader("timezone", tz.id)
                    .build()
            chain.proceed(build)
        }
    }

    @Provides
    @Singleton
    @Named("pre_validation")
    internal fun provideNetworkInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val code = response.code
            if (code >= 500)
                throw ServerError("Unknown server error", response.body!!.string())
            else if (code == 401)
                throw AuthenticationException()
            response
        }
    }

    @Provides
    @Singleton
    @Named("aes")
    internal fun provideAesCryptoInterceptor(aesCryptoInterceptor: AESCryptoInterceptor): Interceptor {
        return aesCryptoInterceptor
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}
