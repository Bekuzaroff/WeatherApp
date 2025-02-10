package com.example.weatherapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.FragmentSavedCityWeatherBinding
import com.example.weatherapp.presentation.activities.MainActivity2
import com.example.weatherapp.presentation.adapters.RcDaysAdapter
import com.example.weatherapp.presentation.adapters.RcHoursAdapter
import com.example.weatherapp.presentation.viewmodel.ApiViewModel
import com.example.weatherapp.utils.ResourceState
import kotlinx.coroutines.launch


class SavedCityWeatherFragment : Fragment() {


    private var _binding: FragmentSavedCityWeatherBinding ?= null
    private val binding: FragmentSavedCityWeatherBinding
        get() = _binding!!

    private lateinit var vm: ApiViewModel

    private var hour_adapter: RcHoursAdapter ?= null
    private var day_adapter: RcDaysAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //view model init
        vm = (requireActivity() as MainActivity2).apiViewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSavedCityWeatherBinding.inflate(inflater)

        //rc view adapters init
        hour_adapter = RcHoursAdapter()
        day_adapter = RcDaysAdapter(events = requireActivity() as MainActivity2)


        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //TODO ALL UI LOGIC

        //rc views init
        binding.rcHours2.adapter = hour_adapter
        binding.rcHours2.layoutManager = LinearLayoutManager(requireContext())

        binding.rcDays2.adapter = day_adapter
        binding.rcDays2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.btBackDetailes2.setOnClickListener {
            requireActivity().finish()
        }

        //collecting state of data about the weather from flow
        lifecycleScope.launch {
            vm.weather_flow.collect{state ->

                when(state){
                    // when the data state is loading we show in all textviews only "--"
                    is ResourceState.Loading -> {

                        binding.apply {
                                tvDate2.text = "--.--.--"
                                tvCondition2.text = "--"
                                tvTemp2.text = "-- C°"
                        }
                    }

                    //when data state is success
                    is ResourceState.Success -> {
                        binding.apply {
                            state.data?.let{ res ->
                                //update the ui
                                hour_adapter?.add_list(state.data.forecast.forecastday[0].hour)
                                day_adapter?.add_list(state.data.forecast.forecastday)

                                tvDate2.text = res.location.localtime.substring(0, 10)
                                tvCondition2.text = "${res.current.condition.text}"
                                tvTemp2.text = "${res.current.temp_c} C°"

                                Glide.with(requireContext()).load("https:${res.current.condition.icon}").into(imgCondition2)
                            }

                        }
                    }
                    //if state of response is error we only show the message about the error as toast
                    is ResourceState.Error -> {
                        Toast.makeText(requireContext(),state.m, Toast.LENGTH_LONG).show()

                        binding.apply {
                                tvDate2.text = "--.--.--"
                                tvCondition2.text = "--"
                                tvTemp2.text = "-- C°"
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


}