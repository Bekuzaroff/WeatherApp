package com.example.weatherapp.data.repositories

import com.example.weatherapp.data.remotedata.WeatherService
import com.example.weatherapp.domain.models.WeatherResponse
import com.example.weatherapp.domain.repoimpls.ForecastRepository

import retrofit2.Response
import javax.inject.Inject

class ForecastRepositoryImpl @Inject constructor(private val service: WeatherService): ForecastRepository {
    override suspend fun getWeatherForecast(
        api_key: String,
        city: String,
    ): Response<WeatherResponse> = service.getWeatherForecast(
        api_key,
        city,
    )

}