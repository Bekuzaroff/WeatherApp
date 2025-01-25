package com.example.weatherapp.domain.models

import java.io.Serializable

data class Forecast(
    val forecastday: List<Forecastday>
): Serializable