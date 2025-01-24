package com.example.weatherapp.domain.repoimpls

import com.example.weatherapp.domain.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Query

interface ForecastRepository {
    suspend fun getWeatherForecast(
        api_key: String,
        city: String,
    ): Response<WeatherResponse>
}