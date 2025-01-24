package com.example.weatherapp.presentation.di

import android.app.Application

class App(): Application() {

    private lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.builder().appModule(AppModule(context = this)).build()
    }
}