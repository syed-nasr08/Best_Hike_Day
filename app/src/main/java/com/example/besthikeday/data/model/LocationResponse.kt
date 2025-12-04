package com.example.besthikeday.data.model

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("name") val name: String,
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lon") val longitude: Double,
    @SerializedName("country") val country: String,
    @SerializedName("state") val state: String? = null
)



