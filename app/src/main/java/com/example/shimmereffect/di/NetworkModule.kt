package com.example.shimmereffect.di

import android.content.Context
import com.example.shimmereffect.InializationData
import com.example.shimmereffect.AppPreferences
import com.example.shimmereffect.BuildConfig
import com.example.shimmereffect.HEADER_CACHE_CONTROL
import com.example.shimmereffect.HEADER_PRAGMA
import com.example.shimmereffect.network.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideKotlinJsonAdapterFactory(): KotlinJsonAdapterFactory = KotlinJsonAdapterFactory()

    @Provides
    @Singleton
    fun provideMoshi(kotlinJsonAdapterFactory: KotlinJsonAdapterFactory): Moshi =
        Moshi.Builder()
            .add(kotlinJsonAdapterFactory)
            .build()

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(
            moshi
        )

    @Provides
    @Singleton
    fun provideOkHttp(
        appPreferences: AppPreferences,
        @ApplicationContext context: Context
    ): OkHttpClient {

        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(CommonParamInterceptor(appPreferences))
            .addInterceptor(provideOfflineCacheInterceptor(context))
            .addInterceptor(AuthTokenInterceptor(appPreferences))
            .addNetworkInterceptor(provideCacheInterceptor(context))
            .cache(provideCache(context))

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor();
            logging.level = (HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(logging)
//            httpClient.addNetworkInterceptor(StethoInterceptor())
        }

        return httpClient.build()
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache? {
        var cache: Cache? = null
        try {
            cache = Cache(File(context.cacheDir, "http-cache"), 10 * 1024 * 1024) // 10 MB
        } catch (e: Exception) {
            Timber.e("Cache: Could not create Cache!")
        }
        return cache
    }

    @Provides
    @Singleton
    fun provideCacheInterceptor(@ApplicationContext context: Context): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl: CacheControl =
                if (!InializationData.isInternetAvailable(context).isNullOrEmpty()) {
                    CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build()
                } else {
                    CacheControl.Builder()
                        .maxStale(1, TimeUnit.DAYS)
                        .build()
                }
            response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }
    }


    @Provides
    @Singleton
    fun provideOfflineCacheInterceptor(@ApplicationContext context: Context): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (InializationData.isInternetAvailable(context).isNullOrEmpty()) {
                val cacheControl: CacheControl = CacheControl.Builder()
                    .maxStale(1, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }

    //https://blog.mindorks.com/okhttp-interceptor-making-the-most-of-it
    class ErrorInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val request: Request = chain.request()
            val response = chain.proceed(request)
            when (response.code) {
                400 -> {
                    //Show Bad Request Error Message
                }
                401 -> {
                    //Show UnauthorizedError Message
                }

                403 -> {
                    //Show Forbidden Message
                }

                404 -> {
                    //Show NotFound Message
                }

                // ... and so on

            }
            return response
        }
    }

    class CacheInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response: Response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(10, TimeUnit.DAYS)
                .build()
            return response.newBuilder()
                .header("Cache-Control", cacheControl.toString())


                .build()
        }
    }

    class ForceCacheInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder: Request.Builder = chain.request().newBuilder()
//            if (!Utils.isInternetAvailable(context)) {
//                builder.cacheControl(CacheControl.FORCE_CACHE);
//            }
            return chain.proceed(builder.build());
        }
    }

    class UserAgentInterceptor(val userAgentt: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder: Request.Builder = chain.request().newBuilder()

            builder.addHeader("User-Agent", userAgentt)

            return chain.proceed(builder.build());
        }
    }

    class AuthTokenInterceptor(private val appPreferences: AppPreferences) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val authToken = appPreferences.getString(AppPreferences.Key.ACCESS_TOKEN, null)
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("Authorization", "Bearer $authToken")
            val request = requestBuilder.build()
            return chain.proceed(request)
        }
    }

    class OAuthInterceptor(private val tokenType: String, private val accessToken: String) :
        Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()
            request =
                request.newBuilder().header("Authorization", "$tokenType $accessToken").build()

            return chain.proceed(request)
        }
    }

    class CommonParamInterceptor(val appPreferences: AppPreferences) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()

            val originalHttpUrl = originalRequest.url
            val httpUrlBuilder = originalHttpUrl.newBuilder()
            val newHttpUrl = httpUrlBuilder.build()
            val requestBuilder = chain.request().newBuilder()
                .header("Accept", "application/json").url(newHttpUrl)
            val newRequest = requestBuilder.build()
            return chain.proceed(newRequest)
        }
    }


    @Singleton
    @Provides
    fun getApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideRetrofitClient(
        okHttp: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(moshiConverterFactory)
        .client(okHttp)
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttp)
        .build()
}

