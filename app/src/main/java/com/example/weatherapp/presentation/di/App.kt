package com.example.weatherapp.presentation.di

import android.app.Application
import javax.inject.Inject

class App(): Application() {

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.builder().appModule(AppModule(context = this)).build()
    }




}