package com.example.weatherapp.presentation.fragments


import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.FragmentSavedCityWeatherDetailBinding
import com.example.weatherapp.domain.models.Forecastday
import com.example.weatherapp.presentation.adapters.RcHoursAdapter
import com.example.weatherapp.utils.NavPoints


class SavedCityWeatherDetailFragment : Fragment() {

    private lateinit var binding: FragmentSavedCityWeatherDetailBinding

    //args from the home fragment
    private var args: Bundle ?= null

    //serializable object of all detailed weather data
    private var model: Forecastday ?= null

    private lateinit var adapter: RcHoursAdapter

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

        binding = FragmentSavedCityWeatherDetailBinding.inflate(inflater)
        adapter = RcHoursAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO UI LOGIC
        binding.apply {

            //rc views and adapters inits
            rcHoursDetailed2.adapter = adapter
            rcHoursDetailed2.layoutManager = LinearLayoutManager(requireContext())

            if (model != null){
                adapter.add_list(model!!.hour)
            }

            tvTempDetailed2.text = "${model?.day?.maxtemp_c} C°"

            //put all data from args into ui
            tvDateDetailed2.text = "${model?.date}"
            tvConditionDetailed2.text = "${model?.day?.condition?.text}"
            sunset2.text = "sunset: ${model?.astro?.sunset}"
            sunrise2.text = "sunrise: ${model?.astro?.sunrise}"
            humidity2.text = "humidity: ${model?.day?.avghumidity}%"
            rain2.text = "rain: ${model?.day?.daily_will_it_rain}%"
            chanceOfSnow2.text = "snow: ${model?.day?.daily_chance_of_snow}%"
            wind2.text = "wind: ${model?.day?.maxwind_kph} km/h"

            Glide.with(requireActivity()).load("https:${model?.day?.condition?.icon}").into(imgConditionDetailed2)

            btBackDetailes3.setOnClickListener {
                NavPoints.navigateTo(NavPoints.Saved_City_Weather_fragment(arg = null), requireActivity().supportFragmentManager)
            }
        }

    }

}