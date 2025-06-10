package com.savana.di

import com.savana.BuildConfig.API_URL
import com.savana.data.network.services.user.UserService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


fun provideRetrofit(baseUrl: String = "$API_URL/api/"): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

object api {
    val userService: UserService by lazy {
        provideRetrofit("$API_URL/api/users").create(UserService::class.java)
    }

}