package com.example.weatherapp.presentation.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings_prefs = requireActivity().getSharedPreferences(SETTINGS_PREF, MODE_PRIVATE)



        binding.apply {
            switchInF.isChecked = settings_prefs.getBoolean(IN_F, false)
            switchMh.isChecked = settings_prefs.getBoolean(IN_MH, false)
            switchInF.setOnClickListener {

                if (switchInF.isChecked){
                    settings_prefs.edit().putBoolean(IN_F, true).commit()
                }else{
                    settings_prefs.edit().putBoolean(IN_F, false).commit()
                }
            }

            switchMh.setOnClickListener {
                if (switchMh.isChecked){
                    settings_prefs.edit().putBoolean(IN_MH, true).commit()
                }else{
                    settings_prefs.edit().putBoolean(IN_MH, false).commit()
                }
            }
        }
    }


    companion object{
        const val SETTINGS_PREF = "SETTINGS_PREF"
        const val IN_F = "IN_F"
        const val IN_MH = "IN_MH"
    }


}