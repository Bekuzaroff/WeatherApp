package com.example.weatherapp.data.remotedata

import com.example.weatherapp.domain.models.Cities
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CitiesService {
    @GET("/v1/city")
    suspend fun searchCity(@Query("name") name: String, @Header("X-Api-Key") api_key: String): Response<Cities>
}