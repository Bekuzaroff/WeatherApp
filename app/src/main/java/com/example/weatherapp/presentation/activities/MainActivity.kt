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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.models.Forecastday
import com.example.weatherapp.domain.models.WeatherResponse
import com.example.weatherapp.presentation.adapters.RcDaysAdapter
import com.example.weatherapp.presentation.di.App
import com.example.weatherapp.presentation.fragments.HomeFragment
import com.example.weatherapp.presentation.fragments.HomeFragment.Companion.CITY_NAME
import com.example.weatherapp.presentation.fragments.HomeFragment.Companion.WEATHER_MODEL
import com.example.weatherapp.presentation.fragments.HomeFragment.Companion.WEATHER_PREF_MODEL
import com.example.weatherapp.presentation.viewmodel.ApiVMFactory
import com.example.weatherapp.presentation.viewmodel.ApiViewModel
import com.example.weatherapp.utils.NavPoints
import com.example.weatherapp.utils.consts.API_KEY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


class MainActivity : AppCompatActivity(), RcDaysAdapter.ClickEvents {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var vm_factory: ApiVMFactory

    private lateinit var vm: ApiViewModel

    private var weather_prefs: SharedPreferences ?= null

    private val gson = Gson()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        (application as App).component.inject(this)

        setContentView(binding.root)
        weather_prefs = getSharedPreferences(WEATHER_PREF_MODEL, MODE_PRIVATE)
        vm = ViewModelProvider(this, vm_factory).get(ApiViewModel::class)


        // start fragment
        NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager)

        //navigation with drawer
        binding.apply {
            //getting from nav points current navigation
            // and go there by clicking the textview from drawer menu
            tvHome.setOnClickListener {
                NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager,
                    )
                drawer.closeDrawer(Gravity.LEFT)
            }
            tvAddcities.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Add_cities_fr(), supportFragmentManager,

                )
                drawer.closeDrawer(Gravity.LEFT)
            }

            tvSavedcities.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Saved_cities_fr(), supportFragmentManager,

                )
                drawer.closeDrawer(Gravity.LEFT)
            }
            tvSettings.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Settings_fragment(), supportFragmentManager,

                    )
                drawer.closeDrawer(Gravity.LEFT)
            }

            btOpenDrawer.setOnClickListener {
                drawer.openDrawer(Gravity.LEFT)
            }

            binding.btBackDetailes.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Home_fr(), supportFragmentManager

                    )

                binding.btOpenDrawer.visibility = View.VISIBLE
                binding.edSearchCity.visibility = View.VISIBLE
                binding.btSearchCity.visibility = View.VISIBLE
                binding.btBackDetailes.visibility = View.GONE


            }

            btSearchCity.setOnClickListener {
                val city = edSearchCity.text

                if (city.isNotEmpty()){
                    vm.getWeatherForecast(API_KEY, city.toString())
                    btSearchCity.visibility = View.GONE
                    edSearchCity.visibility = View.GONE
                    btOpenDrawer.visibility = View.GONE
                    btBackSearchCity.visibility = View.VISIBLE
                    edSearchCity.text.clear()

                    weather_prefs?.let {
                        it.edit().putBoolean(WAS_SEARCHED, true).commit()
                    }

                }else{
                    Toast.makeText(this@MainActivity,
                        R.string.you_not_enter_city_name,
                        Toast.LENGTH_LONG).show()
                }

            }

            btBackSearchCity.setOnClickListener {
                btSearchCity.visibility = View.VISIBLE
                edSearchCity.visibility = View.VISIBLE
                btOpenDrawer.visibility = View.VISIBLE
                btBackSearchCity.visibility = View.GONE

                NavPoints.navigateTo(
                    NavPoints.Home_fr(), supportFragmentManager

                )

                weather_prefs?.let { prefs ->
                    val city_name = prefs.getString(CITY_NAME, "")
                    if (city_name != null) {
                        vm.getWeatherForecast(API_KEY, city_name)
                    }
                    prefs.edit().remove(WEATHER_MODEL).commit()
                }


            }

        }

    }

    override fun onStop() {
        super.onStop()
        weather_prefs?.let {
            it.edit().remove(WEATHER_MODEL).commit()
        }
    }






    override fun itemClick(day: Forecastday) {
        binding.btOpenDrawer.visibility = View.GONE
        binding.edSearchCity.visibility = View.GONE
        binding.btSearchCity.visibility = View.GONE
        weather_prefs?.let { it
            if (it.getBoolean(WAS_SEARCHED, false)){
                binding.btBackSearchCity.visibility = View.VISIBLE
                binding.btBackDetailes.visibility = View.GONE
            }else{
                binding.btBackSearchCity.visibility = View.GONE
                binding.btBackDetailes.visibility = View.VISIBLE
            }
        }

        NavPoints.navigateTo(NavPoints.Detailed_fragment(arg = day), supportFragmentManager)
    }

    companion object {
        const val WAS_SEARCHED = "WAS_SEARCHED"
    }

}