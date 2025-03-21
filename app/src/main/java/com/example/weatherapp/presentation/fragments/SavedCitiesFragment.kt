package com.example.weatherapp.presentation.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentSavedCitiesBinding
import com.example.weatherapp.domain.models.CitiesItem
import com.example.weatherapp.presentation.activities.MainActivity
import com.example.weatherapp.presentation.activities.MainActivity2
import com.example.weatherapp.presentation.adapters.CitySavedAdapter
import com.example.weatherapp.presentation.viewmodel.CitiesViewModel
import kotlinx.coroutines.launch


class SavedCitiesFragment : Fragment(), CitySavedAdapter.CityClicksSaved {

    private lateinit var binding: FragmentSavedCitiesBinding
    private lateinit var citiesviewModel: CitiesViewModel

    private lateinit var adapter: CitySavedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        citiesviewModel = (requireActivity() as MainActivity).citiesViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedCitiesBinding.inflate(inflater, container, false)
        adapter = CitySavedAdapter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.apply {
            rcsavedcities.adapter = adapter
            rcsavedcities.layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            citiesviewModel.getAllCities()
            citiesviewModel.savedCitiesFlow.collect{
                adapter.differ.submitList(it)
            }
        }


    }

    override fun deleteCity(citiesItem: CitiesItem) {
        citiesviewModel.removeCity(citiesItem)
        citiesviewModel.getAllCities()
        Toast.makeText(requireContext(), "removed successfully", Toast.LENGTH_LONG).show()
    }

    override fun itemClick(citiesItem: CitiesItem) {
        val intent = Intent(requireContext(), MainActivity2::class.java)
        intent.putExtra(SAVED_CITY, citiesItem)
        requireActivity().startActivity(intent)
    }
    companion object{
        const val SAVED_CITY = "saved_city"
    }
}