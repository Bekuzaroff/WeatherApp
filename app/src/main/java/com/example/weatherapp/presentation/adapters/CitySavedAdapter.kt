package com.example.weatherapp.presentation.adapters

import com.example.weatherapp.databinding.OneCitySavedBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.domain.models.CitiesItem

class CitySavedAdapter(val cityClicks: CityClicksSaved): RecyclerView.Adapter<CitySavedAdapter.CitySavedHolder>() {
    inner class CitySavedHolder(val binding: OneCitySavedBinding): RecyclerView.ViewHolder(binding.root)


    val util = object: DiffUtil.ItemCallback<CitiesItem>(){
        override fun areItemsTheSame(oldItem: CitiesItem, newItem: CitiesItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CitiesItem, newItem: CitiesItem): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, util)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitySavedHolder {
        return CitySavedHolder(OneCitySavedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CitySavedHolder, position: Int) {
        val currentItem = differ.currentList[position]
        holder.binding.apply {
            tvCityNamesaved.text = currentItem.name
            btdeletecity.setOnClickListener {
                // Передаем currentItem в deleteCity
                cityClicks.deleteCity(currentItem)
            }
            holder.itemView.setOnClickListener {
                cityClicks.itemClick(currentItem)
            }
        }
    }

    interface CityClicksSaved{
        fun deleteCity(citiesItem: CitiesItem)
        fun itemClick(citiesItem: CitiesItem)
    }
}