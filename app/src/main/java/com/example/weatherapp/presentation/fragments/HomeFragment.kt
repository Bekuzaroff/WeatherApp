package com.example.weatherapp.presentation.fragments

import android.Manifest
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.LOCATION_SERVICE
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.presentation.activities.MainActivity.Companion.IN_F
import com.example.weatherapp.presentation.activities.MainActivity.Companion.IN_F_PREF
import com.example.weatherapp.presentation.di.App
import com.example.weatherapp.presentation.viewmodel.ApiVMFactory
import com.example.weatherapp.presentation.viewmodel.ApiViewModel
import com.example.weatherapp.utils.ResourceState
import com.example.weatherapp.utils.consts.API_KEY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


class HomeFragment : Fragment() {


    private lateinit var binding: FragmentHomeBinding
    private lateinit var location_client: FusedLocationProviderClient

    @Inject
    lateinit var vm_factory: ApiVMFactory

    private lateinit var vm: ApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity!!.application as App).component.inject(this)

        binding = FragmentHomeBinding.inflate(inflater)
        location_client = LocationServices.getFusedLocationProviderClient(requireActivity())


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val weather_prefs = requireActivity().getSharedPreferences(WEATHER_PREF_MODEL, MODE_PRIVATE)
        val in_f_prefs = requireActivity().getSharedPreferences(IN_F_PREF, MODE_PRIVATE)


        vm = ViewModelProvider(this, vm_factory).get(ApiViewModel::class)
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
                    Toast.makeText(requireContext(), R.string.location_access_granted, Toast.LENGTH_LONG).show()
                    if(isLocationEnabled()){
                        val result = location_client.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            CancellationTokenSource().token
                        )
                        result.addOnCompleteListener {
                            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                            val address = geoCoder.getFromLocation(it.result.latitude,it.result.longitude,1)
                            if (address != null){
                                var cityName = address[0].locality
                                vm.getWeatherForecast(API_KEY, cityName)
                            }

                        }
                    }else{
                        Toast.makeText(requireContext(), R.string.please_turn_the_location_on, Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    Toast.makeText(requireContext(), R.string.no_location_access, Toast.LENGTH_LONG).show()
                }
            }

        }

        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        location_perm_request.launch(
            perms
        )


        lifecycleScope.launch {
            vm.weather_flow.collect {state ->

                when(state){
                    is ResourceState.Loading -> {
                        binding.apply {
                            tvCity.text = "--"
                            tvDate.text = "--"

                            if (in_f_prefs.getBoolean(IN_F, false)){
                                tvTemp.text = "-- F°"
                            }else{
                                tvTemp.text = "-- C°"
                            }
                            tvCondition.text = "--"
                        }
                    }

                    is ResourceState.Success -> {
                        binding.apply {
                            state.data?.let{ res ->

                                val gson = Gson()
                                val json = gson.toJson(res)

                                weather_prefs.edit().putString(WEATHER_MODEL, json).commit()


                                tvCity.text = res.location.name
                                tvDate.text = res.location.localtime.substring(0, 10)
                                tvCondition.text = "${res.current.condition.text}"

                                if (in_f_prefs.getBoolean(IN_F, false)){
                                    tvTemp.text = "${res.current.temp_f} F°"
                                }else{
                                    tvTemp.text = "${res.current.temp_c} C°"
                                }






                                when(res.current.condition.icon){
                                    //night rain
                                    "//cdn.weatherapi.com/weather/64x64/night/176.png" -> {
                                        imgCondition.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                                    }
                                    //day rain
                                    "https://cdn.weatherapi.com/weather/64x64/day/296.png" -> {
                                        imgCondition.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                                    }
                                    //cloudy
                                    "https://cdn.weatherapi.com/weather/64x64/day/116.png" -> {
                                        imgCondition.setImageResource(R.drawable.cloud)
                                    }
                                    //night cloudy
                                    "https://cdn.weatherapi.com/weather/64x64/night/116.png" -> {
                                        imgCondition.setImageResource(R.drawable.cloud_moon)
                                    }
                                    //day sunny
                                    "https://cdn.weatherapi.com/weather/64x64/day/113.png" -> {
                                        imgCondition.setImageResource(R.drawable.reshot_icon_sun_s3a8p7lhkw)
                                    }
                                    //night clear
                                    "https://cdn.weatherapi.com/weather/64x64/night/113.png" -> {
                                        imgCondition.setImageResource(R.drawable.reshot_icon_moon_crescent_sd4enbav8k)
                                    }
                                    //overcast day/night
                                    "//cdn.weatherapi.com/weather/64x64/night/122.png" -> {
                                        imgCondition.setImageResource(R.drawable.cloud)
                                    }
                                    //day rain
                                    "//cdn.weatherapi.com/weather/64x64/day/176.png" -> {
                                        imgCondition.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                                    }
                                    //mist
                                    "//cdn.weatherapi.com/weather/64x64/night/143.png" -> {
                                        imgCondition.setImageResource(R.drawable.cloud)
                                    }
                                    //rain
                                    "//cdn.weatherapi.com/weather/64x64/day/119.png" -> {
                                        imgCondition.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                                    }
                                    //rain night
                                    "//cdn.weatherapi.com/weather/64x64/night/119.png" -> {
                                        imgCondition.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                                    }
                                }
                            }

                        }
                    }

                    is ResourceState.Error -> {
                        Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }







    }

    // IS GPS SERVICE IS ENABLED
    private fun isLocationEnabled(): Boolean{
        val manager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager

        try{
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return false
    }


    companion object{
        const val WEATHER_PREF_MODEL = "weather_pref_model"
        const val WEATHER_MODEL = "weather_model"
    }


}