package com.example.weatherapp.presentation.activities

import android.Manifest
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.models.Forecastday
import com.example.weatherapp.presentation.adapters.RcDaysAdapter
import com.example.weatherapp.presentation.di.App
import com.example.weatherapp.presentation.fragments.HomeFragment
import com.example.weatherapp.presentation.fragments.HomeFragment.Companion.WEATHER_MODEL
import com.example.weatherapp.presentation.fragments.HomeFragment.Companion.WEATHER_PREF_MODEL
import com.example.weatherapp.utils.NavPoints
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity(), RcDaysAdapter.ClickEvents {

    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)




        setContentView(binding.root)


        // start fragment
        NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager, null, null)

        //navigation with drawer
        binding.apply {
            //getting from nav points current navigation
            // and go there by clicking the textview from drawer menu
            tvHome.setOnClickListener {
                NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager, null, null,
                    )
                drawer.closeDrawer(Gravity.LEFT)
            }
            tvAddcities.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Add_cities_fr(), supportFragmentManager, null, null,

                )
                drawer.closeDrawer(Gravity.LEFT)
            }

            tvSavedcities.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Saved_cities_fr(), supportFragmentManager, null, null,

                )
                drawer.closeDrawer(Gravity.LEFT)
            }

            btOpenDrawer.setOnClickListener {
                drawer.openDrawer(Gravity.LEFT)
            }







            binding.btBackDetailes.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Home_fr(), supportFragmentManager, null, null,

                    )

                binding.btOpenDrawer.visibility = View.VISIBLE
                binding.edSearchCity.visibility = View.VISIBLE
                binding.btSearchCity.visibility = View.VISIBLE
                binding.btBackDetailes.visibility = View.GONE
            }





        }






    }

    override fun onStop() {
        super.onStop()
        val weather_prefs = getSharedPreferences(WEATHER_PREF_MODEL, MODE_PRIVATE)
        weather_prefs.edit().remove(WEATHER_MODEL).commit()
    }






    override fun itemClick(day: Forecastday) {
        binding.btOpenDrawer.visibility = View.GONE
        binding.edSearchCity.visibility = View.GONE
        binding.btSearchCity.visibility = View.GONE
        binding.btBackDetailes.visibility = View.VISIBLE
        NavPoints.navigateTo(NavPoints.Detailed_fragment(arg = day), supportFragmentManager,intent, this)
    }


}