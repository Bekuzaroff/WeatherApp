package com.example.weatherapp.domain.repoimpls

import com.example.weatherapp.domain.models.WeatherResponse
import retrofit2.Response

interface ForecastRepository {
    suspend fun getWeatherForecast(
        api_key: String,
        city: String,
    ): Response<WeatherResponse>
}