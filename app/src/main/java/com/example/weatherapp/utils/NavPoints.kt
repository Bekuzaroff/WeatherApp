package com.example.weatherapp.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.weatherapp.R
import com.example.weatherapp.presentation.activities.DetailedActivity
import com.example.weatherapp.presentation.fragments.AddCityFragment
import com.example.weatherapp.presentation.fragments.HomeFragment
import com.example.weatherapp.presentation.fragments.SavedCitiesFragment
import java.io.Serializable

sealed class NavPoints() {

    class Home_fr(val arg: Serializable ?= null): NavPoints()
    class Add_cities_fr(val arg: Serializable ?= null): NavPoints()
    class Saved_cities_fr(val arg: Serializable ?= null): NavPoints()
    class Detailed_actitity(val arg: Serializable ?= null): NavPoints()

    companion object{
        fun navigateTo(navPoints: NavPoints, fr_manager: FragmentManager?,
                       intent: Intent?, context: AppCompatActivity?){
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
                is Detailed_actitity -> {
                    navPoints.arg?.let { model ->
                        bundle.putSerializable("weather_model", model)
                    }

                    val intent = Intent(context, DetailedActivity::class.java)

                    context?.let{
                        it.startActivity(intent, bundle)
                    }
                }
            }
        }
    }
}