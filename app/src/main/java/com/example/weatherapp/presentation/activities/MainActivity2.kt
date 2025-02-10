package com.example.weatherapp.presentation.activities

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMain2Binding
import com.example.weatherapp.domain.models.CitiesItem
import com.example.weatherapp.domain.models.Forecastday
import com.example.weatherapp.presentation.activities.MainActivity.Companion.CACHE_PREFS
import com.example.weatherapp.presentation.adapters.RcDaysAdapter
import com.example.weatherapp.presentation.di.App
import com.example.weatherapp.presentation.fragments.SavedCitiesFragment.Companion.SAVED_CITY
import com.example.weatherapp.presentation.fragments.SavedCityWeatherFragment
import com.example.weatherapp.presentation.viewmodel.ApiVMFactory
import com.example.weatherapp.presentation.viewmodel.ApiViewModel
import com.example.weatherapp.utils.NavPoints
import com.example.weatherapp.utils.consts.API_KEY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity2 : AppCompatActivity(), RcDaysAdapter.ClickEvents{
    private var _binding: ActivityMain2Binding?= null
    private val binding: ActivityMain2Binding
        get() = _binding!!

    var settings_prefs: SharedPreferences?= null

    @Inject
    lateinit var apiVMFactory: ApiVMFactory
    val apiViewModel by lazy {
        apiVMFactory.create(ApiViewModel::class.java)
    }

    var cityName: MutableStateFlow<String> = MutableStateFlow("")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO HERE ALL THE INITS
        _binding = ActivityMain2Binding.inflate(layoutInflater)

        (application as App).component.inject(this)

        settings_prefs = getSharedPreferences(CACHE_PREFS, MODE_PRIVATE)

        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.host, SavedCityWeatherFragment()).commit()

        val model = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra(SAVED_CITY, CitiesItem::class.java)
        } else {
            intent?.getSerializableExtra(SAVED_CITY) as CitiesItem
        }

        if (model != null) {
            cityName.value = model.name
        }
        //COLLECTING THE CITY NAME AND GET WEATHER FOR THIS CITY
        lifecycleScope.launch {
            cityName.collect {
                if (isOnline()) {
                    apiViewModel.getWeatherForecast(API_KEY, it)
                }else{
                    Toast.makeText(this@MainActivity2, "you do not have internet connection", Toast.LENGTH_LONG).show()
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

    override fun itemClick(day: Forecastday) {
        NavPoints.navigateTo(NavPoints.Saved_City_Weather_Detailed_fragment(arg = day), supportFragmentManager)
    }
}