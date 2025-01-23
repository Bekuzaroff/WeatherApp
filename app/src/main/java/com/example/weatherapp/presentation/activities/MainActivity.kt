package com.example.weatherapp.presentation.activities

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.presentation.fragments.HomeFragment
import com.example.weatherapp.utils.NavPoints

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // start fragment
        NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager, null, null)
        binding.apply {
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
        }

    }


}