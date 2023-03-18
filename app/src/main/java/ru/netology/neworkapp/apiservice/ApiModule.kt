package ru.netology.neworkapp.apiservice

import android.content.SharedPreferences
import androidx.viewbinding.BuildConfig
import ru.netology.neworkapp.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.neworkapp.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {
    companion object {
        const val BASE_URL = "https://netomedia.ru/api/"
    }


    @Provides
    @Singleton
    fun providesLogging() : Interceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }


    @Provides
    @Singleton
    fun providesOkHttp(
        logging : Interceptor,
        prefs : SharedPreferences
    ) = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            prefs.getString(AppAuth.tokenKey, null)?.let{ token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            }
            chain.proceed(chain.request())
        }.build()


    @Provides
    @Singleton
    fun providesRetrofit(
        okHttp : OkHttpClient
    ) = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp)
        .build()!!


    @Provides
    @Singleton
    fun providesApiService(
        retrofit:Retrofit
    ) : ApiService = retrofit.create()
}