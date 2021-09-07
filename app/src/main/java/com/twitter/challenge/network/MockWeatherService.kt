package com.twitter.challenge.network

import com.twitter.challenge.model.WeatherResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface MockWeatherService {
    @GET("current.json")
    suspend fun getCurrentWeather() : Response<WeatherResponse>

    @GET("future_1.json")
    suspend fun getFuture01Weather() : Response<WeatherResponse>

    @GET("future_2.json")
    suspend fun getFuture02Weather() : Response<WeatherResponse>

    @GET("future_3.json")
    suspend fun getFuture03Weather() : Response<WeatherResponse>

    @GET("future_4.json")
    suspend fun getFuture04Weather() : Response<WeatherResponse>

    @GET("future_5.json")
    suspend fun getFuture05Weather() : Response<WeatherResponse>
}

val weatherService: MockWeatherService by lazy {
    val okHttpClient = OkHttpClient.Builder().build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://twitter-code-challenge.s3.amazonaws.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()

    retrofit.create(MockWeatherService::class.java)
}