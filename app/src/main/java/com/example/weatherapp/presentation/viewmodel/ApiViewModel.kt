package com.example.weatherapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.models.WeatherResponse
import com.example.weatherapp.domain.usecases.ForecastUseCase
import com.example.weatherapp.utils.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException
import javax.inject.Inject

class ApiViewModel @Inject constructor(
    private val forecastUseCase: ForecastUseCase
): ViewModel() {

    private val _weather_flow:MutableStateFlow<ResourceState<WeatherResponse>> =
        MutableStateFlow(ResourceState.Loading())
    val weather_flow: MutableStateFlow<ResourceState<WeatherResponse>>
        get() = _weather_flow


    fun getWeatherForecast(
        api_key: String,
        city: String,
    ) = viewModelScope.launch {
        _weather_flow.emit(ResourceState.Loading())

        try {
            val response = forecastUseCase(api_key = api_key, city = city)

            if (response.isSuccessful){
                _weather_flow.emit(ResourceState.Success(resource = response.body()))
            }else{
                _weather_flow.emit(ResourceState.Error(m = response.message()))
            }
        }catch (e: Exception){
            when(e){
                is UnknownHostException -> {
                    _weather_flow.emit(ResourceState.Error(m = "you don't have internet connection"))
                }
            }
        }

    }
}