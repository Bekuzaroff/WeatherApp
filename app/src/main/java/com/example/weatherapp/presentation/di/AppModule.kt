package com.example.weatherapp.presentation.di

import android.content.Context
import com.example.weatherapp.domain.usecases.ForecastUseCase
import com.example.weatherapp.presentation.viewmodel.ApiVMFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule (val context: Context) {

    @Provides
    fun provideContext(): Context = context


    @Provides
    @Singleton
    fun provideApiVMFactory(
        forecastUseCase: ForecastUseCase
    ): ApiVMFactory{
        return ApiVMFactory(forecastUseCase)
    }
}