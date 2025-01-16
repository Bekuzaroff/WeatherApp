package com.example.weatherapp.domain.models

data class WeatherResponse(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)