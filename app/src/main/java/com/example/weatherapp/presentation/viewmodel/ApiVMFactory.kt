package com.example.weatherapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.domain.usecases.ForecastUseCase
import javax.inject.Inject

class ApiVMFactory @Inject constructor(
    private val forecastUseCase: ForecastUseCase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ApiViewModel(forecastUseCase) as T
    }
}