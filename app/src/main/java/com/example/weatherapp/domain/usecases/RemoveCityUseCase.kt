package com.example.weatherapp.domain.usecases

import com.example.weatherapp.domain.models.CitiesItem
import com.example.weatherapp.domain.repos.CityRepo
import javax.inject.Inject

class RemoveCityUseCase @Inject constructor(
    val repo: CityRepo
) {
    suspend operator fun invoke(citiesItem: CitiesItem) = repo.removeCity(citiesItem)
}