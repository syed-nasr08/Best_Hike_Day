package com.example.besthikeday.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DayForecast(
    val date: Date,
    val dateString: String,
    val dayName: String,
    val temperature: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val windSpeed: Double,
    val rainChance: Double, // Probability of rain (0.0 - 1.0, converted to percentage)
    val weatherDescription: String,
    val weatherIcon: String,
    val isBestDay: Boolean = false
) {
    companion object {
        fun fromForecastItem(item: ForecastItem): DayForecast {
            val date = Date(item.timestamp * 1000)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            
            return DayForecast(
                date = date,
                dateString = dateFormat.format(date),
                dayName = dayFormat.format(date),
                temperature = item.main.temperature,
                tempMin = item.main.tempMin,
                tempMax = item.main.tempMax,
                humidity = item.main.humidity,
                windSpeed = item.wind.speed,
                rainChance = item.rainChance,
                weatherDescription = item.weather.firstOrNull()?.description ?: "",
                weatherIcon = item.weather.firstOrNull()?.icon ?: ""
            )
        }
        
        fun groupByDay(forecastItems: List<ForecastItem>): List<DayForecast> {
            val grouped = forecastItems.groupBy { 
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(it.timestamp * 1000))
            }
            
            return grouped.map { (_, items) ->
                val firstItem = items.first()
                val avgTemp = items.map { it.main.temperature }.average()
                val minTemp = items.minOf { it.main.tempMin }
                val maxTemp = items.maxOf { it.main.tempMax }
                val avgHumidity = items.map { it.main.humidity }.average().toInt()
                val avgWindSpeed = items.map { it.wind.speed }.average()
                // Use maximum rain chance for the day (worst case scenario)
                val maxRainChance = items.map { it.rainChance }.maxOrNull() ?: 0.0
                
                val date = Date(firstItem.timestamp * 1000)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                
                DayForecast(
                    date = date,
                    dateString = dateFormat.format(date),
                    dayName = dayFormat.format(date),
                    temperature = avgTemp,
                    tempMin = minTemp,
                    tempMax = maxTemp,
                    humidity = avgHumidity,
                    windSpeed = avgWindSpeed,
                    rainChance = maxRainChance,
                    weatherDescription = firstItem.weather.firstOrNull()?.description ?: "",
                    weatherIcon = firstItem.weather.firstOrNull()?.icon ?: ""
                )
            }.take(7) // Take only 7 days
        }
    }
    
    /**
     * Calculates a percentage score (0-100%) for how good this day is for hiking.
     * Algorithm combines:
     * - Temperature (40% weight): Ideal range 15-25°C
     * - Rain Chance (40% weight): Lower is better (0% = perfect, 100% = bad)
     * - Wind Speed (20% weight): Ideal < 15 km/h
     */
    fun calculateHikeScore(): Int {
        // Temperature Score (0-100, 40% weight)
        val tempScore = when {
            temperature >= 15 && temperature <= 25 -> 100 // Perfect range
            temperature >= 10 && temperature < 15 -> {
                // Linear interpolation: 10°C = 70, 15°C = 100
                70 + ((temperature - 10) / 5 * 30).toInt()
            }
            temperature > 25 && temperature <= 30 -> {
                // Linear interpolation: 25°C = 100, 30°C = 70
                100 - ((temperature - 25) / 5 * 30).toInt()
            }
            temperature >= 5 && temperature < 10 -> {
                // Linear interpolation: 5°C = 40, 10°C = 70
                40 + ((temperature - 5) / 5 * 30).toInt()
            }
            temperature > 30 && temperature <= 35 -> {
                // Linear interpolation: 30°C = 70, 35°C = 40
                70 - ((temperature - 30) / 5 * 30).toInt()
            }
            temperature < 5 -> {
                // Very cold: 0-40 based on how cold
                (40 * (temperature / 5).coerceAtLeast(0.0)).toInt()
            }
            temperature > 35 -> {
                // Very hot: 0-40 based on how hot
                (40 * (1 - (temperature - 35) / 10).coerceAtMost(1.0).coerceAtLeast(0.0)).toInt()
            }
            else -> 0
        }.coerceIn(0, 100)
        
        // Rain Chance Score (0-100, 40% weight)
        // rainChance is 0.0-1.0, convert to percentage and invert (0% rain = 100 score, 100% rain = 0 score)
        val rainChancePercent = rainChance * 100
        val rainScore = (100 - rainChancePercent).toInt().coerceIn(0, 100)
        
        // Wind Speed Score (0-100, 20% weight)
        val windScore = when {
            windSpeed <= 10 -> 100 // Perfect: calm to light breeze
            windSpeed <= 15 -> 90  // Good: gentle breeze
            windSpeed <= 20 -> 70  // Moderate: moderate breeze
            windSpeed <= 25 -> 50  // Acceptable: fresh breeze
            windSpeed <= 30 -> 30  // Poor: strong breeze
            windSpeed <= 40 -> 10  // Bad: near gale
            else -> 0              // Very bad: gale or stronger
        }
        
        // Weighted average: Temperature 40%, Rain 40%, Wind 20%
        val finalScore = (tempScore * 0.4 + rainScore * 0.4 + windScore * 0.2).toInt()
        
        return finalScore.coerceIn(0, 100)
    }
    
    /**
     * Get rain chance as percentage (0-100)
     */
    fun getRainChancePercent(): Int {
        return (rainChance * 100).toInt()
    }
}

