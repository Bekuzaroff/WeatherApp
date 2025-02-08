package com.example.weatherapp.data.reposimpls

import com.example.weatherapp.data.localdata.CityDao
import com.example.weatherapp.data.remotedata.CitiesService
import com.example.weatherapp.domain.models.Cities
import com.example.weatherapp.domain.models.CitiesItem
import com.example.weatherapp.domain.repos.CityRepo
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class CityRepoImpl @Inject constructor(val dao: CityDao,
    val citiesService: CitiesService): CityRepo {
    //db methods
    override suspend fun getAllCities(): List<CitiesItem> = dao.getAllCities()
    override suspend fun addCity(citiesItem: CitiesItem) = dao.addCity(citiesItem)

    override suspend fun removeCity(citiesItem: CitiesItem) = dao.removeCity(citiesItem)
    override suspend fun searchSavedCities(query: String) = dao.searchCities(query)


    //api methods
    override suspend fun searchCities(query: String): Response<Cities> = citiesService.searchCity(query)

}