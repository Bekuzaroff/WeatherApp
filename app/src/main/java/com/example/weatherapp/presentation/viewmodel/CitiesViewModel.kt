package com.example.weatherapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.models.Cities
import com.example.weatherapp.domain.models.CitiesItem
import com.example.weatherapp.domain.usecases.AddCityUseCase
import com.example.weatherapp.domain.usecases.GetAllCitiesUseCase
import com.example.weatherapp.domain.usecases.RemoveCityUseCase
import com.example.weatherapp.domain.usecases.SearchCitiesUseCase
import com.example.weatherapp.domain.usecases.SearchSavedCitiesUseCase
import com.example.weatherapp.utils.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CitiesViewModel @Inject constructor(
    val getAllCitiesUseCase: GetAllCitiesUseCase,
    val searchSavedCitiesUseCase: SearchSavedCitiesUseCase,
    val addCityUseCase: AddCityUseCase,
    val removeCityUseCase: RemoveCityUseCase,
    val searchCitiesUseCase: SearchCitiesUseCase
): ViewModel() {

    // database operations
    private val _savedCitiesFlow: MutableStateFlow<List<CitiesItem>> = MutableStateFlow(listOf())
    val savedCitiesFlow: MutableStateFlow<List<CitiesItem>>
        get() = _savedCitiesFlow

    fun getAllCities() = viewModelScope.launch {
        _savedCitiesFlow.emit(getAllCitiesUseCase())
    }

    fun searchSavedCities(query: String) = viewModelScope.launch {
        _savedCitiesFlow.emit(searchSavedCitiesUseCase(query))
    }

    fun addCity(citiesItem: CitiesItem) = viewModelScope.launch {
        addCityUseCase(citiesItem)
    }

    fun removeCity(citiesItem: CitiesItem) = viewModelScope.launch {
        removeCityUseCase(citiesItem)
    }


    //api operations
    private val _citiesFlow: MutableStateFlow<ResourceState<Cities>> = MutableStateFlow(ResourceState.Loading())
    val citiesFlow: MutableStateFlow<ResourceState<Cities>>
        get() = _citiesFlow

    fun searchCities(query: String, apiKey: String) = viewModelScope.launch {
        try {
            _citiesFlow.emit(ResourceState.Loading())

            val response = searchCitiesUseCase(query, apiKey)

            if (response.isSuccessful){
                _citiesFlow.emit(ResourceState.Success(resource = response.body()))
            }else{
                _citiesFlow.emit(ResourceState.Error(m = response.message()))
            }
        }catch (e: Exception){
            Log.d("e in cities vm", e.message.toString())
        }
    }


}