package com.example.besthikeday.data.api

import com.example.besthikeday.data.model.LocationResponse
import com.example.besthikeday.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
    
    @GET("geo/1.0/direct")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): List<LocationResponse>
}

