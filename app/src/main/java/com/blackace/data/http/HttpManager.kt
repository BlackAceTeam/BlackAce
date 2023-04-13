package com.blackace.data.http

import com.blackace.BuildConfig
import com.blackace.util.holder.GsonHolder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 10:52 PM
 */
object HttpManager {

    private val BASE_URL = "https://goolgostat.com/"

    val okHttp by lazy {
        OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    this.addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }
            .addInterceptor(BestHostInterceptor())
            .addInterceptor(TokenInterceptor())
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(2,TimeUnit.MINUTES)
            .writeTimeout(2,TimeUnit.MINUTES)
            .build()
    }

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(GsonHolder.gson))
            .build()
    }


    inline fun <reified T : Any> create(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }
}
