package com.example.weatherapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.FragmentSavedCitiesBinding
import com.example.weatherapp.presentation.activities.MainActivity
import com.example.weatherapp.presentation.viewmodel.CitiesViewModel
import kotlinx.coroutines.launch


class SavedCitiesFragment : Fragment() {

    private lateinit var binding: FragmentSavedCitiesBinding
    private lateinit var citiesviewModel: CitiesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        citiesviewModel = (requireActivity() as MainActivity).citiesViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            citiesviewModel.savedCitiesFlow.collect{

            }
        }


    }
}