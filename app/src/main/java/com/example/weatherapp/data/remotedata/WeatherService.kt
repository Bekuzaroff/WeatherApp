package com.example.weatherapp.data.remotedata

import com.example.weatherapp.domain.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast.json")
    suspend fun getWeatherForecast(@Query("key") api_key: String,
                                   @Query("city") city: String,
                                   @Query("days") days: Int = 7,
                                   @Query("aqi") aqi: String = "no",
                                   @Query("alerts") alerts: String = "no"): Response<WeatherResponse>
}