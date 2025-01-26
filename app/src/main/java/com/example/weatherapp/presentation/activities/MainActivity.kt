package com.example.weatherapp.presentation.activities

import android.Manifest
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.presentation.di.App
import com.example.weatherapp.presentation.fragments.HomeFragment
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


class MainActivity : AppCompatActivity() {

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
                NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager, null, null)
                drawer.closeDrawer(Gravity.LEFT)
            }
            tvAddcities.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Add_cities_fr(), supportFragmentManager, null, null
                )
                drawer.closeDrawer(Gravity.LEFT)
            }

            tvSavedcities.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Saved_cities_fr(), supportFragmentManager, null, null
                )
                drawer.closeDrawer(Gravity.LEFT)
            }

            btOpenDrawer.setOnClickListener {
                drawer.openDrawer(Gravity.LEFT)
            }







            //prefs for switch state
            val prefs: SharedPreferences = getSharedPreferences(IN_F_PREF, MODE_PRIVATE)

            //save the beginning state of switch
            fSwitch.isChecked = prefs.getBoolean(IN_F, false)

            //change the state of tempirature (F/C)
            fSwitch.setOnClickListener {
                lifecycleScope.launch {
                    if(fSwitch.isChecked){

                        prefs.edit().putBoolean(IN_F, true).commit()

                    }else{

                        prefs.edit().putBoolean(IN_F, false).commit()

                    }
                }

            }





        }






    }


    companion object{
        const val IN_F_PREF = "in_f_pref"
        const val IN_F = "in_f"
    }





}