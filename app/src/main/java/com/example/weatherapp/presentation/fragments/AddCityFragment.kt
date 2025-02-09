package com.example.weatherapp.presentation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentAddCityBinding
import com.example.weatherapp.domain.models.CitiesItem
import com.example.weatherapp.presentation.activities.MainActivity
import com.example.weatherapp.presentation.adapters.CityItemAdapter
import com.example.weatherapp.presentation.viewmodel.CitiesViewModel
import com.example.weatherapp.utils.CitiesResourceState
import com.example.weatherapp.utils.ResourceState
import com.example.weatherapp.utils.consts.API_KEY_CITY
import kotlinx.coroutines.launch

class AddCityFragment : Fragment(), CityItemAdapter.CityClicks {

    private lateinit var binding: FragmentAddCityBinding
    private lateinit var citiesviewModel: CitiesViewModel

    private lateinit var adapter: CityItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        citiesviewModel = (requireActivity() as MainActivity).citiesViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCityBinding.inflate(inflater)
        adapter = CityItemAdapter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rcCityApi.adapter = adapter
            rcCityApi.layoutManager = LinearLayoutManager(requireContext())
        }
        lifecycleScope.launch {
            citiesviewModel.citiesFlow.collect{state->
                when(state){
                    is CitiesResourceState.Loading -> {

                    }
                    is CitiesResourceState.Success -> {
                        adapter.differ.submitList(state.data)
                    }
                    is CitiesResourceState.Error -> {
                        Toast.makeText(requireContext(), state.m, Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

    override fun addCity(citiesItem: CitiesItem) {
        citiesviewModel.addCity(citiesItem)
        Toast.makeText(requireContext(), "added successfully", Toast.LENGTH_LONG).show()
    }
}