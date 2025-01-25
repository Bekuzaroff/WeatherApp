package com.example.weatherapp.presentation.fragments

import android.Manifest
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.LOCATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.presentation.activities.MainActivity
import com.example.weatherapp.presentation.di.App
import com.example.weatherapp.presentation.viewmodel.ApiVMFactory
import com.example.weatherapp.presentation.viewmodel.ApiViewModel
import com.example.weatherapp.utils.ResourceState
import com.example.weatherapp.utils.consts.API_KEY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var location_client: FusedLocationProviderClient

    @Inject
    lateinit var vm_factory: ApiVMFactory

    private lateinit var vm: ApiViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity!!.application as App).component.inject(this)

        binding = FragmentHomeBinding.inflate(inflater)
        location_client = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        vm = ViewModelProvider(this, vm_factory).get(ApiViewModel::class)
        // GETTING LOCATION PERMISSIONS
        val location_perm_request = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){perms ->
            when {
                perms.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    false) || perms.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    ,false) -> {
                    Toast.makeText(requireContext(), R.string.location_access_granted, Toast.LENGTH_LONG).show()
                    if(isLocationEnabled()){
                        val result = location_client.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            CancellationTokenSource().token
                        )
                        result.addOnCompleteListener {
                            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                            val address = geoCoder.getFromLocation(it.result.latitude,it.result.longitude,1)
                            if (address != null){
                                var cityName = address[0].locality
                                vm.getWeatherForecast(API_KEY, cityName)
                            }

                        }
                    }else{
                        Toast.makeText(requireContext(), R.string.please_turn_the_location_on, Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    Toast.makeText(requireContext(), R.string.no_location_access, Toast.LENGTH_LONG).show()
                }
            }

        }

        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        location_perm_request.launch(
            perms
        )


        lifecycleScope.launch {
            vm.weather_flow.collect {state ->

                when(state){
                    is ResourceState.Loading -> {
                        binding.apply {
                            tvCity.text = "--"
                            tvDate.text = "--"
                            tvTemp.text = "-- C°"
                            tvCondition.text = "--"
                        }
                    }

                    is ResourceState.Success -> {
                        binding.apply {
                            state.data?.let{ res ->
                                tvCity.text = res.location.name
                                tvDate.text = res.location.localtime.substring(0, 10)
                                tvTemp.text = "${res.current.temp_c} C°"
                                tvCondition.text = "${res.current.condition.text}"
                            }

                        }
                    }

                    is ResourceState.Error -> {

                    }
                }

            }
        }
    }

    // IS GPS SERVICE IS ENABLED
    private fun isLocationEnabled(): Boolean{
        val manager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager

        try{
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return false
    }


}