package com.example.weatherapp.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentDetailedBinding
import com.example.weatherapp.domain.models.Forecastday
import com.example.weatherapp.presentation.adapters.RcHoursAdapter
import com.example.weatherapp.presentation.fragments.SettingsFragment.Companion.IN_F
import com.example.weatherapp.presentation.fragments.SettingsFragment.Companion.IN_MH
import com.example.weatherapp.presentation.fragments.SettingsFragment.Companion.SETTINGS_PREF


class DetailedFragment : Fragment() {

    private lateinit var binding: FragmentDetailedBinding

    private var args: Bundle ?= null

    private var model: Forecastday ?= null

    private lateinit var adapter: RcHoursAdapter

    private var settings_prefs: SharedPreferences ?= null
    private var in_f: Boolean ?= null
    private var in_mh: Boolean ?= null

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

        settings_prefs = requireActivity().getSharedPreferences(SETTINGS_PREF,MODE_PRIVATE)
        if (settings_prefs != null){
            in_f = settings_prefs!!.getBoolean(IN_F, false)
            in_mh = settings_prefs!!.getBoolean(IN_MH, false)
        }

        binding.apply {
            rcHoursDetailed.adapter = adapter
            rcHoursDetailed.layoutManager = LinearLayoutManager(requireContext())
            if (model != null){
                adapter.add_list(model!!.hour)
            }

            in_f?.let {
                if (it){
                    tvTempDetailed.text = "${model?.day?.maxtemp_f} F°"
                }else{
                    tvTempDetailed.text = "${model?.day?.maxtemp_c} C°"
                }
            }
            in_mh?.let {
                if (it){
                    wind.text = "${model?.day?.maxwind_mph} m/h"
                }else{
                    wind.text = "${model?.day?.maxwind_kph} km/h"
                }
            }

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