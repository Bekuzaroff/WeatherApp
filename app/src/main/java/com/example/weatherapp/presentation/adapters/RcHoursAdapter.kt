package com.example.weatherapp.presentation.adapters

import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.OneWeatherTimeBinding
import com.example.weatherapp.domain.models.Hour


class RcHoursAdapter(
): RecyclerView.Adapter<RcHoursAdapter.RcHoursViewHolder>() {

    private var hour_list: List<Hour> = listOf()

    inner class RcHoursViewHolder(
        val binding: OneWeatherTimeBinding
    ): RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RcHoursViewHolder {
        return RcHoursViewHolder(
            OneWeatherTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return hour_list.size
    }

    override fun onBindViewHolder(holder: RcHoursViewHolder, position: Int) {
        holder.binding.apply {
            tvTime.text = hour_list[position].time.substring(10)
            tvTemp.text = "${hour_list[position].temp_c} C°"

            Glide.with(holder.itemView).load("https:${hour_list[position].condition.icon}").into(imgConditionHour)
        }

    }
    fun add_list(list: List<Hour>){
        hour_list = list
        notifyDataSetChanged()
    }
}