package com.example.weatherapp.presentation.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.FragmentDetailedBinding
import com.example.weatherapp.domain.models.Forecastday
import com.example.weatherapp.presentation.activities.MainActivity
import com.example.weatherapp.presentation.adapters.RcHoursAdapter


class DetailedFragment : Fragment() {

    private lateinit var binding: FragmentDetailedBinding

    //args from the home fragment
    private var args: Bundle ?= null

    //serializable object of all detailed weather data
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
        adapter = RcHoursAdapter(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO UI LOGIC
        binding.apply {

            //rc views and adapters inits
            rcHoursDetailed.adapter = adapter
            rcHoursDetailed.layoutManager = LinearLayoutManager(requireContext())

            if (model != null){
                adapter.add_list(model!!.hour)
            }

            tvTempDetailed.text = "${model?.day?.maxtemp_c} C°"
            wind.text = "${model?.day?.maxwind_kph} km/h"



            //put all data from args into ui
            tvDateDetailed.text = "${model?.date}"
            tvConditionDetailed.text = "${model?.day?.condition?.text}"
            sunset.text = "${model?.astro?.sunset}"
            sunrise.text = "${model?.astro?.sunrise}"
            humidity.text = "${model?.day?.avghumidity}%"
            rain.text = "${model?.day?.daily_will_it_rain}%"
            chanceOfSnow.text = "${model?.day?.daily_chance_of_snow}%"

            Glide.with(requireActivity()).load("https:${model?.day?.condition?.icon}").into(imgConditionDetailed)
        }

    }

}