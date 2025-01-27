package com.example.weatherapp.presentation.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentDetailedBinding
import com.example.weatherapp.domain.models.Forecastday
import com.example.weatherapp.domain.models.WeatherResponse
import com.example.weatherapp.presentation.adapters.RcHoursAdapter


class DetailedFragment : Fragment() {

    private lateinit var binding: FragmentDetailedBinding

    private var args: Bundle ?= null

    private var model: Forecastday ?= null

    private lateinit var adapter: RcHoursAdapter

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        args = arguments

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            model = args?.getSerializable("weather_model", Forecastday::class.java)
        } else {
            model = args?.getSerializable("weather_model") as Forecastday
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailedBinding.inflate(inflater)
        adapter = RcHoursAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rcHoursDetailed.adapter = adapter
            rcHoursDetailed.layoutManager = LinearLayoutManager(requireContext())
            if (model != null){
                adapter.add_list(model!!.hour)
            }
            tvTempDetailed.text = "${model?.day?.avgtemp_c} C°"
            tvDateDetailed.text = "${model?.date}"
            tvConditionDetailed.text = "${model?.day?.condition?.text}"
            sunset.text = "${model?.astro?.sunset}"
            sunrise.text = "${model?.astro?.sunrise}"
            humidity.text = "${model?.day?.avghumidity}%"
            rain.text = "${model?.day?.daily_will_it_rain}%"
            chanceOfSnow.text = "${model?.day?.daily_chance_of_snow}%"
            wind.text = "${model?.day?.maxwind_kph} km/h"


            when(model?.day?.condition?.icon){
                //night rain
                "//cdn.weatherapi.com/weather/64x64/night/176.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                }
                //day rain
                "//cdn.weatherapi.com/weather/64x64/day/296.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                }
                //cloudy
                "//cdn.weatherapi.com/weather/64x64/day/116.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.cloud)
                }
                //night cloudy
                "//cdn.weatherapi.com/weather/64x64/night/116.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.cloud_moon)
                }
                //day sunny
                "//cdn.weatherapi.com/weather/64x64/day/113.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.reshot_icon_sun_s3a8p7lhkw)
                }
                //night clear
                "//cdn.weatherapi.com/weather/64x64/night/113.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.reshot_icon_moon_crescent_sd4enbav8k)
                }
                //overcast day/night
                "//cdn.weatherapi.com/weather/64x64/night/122.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.cloud)
                }
                //day rain
                "//cdn.weatherapi.com/weather/64x64/day/176.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                }
                //mist
                "//cdn.weatherapi.com/weather/64x64/night/143.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.cloud)
                }
                //rain
                "//cdn.weatherapi.com/weather/64x64/day/119.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                }
                //rain night
                "//cdn.weatherapi.com/weather/64x64/night/119.png" -> {
                    imgConditionDetailed.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                }
            }
        }

    }

}