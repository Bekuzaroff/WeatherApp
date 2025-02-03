package com.example.weatherapp.presentation.activities

import android.Manifest
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.models.Forecastday
import com.example.weatherapp.presentation.adapters.RcDaysAdapter
import com.example.weatherapp.presentation.di.App
import com.example.weatherapp.presentation.viewmodel.ApiVMFactory
import com.example.weatherapp.presentation.viewmodel.ApiViewModel
import com.example.weatherapp.utils.NavPoints
import com.example.weatherapp.utils.consts.API_KEY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale
import javax.inject.Inject


class MainActivity : AppCompatActivity(), RcDaysAdapter.ClickEvents {

    private var _binding: ActivityMainBinding ?= null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private lateinit var location_client: FusedLocationProviderClient

    @Inject
    lateinit var vm_factory: ApiVMFactory
    lateinit var vm: ApiViewModel

    var cityName: String = ""
    var ed_city: String = ""

    var was_searched: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        (application as App).component.inject(this)

        //VIEWMODEL INIT
        vm = ViewModelProvider(this, vm_factory).get(ApiViewModel::class)

        location_client = LocationServices.getFusedLocationProviderClient(this)


        setContentView(binding.root)


        // GETTING LOCATION PERMISSIONS
        val location_perm_request = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){perms ->
            when {
                perms.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    false) || perms.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    ,false) -> {
                    //WHEN LOCATION IS GRANTED
                    //check if the gps is enabled so we get current user location
                    if(isLocationEnabled()){
                        val result = location_client.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            CancellationTokenSource().token
                        )

                        //when getting location is completed we get the city from location with Geocoder
                        result.addOnCompleteListener {
                            val geoCoder = Geocoder(this, Locale.getDefault())
                            val address = geoCoder.getFromLocation(it.result.latitude,it.result.longitude,1)
                            if (address != null){
                                cityName = address[0].locality
                                vm.getWeatherForecast(API_KEY, cityName)
                            }

                        }
                    }else{
                        cityName = "London"
                        vm.getWeatherForecast(API_KEY, cityName)
                        Toast.makeText(this, R.string.please_turn_the_location_on, Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    cityName = "London"
                    vm.getWeatherForecast(API_KEY, cityName)
                    Toast.makeText(this, R.string.no_location_access, Toast.LENGTH_LONG).show()
                }
            }

        }

        //array of needed permissions
        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        location_perm_request.launch(
            perms
        )

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

            btBackDetailes.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Home_fr(), supportFragmentManager

                    )

               if (was_searched){
                   btBackDetailes.visibility = View.GONE
                   btBackFromSearch.visibility = View.VISIBLE
               }else{
                   btOpenDrawer.visibility = View.VISIBLE
                   edSearchCity.visibility = View.VISIBLE
                   btSearchCity.visibility = View.VISIBLE
                   btBackDetailes.visibility = View.GONE
               }

            }


            btSearchCity.setOnClickListener {
                was_searched = true

                ed_city = edSearchCity.text.toString()
                if (ed_city.isNotEmpty()){
                    vm.getWeatherForecast(API_KEY, ed_city)

                    btOpenDrawer.visibility = View.GONE
                    edSearchCity.visibility = View.GONE
                    btSearchCity.visibility = View.GONE
                    btBackFromSearch.visibility = View.VISIBLE


                }else{
                    Toast.makeText(this@MainActivity, "sorry but you did not input the city name",
                        Toast.LENGTH_LONG).show()
                }

            }

            btBackFromSearch.setOnClickListener {
                btOpenDrawer.visibility = View.VISIBLE
                edSearchCity.visibility = View.VISIBLE
                btSearchCity.visibility = View.VISIBLE
                btBackFromSearch.visibility = View.GONE
                edSearchCity.text.clear()
                was_searched = false

                vm.getWeatherForecast(API_KEY, cityName)
                NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager)
            }




        }






    }


    override fun itemClick(day: Forecastday) {
        binding.btOpenDrawer.visibility = View.GONE
        binding.edSearchCity.visibility = View.GONE
        binding.btSearchCity.visibility = View.GONE
        binding.btBackFromSearch.visibility = View.GONE
        binding.btBackDetailes.visibility = View.VISIBLE
        NavPoints.navigateTo(NavPoints.Detailed_fragment(arg = day), supportFragmentManager)
    }


    // IS GPS SERVICE IS ENABLED
    private fun isLocationEnabled(): Boolean{
        //getting location manager
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager

        //checking if the gps is enabled
        try{
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return false
    }


}