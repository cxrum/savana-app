package com.savana.di

import com.savana.BuildConfig.apiUrl
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


fun provideRetrofit(baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}


val apiModule = module {
    val baseUrl = "$apiUrl/api"

    single {
        provideRetrofit(baseUrl)
    }
}