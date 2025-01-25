package com.example.weatherapp.presentation.di

import com.example.weatherapp.data.remotedata.WeatherService
import com.example.weatherapp.data.repositories.ForecastRepositoryImpl
import com.example.weatherapp.domain.repoimpls.ForecastRepository
import com.example.weatherapp.domain.usecases.ForecastUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {

    @Provides
    fun provideForecastRepository(service: WeatherService): ForecastRepository
    = ForecastRepositoryImpl(service)

    @Provides
    fun provideForecastUseCase(repository: ForecastRepository): ForecastUseCase
        = ForecastUseCase(repository)

}