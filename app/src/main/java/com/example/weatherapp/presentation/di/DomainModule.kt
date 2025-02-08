package com.example.weatherapp.presentation.di

import com.example.weatherapp.data.localdata.CityDao
import com.example.weatherapp.data.remotedata.CitiesService
import com.example.weatherapp.data.remotedata.WeatherService
import com.example.weatherapp.data.reposimpls.CityRepoImpl
import com.example.weatherapp.data.reposimpls.ForecastRepositoryImpl
import com.example.weatherapp.domain.repos.CityRepo
import com.example.weatherapp.domain.repos.ForecastRepository
import com.example.weatherapp.domain.usecases.AddCityUseCase
import com.example.weatherapp.domain.usecases.ForecastUseCase
import com.example.weatherapp.domain.usecases.GetAllCitiesUseCase
import com.example.weatherapp.domain.usecases.RemoveCityUseCase
import com.example.weatherapp.domain.usecases.SearchCitiesUseCase
import com.example.weatherapp.domain.usecases.SearchSavedCitiesUseCase
import dagger.Module
import dagger.Provides

@Module
class DomainModule {

    @Provides
    fun provideForecastRepository(service: WeatherService): ForecastRepository
    = ForecastRepositoryImpl(service)

    @Provides
    fun provideForecastUseCase(repository: ForecastRepository): ForecastUseCase
        = ForecastUseCase(repository)



    @Provides
    fun provideSearchCitiesUseCase(repo: CityRepo): SearchCitiesUseCase = SearchCitiesUseCase(repo)



    //db provides

    @Provides
    fun provideCityRepo(dao: CityDao, citiesService: CitiesService): CityRepo = CityRepoImpl(dao, citiesService)

    @Provides
    fun provideAddCityUC(repo: CityRepo): AddCityUseCase = AddCityUseCase(repo)

    @Provides
    fun provideRemoveCityUC(repo: CityRepo): RemoveCityUseCase = RemoveCityUseCase(repo)

    @Provides
    fun provideGetAllCitiesUC(repo: CityRepo): GetAllCitiesUseCase = GetAllCitiesUseCase(repo)

    @Provides
    fun provideSearchSavedCitiesUC(repo: CityRepo): SearchSavedCitiesUseCase = SearchSavedCitiesUseCase(repo)
}