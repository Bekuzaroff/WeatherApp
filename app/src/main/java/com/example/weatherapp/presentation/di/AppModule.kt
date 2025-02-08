package com.example.weatherapp.presentation.di

import android.content.Context
import com.example.weatherapp.domain.usecases.AddCityUseCase
import com.example.weatherapp.domain.usecases.ForecastUseCase
import com.example.weatherapp.domain.usecases.GetAllCitiesUseCase
import com.example.weatherapp.domain.usecases.RemoveCityUseCase
import com.example.weatherapp.domain.usecases.SearchCitiesUseCase
import com.example.weatherapp.domain.usecases.SearchSavedCitiesUseCase
import com.example.weatherapp.presentation.viewmodel.ApiVMFactory
import com.example.weatherapp.presentation.viewmodel.CitiesVMFactory
import dagger.Module
import dagger.Provides

@Module
class AppModule (val context: Context) {

    @Provides
    fun provideContext(): Context = context



    @Provides
    fun provideApiVMFactory(
        forecastUseCase: ForecastUseCase
    ): ApiVMFactory{
        return ApiVMFactory(forecastUseCase)
    }

    @Provides
    fun provideCitiesVMFactory(
        getAllCitiesUseCase: GetAllCitiesUseCase,
        searchSavedCitiesUseCase: SearchSavedCitiesUseCase,
        addCityUseCase: AddCityUseCase,
        removeCityUseCase: RemoveCityUseCase,
        searchCitiesUseCase: SearchCitiesUseCase
    ): CitiesVMFactory {
        return CitiesVMFactory(
            getAllCitiesUseCase,
            searchSavedCitiesUseCase,
            addCityUseCase,
            removeCityUseCase,
            searchCitiesUseCase
        )
    }
}