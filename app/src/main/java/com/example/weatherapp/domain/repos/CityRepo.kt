package com.example.weatherapp.domain.repos

import com.example.weatherapp.domain.models.Cities
import com.example.weatherapp.domain.models.CitiesItem
import retrofit2.Response


interface CityRepo {
    //db
    suspend fun getAllCities(): List<CitiesItem>

    suspend fun addCity(citiesItem: CitiesItem)

    suspend fun removeCity(citiesItem: CitiesItem)

    suspend fun searchSavedCities(query: String): List<CitiesItem>

    //api
    suspend fun searchCities(query: String, apiKey: String): Response<Cities>
}