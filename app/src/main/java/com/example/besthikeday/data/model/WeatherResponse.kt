package com.example.besthikeday.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("list") val forecastList: List<ForecastItem>,
    @SerializedName("city") val city: City
)

data class ForecastItem(
    @SerializedName("dt") val timestamp: Long,
    @SerializedName("main") val main: MainWeather,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("dt_txt") val dateText: String,
    @SerializedName("pop") val rainChance: Double = 0.0, // Probability of precipitation (0-1)
    @SerializedName("rain") val rain: Rain? = null // Rain volume if available
)

data class Rain(
    @SerializedName("3h") val volume3h: Double? = null // Rain volume for last 3 hours
)

data class MainWeather(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("pressure") val pressure: Int
)

data class Weather(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class Wind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val degree: Int
)

data class City(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("coord") val coordinates: Coordinates
)

data class Coordinates(
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lon") val longitude: Double
)

