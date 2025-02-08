package com.example.weatherapp.presentation.di

import com.example.weatherapp.presentation.activities.MainActivity
import com.example.weatherapp.presentation.fragments.HomeFragment
import com.example.weatherapp.presentation.fragments.SavedCitiesFragment
import dagger.Component

@Component(modules = [DataModule::class, DomainModule::class, AppModule::class])
interface AppComponent {
    fun inject(homeFragment: HomeFragment)
    fun inject(activity: MainActivity)
    fun inject(savedCitiesFragment: SavedCitiesFragment)
}