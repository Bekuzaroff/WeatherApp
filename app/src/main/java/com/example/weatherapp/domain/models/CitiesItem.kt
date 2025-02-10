package com.example.weatherapp.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cities_table")
data class CitiesItem(
    val country: String,
    val is_capital: Boolean,
    val latitude: Double,
    val longitude: Double,
    @PrimaryKey
    val name: String,
    val population: Int,
    val region: String
): Serializable