package com.example.weatherapp.domain.models

import java.io.Serializable

data class Condition(
    val code: Int,
    val icon: String,
    val text: String
): Serializable