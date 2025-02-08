package com.example.weatherapp.domain.usecases

import com.example.weatherapp.domain.models.CitiesItem
import com.example.weatherapp.domain.repos.CityRepo
import javax.inject.Inject

class AddCityUseCase @Inject constructor(
    val repo: CityRepo
) {

    suspend operator fun invoke(cityItem: CitiesItem) = repo.addCity(cityItem)
}