package com.example.weatherapp.presentation.fragments

import android.Manifest
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.presentation.activities.MainActivity
import com.example.weatherapp.presentation.adapters.RcDaysAdapter
import com.example.weatherapp.presentation.adapters.RcHoursAdapter
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

    private lateinit var hour_adapter: RcHoursAdapter
    private lateinit var day_adapter: RcDaysAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity().application as App).component.inject(this)

        binding = FragmentHomeBinding.inflate(inflater)
        location_client = LocationServices.getFusedLocationProviderClient(requireActivity())

        hour_adapter = RcHoursAdapter()
        day_adapter = RcDaysAdapter(requireActivity() as MainActivity)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.rcHours.adapter = hour_adapter
        binding.rcHours.layoutManager = LinearLayoutManager(requireContext())

        binding.rcDays.adapter = day_adapter
        binding.rcDays.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)





        // SHARED PREFS FOR SAVING WEATHER MODEL AND THE SWITCH STATE
        val weather_prefs = requireActivity().getSharedPreferences(WEATHER_PREF_MODEL, MODE_PRIVATE)


        //VIEWMODEL INIT
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
                        //WHEN LOCATION IS GRANTED
                    //check if the gps is enabled so we get current user location
                    if(isLocationEnabled()){
                        val result = location_client.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            CancellationTokenSource().token
                        )

                        //when getting location is completed we get the city from location with Geocoder
                        result.addOnCompleteListener {
                            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                            val address = geoCoder.getFromLocation(it.result.latitude,it.result.longitude,1)
                            if (address != null){
                                var cityName = address[0].locality
                                vm.getWeatherForecast(API_KEY, cityName)
                            }

                        }
                    }else{
                        vm.getWeatherForecast(API_KEY, "London")
                        Toast.makeText(requireContext(), R.string.please_turn_the_location_on, Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    vm.getWeatherForecast(API_KEY, "London")
                    Toast.makeText(requireContext(), R.string.no_location_access, Toast.LENGTH_LONG).show()
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


        //collecting state of data about the weather from flow
        lifecycleScope.launch {
            vm.weather_flow.collect {state ->

                when(state){

                    // when the data state is loading we show in all textviews only "--"
                    is ResourceState.Loading -> {
                        binding.apply {
                            tvCity.text = "--"
                            tvDate.text = "--"

                            tvTemp.text = "-- C°"
                            tvCondition.text = "--"
                        }
                    }

                    //when data state is success
                    is ResourceState.Success -> {
                        binding.apply {
                            state.data?.let{ res ->







                                //convert model to json so we could pass it into shared prefs
                                val gson = Gson()
                                val json = gson.toJson(res)

                                //adding to shared prefs weather data
                                weather_prefs.edit().putString(WEATHER_MODEL, json).commit()


                                //update the ui


                                hour_adapter.add_list(state.data.forecast.forecastday[0].hour)
                                day_adapter.add_list(state.data.forecast.forecastday)


                                tvCity.text = res.location.name
                                tvDate.text = res.location.localtime.substring(0, 10)
                                tvCondition.text = "${res.current.condition.text}"

                                tvTemp.text = "${res.current.temp_c} C°"






                                /*I want to use my own weather icons so i check the name of current icon from api and

                                    show mine instead
                                 */
                                when(res.current.condition.icon){
                                    //night rain
                                    "//cdn.weatherapi.com/weather/64x64/night/176.png" -> {
                                        imgCondition.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                                    }
                                    //day rain
                                    "//cdn.weatherapi.com/weather/64x64/day/296.png" -> {
                                        imgCondition.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                                    }
                                    //cloudy
                                    "//cdn.weatherapi.com/weather/64x64/day/116.png" -> {
                                        imgCondition.setImageResource(R.drawable.cloud)
                                    }
                                    //night cloudy
                                    "//cdn.weatherapi.com/weather/64x64/night/116.png" -> {
                                        imgCondition.setImageResource(R.drawable.cloud_moon)
                                    }
                                    //day sunny
                                    "//cdn.weatherapi.com/weather/64x64/day/113.png" -> {
                                        imgCondition.setImageResource(R.drawable.reshot_icon_sun_s3a8p7lhkw)
                                    }
                                    //night clear
                                    "//cdn.weatherapi.com/weather/64x64/night/113.png" -> {
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

                    //if state of response is error we only show the message about the error as toast
                    is ResourceState.Error -> {
                        Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }







    }

    // IS GPS SERVICE IS ENABLED
    private fun isLocationEnabled(): Boolean{
        //getting location manager
        val manager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager

        //checking if the gps is enabled
        try{
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return false
    }


    companion object{
        // prefs names
        const val WEATHER_PREF_MODEL = "weather_pref_model"
        const val WEATHER_MODEL = "weather_model"
    }


}