package com.example.weatherapp.domain.usecases

import com.example.weatherapp.domain.models.WeatherResponse
import com.example.weatherapp.domain.repoimpls.ForecastRepository
import retrofit2.Response
import javax.inject.Inject

class ForecastUseCase @Inject constructor(private val repo: ForecastRepository) {

    suspend operator fun invoke(api_key: String, city: String): Response<WeatherResponse>{
        return repo.getWeatherForecast(api_key, city)
    }
}