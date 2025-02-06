package com.example.weatherapp.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.domain.models.WeatherResponse
import com.example.weatherapp.presentation.activities.MainActivity
import com.example.weatherapp.presentation.adapters.RcDaysAdapter
import com.example.weatherapp.presentation.adapters.RcHoursAdapter
import com.example.weatherapp.presentation.fragments.SettingsFragment.Companion.IN_F
import com.example.weatherapp.presentation.fragments.SettingsFragment.Companion.SETTINGS_PREF
import com.example.weatherapp.presentation.viewmodel.ApiViewModel
import com.example.weatherapp.utils.ResourceState
import com.google.gson.Gson
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding ?= null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    private lateinit var vm: ApiViewModel

    private var hour_adapter: RcHoursAdapter ?= null
    private var day_adapter: RcDaysAdapter ?= null

    var settings_prefs: SharedPreferences ?= null
    var in_f: Boolean ?= null

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //view model init
        vm = (requireActivity() as MainActivity).vm

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater)

        //rc view adapters init
        hour_adapter = RcHoursAdapter(requireActivity())
        day_adapter = RcDaysAdapter(events = requireActivity() as MainActivity, requireActivity())


        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //prefs init
        settings_prefs = requireActivity().getSharedPreferences(SETTINGS_PREF, MODE_PRIVATE)
        settings_prefs?.let {
            in_f = it.getBoolean(IN_F, false)
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //TODO ALL UI LOGIC

        //rc views init
        binding.rcHours.adapter = hour_adapter
        binding.rcHours.layoutManager = LinearLayoutManager(requireContext())

        binding.rcDays.adapter = day_adapter
        binding.rcDays.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        //collecting state of data about the weather from flow
        lifecycleScope.launch {
            vm.weather_flow.collect{state ->

                when(state){
                    // when the data state is loading we show in all textviews only "--"
                    is ResourceState.Loading -> {

                        binding.apply {
                            //put previous data into ui
                            val str_data = settings_prefs!!.getString(PREVIOUS_DATA_PREF, "")
                            var data: WeatherResponse ?= null
                            if (str_data!!.isNotEmpty()){
                                data = gson.fromJson(str_data, WeatherResponse::class.java)
                            }

                            if (data != null){
                                hour_adapter?.add_list(data.forecast.forecastday[0].hour)
                                day_adapter?.add_list(data.forecast.forecastday)

                                tvDate.text = data.location.localtime.substring(0, 10)
                                tvCondition.text = "${data.current.condition.text}"

                                if (in_f == true){
                                    tvTemp.text = "${data.current.temp_f} F°"
                                }else{
                                    tvTemp.text = "${data.current.temp_c} C°"
                                }

                                Glide.with(requireContext()).load("https:${data.current.condition.icon}").into(imgCondition)
                            }else{
                                tvDate.text = "--.--.--"
                                tvCondition.text = "--"

                                if (in_f == true){
                                    tvTemp.text = "-- F°"
                                }else{
                                    tvTemp.text = "-- C°"
                                }
                            }
                        }
                    }

                    //when data state is success
                    is ResourceState.Success -> {
                        binding.apply {
                            state.data?.let{ res ->
                                //update the ui
                                hour_adapter?.add_list(state.data.forecast.forecastday[0].hour)
                                day_adapter?.add_list(state.data.forecast.forecastday)

                                tvDate.text = res.location.localtime.substring(0, 10)
                                tvCondition.text = "${res.current.condition.text}"

                                if (in_f == true){
                                    tvTemp.text = "${res.current.temp_f} F°"
                                }else{
                                    tvTemp.text = "${res.current.temp_c} C°"
                                }

                                Glide.with(requireContext()).load("https:${res.current.condition.icon}").into(imgCondition)

                                val str_data = gson.toJson(res)
                                settings_prefs!!.edit().putString(PREVIOUS_DATA_PREF, str_data).commit()

                            }

                        }
                    }
                    //if state of response is error we only show the message about the error as toast
                    is ResourceState.Error -> {
                        Toast.makeText(requireContext(),state.m, Toast.LENGTH_LONG).show()

                        binding.apply {
                            //put previous data into ui
                            val str_data = settings_prefs!!.getString(PREVIOUS_DATA_PREF, "")
                            var data: WeatherResponse ?= null
                            if (str_data!!.isNotEmpty()){
                                data = gson.fromJson(str_data, WeatherResponse::class.java)
                            }

                            if (data != null){
                                hour_adapter?.add_list(data!!.forecast.forecastday[0].hour)
                                day_adapter?.add_list(data!!.forecast.forecastday)

                                tvDate.text = data!!.location.localtime.substring(0, 10)
                                tvCondition.text = "${data!!.current.condition.text}"

                                if (in_f == true){
                                    tvTemp.text = "${data!!.current.temp_f} F°"
                                }else{
                                    tvTemp.text = "${data!!.current.temp_c} C°"
                                }

                                Glide.with(requireContext()).load("https:${data!!.current.condition.icon}").into(imgCondition)
                            }else{
                                tvDate.text = "--.--.--"
                                tvCondition.text = "--"

                                if (in_f == true){
                                    tvTemp.text = "-- F°"
                                }else{
                                    tvTemp.text = "-- C°"
                                }
                            }
                        }
                    }
                }

            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val PREVIOUS_DATA_PREF = "PREVIOUS_DATA_PREF"
    }











}