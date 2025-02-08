package com.example.weatherapp.domain.usecases

import com.example.weatherapp.domain.models.CitiesItem
import com.example.weatherapp.domain.repos.CityRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCitiesUseCase @Inject constructor(
    val repo: CityRepo
) {
    suspend operator fun invoke(): List<CitiesItem>{
        return repo.getAllCities()
    }

}