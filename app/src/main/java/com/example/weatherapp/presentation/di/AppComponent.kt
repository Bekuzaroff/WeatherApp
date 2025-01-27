package com.example.weatherapp.presentation.di

import com.example.weatherapp.presentation.activities.MainActivity
import com.example.weatherapp.presentation.fragments.HomeFragment
import dagger.Component

@Component(modules = [DataModule::class, DomainModule::class, AppModule::class])
interface AppComponent {
    fun inject(homeFragment: HomeFragment)
    fun inject(main_activity: MainActivity)
}