package com.savana.di

import com.savana.BuildConfig.API_URL
import com.savana.data.network.services.tracks.TrackService
import com.savana.data.network.services.user.UserService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


fun provideRetrofit(baseUrl: String = "$API_URL/api/"): Retrofit {

    val interceptor = HttpLoggingInterceptor()

    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

    val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

object api {
    val userService: UserService by lazy {
        provideRetrofit("$API_URL/api/users/").create(UserService::class.java)
    }

    val trackService: TrackService by lazy {
        provideRetrofit("$API_URL/api/tracks/").create(TrackService::class.java)
    }

}