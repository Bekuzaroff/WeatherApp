package com.example.weatherapp.presentation.activities

import android.Manifest
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.presentation.fragments.HomeFragment
import com.example.weatherapp.utils.NavPoints
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var location_client: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        location_client = LocationServices.getFusedLocationProviderClient(this)


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





        // GETTING LOCATION PERMISSIONS
        val location_perm_request = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){perms ->
            when {
                perms.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION,
                    false) || perms.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION
                    ,false) -> {
                        Toast.makeText(this, R.string.location_access_granted,Toast.LENGTH_LONG).show()
                        if(isLocationEnabled()){
                            val result = location_client.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                CancellationTokenSource().token
                            )
                            result.addOnCompleteListener {
                                val geoCoder = Geocoder(this, Locale.getDefault())
                                val address = geoCoder.getFromLocation(it.result.latitude,it.result.longitude,1)
                                if (address != null){
                                    var cityName = address[0].locality
                                    Log.d("tagg", "${cityName}")
                                }

                            }
                        }else{
                            Toast.makeText(this, R.string.please_turn_the_location_on,Toast.LENGTH_LONG).show()
                        }
                    }
                else -> {
                    Toast.makeText(this, R.string.no_location_access,Toast.LENGTH_LONG).show()
                }
            }

        }

        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        location_perm_request.launch(
            perms
        )



    }

    // IS GPS SERVICE IS ENABLED
    private fun isLocationEnabled(): Boolean{
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager

        try{
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return false
    }





}