package com.example.weatherapp.presentation.adapters

import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.databinding.OneDayWeatherBinding
import com.example.weatherapp.databinding.OneWeatherTimeBinding
import com.example.weatherapp.domain.models.Forecastday
import com.example.weatherapp.domain.models.Hour
import com.example.weatherapp.presentation.fragments.SettingsFragment.Companion.IN_F
import com.example.weatherapp.presentation.fragments.SettingsFragment.Companion.SETTINGS_PREF

class RcDaysAdapter(
    private val events: ClickEvents,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<RcDaysAdapter.RcDaysViewHolder>() {

    private var day_list: List<Forecastday> = listOf()

    inner class RcDaysViewHolder(
        val binding: OneDayWeatherBinding
    ): RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RcDaysViewHolder {
        return RcDaysViewHolder(
            OneDayWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return day_list.size
    }

    override fun onBindViewHolder(holder: RcDaysViewHolder, position: Int) {
        val prefs = activity.getSharedPreferences(SETTINGS_PREF, MODE_PRIVATE)
        val in_f = prefs.getBoolean(IN_F, false)


        holder.binding.apply {
            tvDateDay.text = "${day_list[position].date.substring(0, 10)}"

            if (in_f){
                tvTempDay.text = "${day_list[position].day.avgtemp_f} F°"
            }else{
                tvTempDay.text = "${day_list[position].day.avgtemp_c} C°"
            }

            Glide.with(holder.itemView).load("https:${day_list[position].day.condition.icon}").into(imgConditionDay)

            holder.itemView.setOnClickListener {
                events.itemClick(day_list[position])
            }
        }
    }


    fun add_list(list: List<Forecastday>){
        day_list = list
        notifyDataSetChanged()
    }

    interface ClickEvents{
        fun itemClick(day: Forecastday)
    }
}