package com.example.weatherapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.OneWeatherTimeBinding
import com.example.weatherapp.domain.models.Hour


class RcHoursAdapter(): RecyclerView.Adapter<RcHoursAdapter.RcHoursViewHolder>() {

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

            when(hour_list[position].condition.icon){
                //night rain
                "//cdn.weatherapi.com/weather/64x64/night/176.png" -> {
                    imgConditionHour.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                }
                //day rain
                "https://cdn.weatherapi.com/weather/64x64/day/296.png" -> {
                    imgConditionHour.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                }
                //cloudy
                "https://cdn.weatherapi.com/weather/64x64/day/116.png" -> {
                    imgConditionHour.setImageResource(R.drawable.cloud)
                }
                //night cloudy
                "https://cdn.weatherapi.com/weather/64x64/night/116.png" -> {
                    imgConditionHour.setImageResource(R.drawable.cloud_moon)
                }
                //day sunny
                "https://cdn.weatherapi.com/weather/64x64/day/113.png" -> {
                    imgConditionHour.setImageResource(R.drawable.reshot_icon_sun_s3a8p7lhkw)
                }
                //night clear
                "https://cdn.weatherapi.com/weather/64x64/night/113.png" -> {
                    imgConditionHour.setImageResource(R.drawable.reshot_icon_moon_crescent_sd4enbav8k)
                }
                //overcast day/night
                "//cdn.weatherapi.com/weather/64x64/night/122.png" -> {
                    imgConditionHour.setImageResource(R.drawable.cloud)
                }
                //day rain
                "//cdn.weatherapi.com/weather/64x64/day/176.png" -> {
                    imgConditionHour.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                }
                //mist
                "//cdn.weatherapi.com/weather/64x64/night/143.png" -> {
                    imgConditionHour.setImageResource(R.drawable.cloud)
                }
                //rain
                "//cdn.weatherapi.com/weather/64x64/day/119.png" -> {
                    imgConditionHour.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                }
                //rain night
                "//cdn.weatherapi.com/weather/64x64/night/119.png" -> {
                    imgConditionHour.setImageResource(R.drawable.reshot_icon_rain_bf97dvzpjh)
                }
            }
        }
    }


    fun add_list(list: List<Hour>){
        hour_list = list
        notifyDataSetChanged()
    }
}