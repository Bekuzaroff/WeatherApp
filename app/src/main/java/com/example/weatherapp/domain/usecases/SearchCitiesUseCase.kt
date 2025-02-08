package com.example.weatherapp.domain.usecases

import com.example.weatherapp.domain.models.Cities
import com.example.weatherapp.domain.repos.CityRepo
import retrofit2.Response
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
    val repo: CityRepo
) {
    suspend operator fun invoke(query: String): Response<Cities>{
        return repo.searchCities(query)
    }
}