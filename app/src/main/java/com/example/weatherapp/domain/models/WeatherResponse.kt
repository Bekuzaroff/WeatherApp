package com.example.weatherapp.domain.models

import java.io.Serializable

data class WeatherResponse(
    val current: Current,
    val forecast: Forecast,
    val location: Location
): Serializable