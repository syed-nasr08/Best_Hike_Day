package com.example.besthikeday.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.besthikeday.BuildConfig
import com.example.besthikeday.data.api.RetrofitClient
import com.example.besthikeday.data.model.DayForecast
import com.example.besthikeday.data.model.LocationResponse
import com.example.besthikeday.data.model.WeatherResponse
import com.example.besthikeday.util.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val forecast: List<DayForecast> = emptyList(),
    val cityName: String = "",
    val errorMessage: String? = null,
    val bestDay: DayForecast? = null,
    val searchQuery: String = "",
    val isSearchingLocation: Boolean = false
)

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherApiService = RetrofitClient.weatherApiService
    private val geoApiService = RetrofitClient.geoApiService
    private val locationHelper = LocationHelper(application)
    
    // API key is loaded from BuildConfig, which reads from local.properties
    // See README.md for setup instructions
    private val apiKey = BuildConfig.OPENWEATHER_API_KEY
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    init {
        loadWeatherData()
    }
    
    fun searchLocation(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearchingLocation = true, errorMessage = null)
            
            try {
                val locations = geoApiService.searchLocation(query, 1, apiKey)
                if (locations.isNotEmpty()) {
                    val location = locations.first()
                    loadWeatherForLocation(location.latitude, location.longitude, location.name)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSearchingLocation = false,
                        errorMessage = "Location not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearchingLocation = false,
                    errorMessage = "Error searching location: ${e.message}"
                )
            }
        }
    }
    
    fun loadWeatherForLocation(lat: Double, lon: Double, cityName: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isSearchingLocation = false)
            
            try {
                val response: WeatherResponse = weatherApiService.getWeatherForecast(
                    latitude = lat,
                    longitude = lon,
                    apiKey = apiKey
                )
                
                val dayForecasts = DayForecast.groupByDay(response.forecastList)
                val bestDay = dayForecasts.maxByOrNull { it.calculateHikeScore() }
                
                val forecastWithBestDay = dayForecasts.map { day ->
                    if (day.dateString == bestDay?.dateString) {
                        day.copy(isBestDay = true)
                    } else {
                        day
                    }
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    forecast = forecastWithBestDay,
                    cityName = cityName ?: response.city.name,
                    bestDay = bestDay?.copy(isBestDay = true),
                    errorMessage = null
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error loading weather: ${e.message}"
                )
            }
        }
    }
    
    fun loadWeatherData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val location = locationHelper.getCurrentLocation()
                
                if (location == null && !locationHelper.hasLocationPermission()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Location permission required"
                    )
                    return@launch
                }
                
                // Default to a location if permission not granted (San Francisco as example)
                val lat = location?.latitude ?: 37.7749
                val lon = location?.longitude ?: -122.4194
                
                loadWeatherForLocation(lat, lon)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error loading weather: ${e.message}"
                )
            }
        }
    }
    
    fun refresh() {
        loadWeatherData()
    }
}

