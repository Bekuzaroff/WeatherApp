package com.example.weatherapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.OneCityApiBinding
import com.example.weatherapp.domain.models.CitiesItem

class CityItemAdapter(val cityClicks: CityClicks): RecyclerView.Adapter<CityItemAdapter.CityItemHolder>() {
    inner class CityItemHolder(val binding: OneCityApiBinding): RecyclerView.ViewHolder(binding.root)


    val util = object: DiffUtil.ItemCallback<CitiesItem>(){
        override fun areItemsTheSame(oldItem: CitiesItem, newItem: CitiesItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CitiesItem, newItem: CitiesItem): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, util)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityItemHolder {
        return CityItemHolder(OneCityApiBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CityItemHolder, position: Int) {
        val currentItem = differ.currentList[position]
        holder.binding.apply {
            tvCityName.text = currentItem.name
            btAddCity.setOnClickListener {
                cityClicks.addCity(currentItem)
            }
            holder.itemView.setOnClickListener {
                cityClicks.itemClick(currentItem)
            }
        }
    }

    interface CityClicks{
        fun addCity(citiesItem: CitiesItem)
        fun itemClick(citiesItem: CitiesItem)
    }
}