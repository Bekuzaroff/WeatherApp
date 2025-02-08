package com.example.weatherapp.presentation.di

import android.content.Context
import com.example.weatherapp.data.localdata.Citydb
import com.example.weatherapp.data.remotedata.CitiesService
import com.example.weatherapp.data.remotedata.WeatherService
import com.example.weatherapp.utils.consts.BASE_URL
import com.example.weatherapp.utils.consts.BASE_URL_CITY
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class DataModule {

    //api
    @Named("WeatherRetrofit")
    @Provides
    fun provideWeatherRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideWeatherAPIService(@Named("WeatherRetrofit") weatherRetrofit: Retrofit): WeatherService {
        return weatherRetrofit.create(WeatherService::class.java)
    }

    @Named("CitiesRetrofit")
    @Provides
    fun provideCitiesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_CITY)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideCitiesAPIService(@Named("CitiesRetrofit") citiesRetrofit: Retrofit): CitiesService{
        return citiesRetrofit.create(CitiesService::class.java)
    }

    //db
    @Provides
    fun provideDb(context: Context): Citydb = Citydb.getDb(context)

    @Provides
    fun provideDao(db: Citydb) = db.getDao()






}