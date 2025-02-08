package com.example.weatherapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.domain.usecases.AddCityUseCase
import com.example.weatherapp.domain.usecases.GetAllCitiesUseCase
import com.example.weatherapp.domain.usecases.RemoveCityUseCase
import com.example.weatherapp.domain.usecases.SearchCitiesUseCase
import com.example.weatherapp.domain.usecases.SearchSavedCitiesUseCase
import javax.inject.Inject

class CitiesVMFactory @Inject constructor(
    val getAllCitiesUseCase: GetAllCitiesUseCase,
    val searchSavedCitiesUseCase: SearchSavedCitiesUseCase,
    val addCityUseCase: AddCityUseCase,
    val removeCityUseCase: RemoveCityUseCase,
    val searchCitiesUseCase: SearchCitiesUseCase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CitiesViewModel(
            getAllCitiesUseCase,
            searchSavedCitiesUseCase,
            addCityUseCase,
            removeCityUseCase,
            searchCitiesUseCase
        ) as T
    }
}