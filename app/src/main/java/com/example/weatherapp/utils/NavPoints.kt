package com.example.weatherapp.utils

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.weatherapp.R
import com.example.weatherapp.presentation.fragments.AddCityFragment
import com.example.weatherapp.presentation.fragments.DetailedFragment
import com.example.weatherapp.presentation.fragments.HomeFragment
import com.example.weatherapp.presentation.fragments.SavedCitiesFragment
import com.example.weatherapp.presentation.fragments.SettingsFragment
import java.io.Serializable

sealed class NavPoints() {

    class Home_fr(val arg: Serializable ?= null): NavPoints()
    class Add_cities_fr(val arg: Serializable ?= null): NavPoints()
    class Saved_cities_fr(val arg: Serializable ?= null): NavPoints()
    class Detailed_fragment(val arg: Serializable ?= null): NavPoints()
    class Settings_fragment(val arg: Serializable ?= null): NavPoints()

    companion object{
        fun navigateTo(navPoints: NavPoints, fr_manager: FragmentManager?,
                       ){
            val bundle = Bundle()

            when(navPoints){
                is Home_fr -> {

                    navPoints.arg?.let { model ->
                        bundle.putSerializable("weather_model", model)
                    }

                    val fr = HomeFragment()
                    fr.arguments = bundle

                    fr_manager!!.beginTransaction().replace(R.id.fragmenthost,
                        fr).commit()

                }
                is Add_cities_fr -> {

                    val fr = AddCityFragment()

                    fr_manager!!.beginTransaction().replace(R.id.fragmenthost,
                        fr).commit()
                }
                is Saved_cities_fr -> {

                    val fr = SavedCitiesFragment()

                    fr_manager!!.beginTransaction().replace(R.id.fragmenthost,
                        fr).commit()
                }
                is Detailed_fragment -> {

                    navPoints.arg?.let { model ->
                        bundle.putSerializable("weather_model", model)
                    }
                    val fr = DetailedFragment()
                    fr.arguments = bundle
                    fr_manager!!.beginTransaction().replace(R.id.fragmenthost,
                        fr).commit()
                }
                is Settings_fragment -> {
                    val fr = SettingsFragment()

                    fr_manager!!.beginTransaction().replace(R.id.fragmenthost,
                        fr).commit()
                }
            }
        }
    }



}