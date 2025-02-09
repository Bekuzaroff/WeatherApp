package com.example.weatherapp.presentation.activities

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.models.Forecastday
import com.example.weatherapp.presentation.adapters.RcDaysAdapter
import com.example.weatherapp.presentation.di.App
import com.example.weatherapp.presentation.viewmodel.ApiVMFactory
import com.example.weatherapp.presentation.viewmodel.ApiViewModel
import com.example.weatherapp.presentation.viewmodel.CitiesVMFactory
import com.example.weatherapp.presentation.viewmodel.CitiesViewModel
import com.example.weatherapp.utils.NavPoints
import com.example.weatherapp.utils.consts.API_KEY
import com.example.weatherapp.utils.consts.API_KEY_CITY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


class MainActivity : AppCompatActivity(), RcDaysAdapter.ClickEvents {

    //ADDRESS FOR GETTING CITY NAME
    private var address: MutableList<Address>? = null

    private var _binding: ActivityMainBinding ?= null
    private val binding: ActivityMainBinding
        get() = _binding!!


    private lateinit var location_client: FusedLocationProviderClient

    var settings_prefs: SharedPreferences ?= null

    @Inject
    lateinit var apiVMFactory: ApiVMFactory
    val apiViewModel by lazy {
        apiVMFactory.create(ApiViewModel::class.java)
    }

    @Inject
    lateinit var citiesVMFactory: CitiesVMFactory

    val citiesViewModel by lazy {
        citiesVMFactory.create(CitiesViewModel::class.java)
    }

    var cityName: MutableStateFlow<String> = MutableStateFlow("")

    private var was_searched: Boolean = false
    private var which_fragment: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO HERE ALL THE INITS
        _binding = ActivityMainBinding.inflate(layoutInflater)

        (application as App).component.inject(this)

        location_client = LocationServices.getFusedLocationProviderClient(this)

        settings_prefs = getSharedPreferences(CACHE_PREFS, MODE_PRIVATE)
        setContentView(binding.root)

        //COLLECTING THE CITY NAME AND GET WEATHER FOR THIS CITY
        lifecycleScope.launch {
            cityName.collect {
                apiViewModel.getWeatherForecast(API_KEY, it)

            }
        }


        //TODO LAUNCHER FOR REQUEST PERMISSIONS
        val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){perms->

            val coarse = perms[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            val fine = perms[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

            if (fine && coarse){

                if (isOnline()) {
                    val result = location_client.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        null
                    )


                    //when getting location is completed we get the city from location with Geocoder
                    result.addOnCompleteListener {
                        val geoCoder = Geocoder(this, Locale.getDefault())
                        address =
                            geoCoder.getFromLocation(it.result.latitude, it.result.longitude, 1)

                        if (address != null) {
                            cityName.value = address!![0].adminArea

                            if (cityName.value == null) {
                                cityName.value = address!![0].locality

                                if (cityName.value == null) {
                                    cityName.value = address!![0].subAdminArea

                                    if (cityName.value == null) {
                                        cityName.value = "London"
                                    }
                                }
                            }
                        }

                    }
                }else{
                    cityName.value = "London"
                }
            }else{
                Toast.makeText(this, "you should enable your location in settings for normal app functionality"
                ,Toast.LENGTH_LONG).show()
            }

        }

        //TODO CHECK PERMISSIONS AND REQUEST IF THERE AREN'T
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
                if (isOnline()) {
                    val result = location_client.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        null
                    )

                    //when getting location is completed we get the city from location with Geocoder
                    result.addOnCompleteListener {
                        val geoCoder = Geocoder(this, Locale.getDefault())
                        address = geoCoder.getFromLocation(it.result.latitude, it.result.longitude, 1)
                        //GETTING THE ADDRESS AND THE CITY FROM IT
                        if (address != null) {
                            //WHEN WE CHANGE THE VALUE OF CITY NAME, IT COLLECTS IT AND GETS THA API RESPONSE
                            cityName.value = address!![0].adminArea

                            if (cityName.value == null) {
                                cityName.value = address!![0].locality

                                if (cityName.value == null) {
                                    cityName.value = address!![0].subAdminArea
                                }
                            }

                        }

                    }
                }else{
                    cityName.value = "London"
                }
        }else{
            //IF PERMISSIONS ARE NOT GRANTED, WE LAUNCH THE REQUEST PERMISSION LAUNCHER
            launcher.launch(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }





        //TODO HERE ALL THE UI LOGIC

        // start fragment
        NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager)

        //navigation with drawer
        binding.apply {
            //THE MENU NAVIGATION DRAWER LOGIC
            tvHome.setOnClickListener {
                NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager,
                    )
                drawer.closeDrawer(Gravity.LEFT)
                which_fragment = 1
            }
            tvAddcities.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Add_cities_fr(), supportFragmentManager,

                )
                drawer.closeDrawer(Gravity.LEFT)
                which_fragment = 2
            }

            tvSavedcities.setOnClickListener {
                NavPoints.navigateTo(
                    NavPoints.Saved_cities_fr(), supportFragmentManager,

                )
                drawer.closeDrawer(Gravity.LEFT)
                which_fragment = 3
            }


            btOpenDrawer.setOnClickListener {
                drawer.openDrawer(Gravity.LEFT)
            }

            btBackDetailes.setOnClickListener {

                NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager)

                //THE LOGIC FOR GETTING BACK FROM ANOTHER CITY WEATHER TO USER'S CITY WEATHER, WHEN
                //USER GOES BACK FROM DETAILED FRAGMENT
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

                //WE GET THE CITY NAME
                val ed_city = edSearchCity.text.toString()
                if (ed_city.isNotEmpty()){
                    //WE CHANGE THE OBSERVABLE VALUE OF CITY NAME AND GET THE RESPONSE FOR THIS CITYNAME
                    was_searched = true

                    btOpenDrawer.visibility = View.GONE
                    edSearchCity.visibility = View.GONE
                    btSearchCity.visibility = View.GONE
                    btBackFromSearch.visibility = View.VISIBLE
                    if (which_fragment == 1){
                        cityName.value = ed_city
                    }else if (which_fragment == 2){
                        citiesViewModel.searchCities(ed_city, API_KEY_CITY)
                    }else if (which_fragment == 3){
                    }

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

                if (which_fragment == 1){
                    was_searched = false

                    //WE GET BACK THE RESPONSE FOR CURRENT USER CITY
                    if (address != null) {
                        cityName.value = address!![0].adminArea

                        if (cityName.value == null){
                            cityName.value = address!![0].locality

                            if (cityName.value == null){
                                cityName.value = address!![0].subAdminArea
                            }
                        }
                    }

                    NavPoints.navigateTo(NavPoints.Home_fr(), supportFragmentManager)
                }else if (which_fragment == 2){
                    was_searched = false
                }else if (which_fragment == 3){
                    was_searched = false
                }
            }




        }


    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }



    //WHEN WE CLICK THE ITEM OF WEATHER FOR NEXT DAYS WE DO THIS

    override fun itemClick(day: Forecastday) {
        binding.btOpenDrawer.visibility = View.GONE
        binding.edSearchCity.visibility = View.GONE
        binding.btSearchCity.visibility = View.GONE
        binding.btBackFromSearch.visibility = View.GONE
        binding.btBackDetailes.visibility = View.VISIBLE

        NavPoints.navigateTo(NavPoints.Detailed_fragment(arg = day), supportFragmentManager)
    }

    companion object{
        const val CACHE_PREFS = "CACHE_PREFS"
    }



}