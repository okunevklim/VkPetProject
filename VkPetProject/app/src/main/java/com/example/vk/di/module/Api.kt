package com.example.vk.di.module

import androidx.annotation.NonNull
import com.example.vk.BuildConfig
import com.example.vk.network.VKService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Module
class Api @Inject constructor() {
    lateinit var servicePostVk: VKService
    var gson = Gson()

    private fun init() {
        val logging = HttpLoggingInterceptor()
        logging.level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
        servicePostVk = retrofit.create(VKService::class.java)
    }

    @Provides
    @NonNull
    @Singleton
    fun getService(): VKService {
        if (!this::servicePostVk.isInitialized) {
            init()
        }
        return servicePostVk
    }

    companion object {
        private const val SERVER_URL = "https://api.vk.com/method/"
    }
}