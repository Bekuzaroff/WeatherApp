package com.example.weatherapp.presentation.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


class MainActivity : AppCompatActivity(), RcDaysAdapter.ClickEvents {

    private var address: MutableList<Address>? = null
    private var _binding: ActivityMainBinding ?= null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private lateinit var location_client: FusedLocationProviderClient

    @Inject
    lateinit var vm_factory: ApiVMFactory
    lateinit var vm: ApiViewModel

    var cityName: MutableStateFlow<String> = MutableStateFlow("")

    var was_searched: Boolean = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        (application as App).component.inject(this)

        //VIEWMODEL INIT
        vm = ViewModelProvider(this, vm_factory).get(ApiViewModel::class)

        location_client = LocationServices.getFusedLocationProviderClient(this)


        setContentView(binding.root)

        lifecycleScope.launch {
            cityName.collect {
                vm.getWeatherForecast(API_KEY, it)

            }
        }


        val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){perms->
            val coarse = perms[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            val fine = perms[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

            if (fine && coarse){
                val result = location_client.getLastLocation(
                )

                //when getting location is completed we get the city from location with Geocoder
                result.addOnCompleteListener {
                    val geoCoder = Geocoder(this, Locale.getDefault())
                    address = geoCoder.getFromLocation(it.result.latitude,it.result.longitude,1)
                    if (address != null){
                        cityName.value = address!![0].adminArea
                        if (cityName.value == null){
                            cityName.value = address!![0].locality
                            if (cityName.value == null){
                                cityName.value = address!![0].subAdminArea
                            }
                        }
                    }

                }
            }else{
                Toast.makeText(this, "you should enable your location in settings for normal app functionality"
                ,Toast.LENGTH_LONG).show()
            }

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
                val result = location_client.getLastLocation(
                )

                //when getting location is completed we get the city from location with Geocoder
                result.addOnCompleteListener {
                    val geoCoder = Geocoder(this, Locale.getDefault())
                    address = geoCoder.getFromLocation(it.result.latitude,it.result.longitude,1)
                    if (address != null){
                        cityName.value = address!![0].adminArea
                        if (cityName.value == null){
                            cityName.value = address!![0].locality
                            if (cityName.value == null){
                                cityName.value = address!![0].subAdminArea
                            }
                        }
                    }

                }

        }else{
            launcher.launch(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }
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


                val ed_city = edSearchCity.text.toString()
                if (ed_city.isNotEmpty()){
                    cityName.value = ed_city
                    was_searched = true
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

    companion object{
        const val REQUEST_CODE_LOCATION = 111
    }

}