package com.example.weatherapp.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.presentation.activities.MainActivity
import com.example.weatherapp.presentation.adapters.RcDaysAdapter
import com.example.weatherapp.presentation.adapters.RcHoursAdapter
import com.example.weatherapp.presentation.fragments.SettingsFragment.Companion.IN_F
import com.example.weatherapp.presentation.fragments.SettingsFragment.Companion.SETTINGS_PREF
import com.example.weatherapp.presentation.viewmodel.ApiViewModel
import com.example.weatherapp.utils.ResourceState
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding ?= null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    private lateinit var vm: ApiViewModel

    private var hour_adapter: RcHoursAdapter ?= null
    private var day_adapter: RcDaysAdapter ?= null

    var settings_pref: SharedPreferences ?= null
    var in_f: Boolean ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //VIEWMODEL INIT
        vm = (requireActivity() as MainActivity).vm

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater)

        hour_adapter = RcHoursAdapter(requireActivity())
        day_adapter = RcDaysAdapter(events = requireActivity() as MainActivity, requireActivity())


        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        settings_pref = requireActivity().getSharedPreferences(SETTINGS_PREF, MODE_PRIVATE)
        settings_pref?.let {
            in_f = it.getBoolean(IN_F, false)
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




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
                        Log.d("load", "loading")
                        binding.apply {
                            tvDate.text = "--.--.--"
                            tvCondition.text = "--"

                            if (in_f == true){
                                tvTemp.text = "-- F°"
                            }else{
                                tvTemp.text = "-- C°"
                            }
                        }
                    }

                    //when data state is success
                    is ResourceState.Success -> {
                        binding.apply {
                            state.data?.let{ res ->
                                Log.d("success", state.data.forecast.toString())
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

                            }

                        }
                    }
                    //if state of response is error we only show the message about the error as toast
                    is ResourceState.Error -> {
                    }
                }

            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }









}