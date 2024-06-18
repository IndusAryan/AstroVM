package com.aryan.astro.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aryan.astro.R
import com.aryan.astro.databinding.FragmentMoonBinding
import com.aryan.astro.db.DataStoreHelper
import com.aryan.astro.helpers.IntentHelper
import com.aryan.astro.viewmodels.MoonViewModel
import com.aryan.astro.utils.DatePicker
import com.aryan.astro.utils.TimePicker
import com.aryan.astro.utils.ToastUtil.showToast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("SetTextI18n")
class MoonFragment : Fragment() {

    private val binding get() = _binding!!
    private val intentHelper = IntentHelper()
    private var simpleVideoView: VideoView? = null
    private var _binding: FragmentMoonBinding? = null

    private var bornCity: Any? = null
    private lateinit var moonSymbol: String
    private var btnDatePicker: Button? = null
    private lateinit var moonViewModel: MoonViewModel

    private lateinit var locationCallback: LocationCallback
    private var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoonBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val root: View = binding.root
        initViews()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.locations[0]
                    getCityNameFromLocation(location)
                }
            }
        }

        moonViewModel = ViewModelProvider(this)[MoonViewModel::class.java]

        moonViewModel.getSelectedDate().observe(viewLifecycleOwner) { selectedDate ->

            binding.tvSelectedDate.text = "DOB: $selectedDate"

            val splitDOB = selectedDate.split("/").map { it }
            moonViewModel.day = splitDOB[0]
            moonViewModel.month = splitDOB[1]
            moonViewModel.year = splitDOB[2]

            moonViewModel.bornCity = bornCity
        }

        moonViewModel.timezone.observe(viewLifecycleOwner) { timezone ->
            val tz = binding.parsedTimezone
            tz.text = "Timezone: ${timezone?.userTimezone}"
            tz.isVisible = (tz.text == null)
        }

        binding.calculate.setOnClickListener {
            moonViewModel.moonSign.observe(viewLifecycleOwner) {

                if (!it.isNullOrEmpty()) {
                    moonSymbol = it
                    binding.loading.isVisible = false
                    showResult()
                    DataStoreHelper(context ?: return@observe).saveData(
                        "MoonSign -> $moonSymbol ${moonViewModel.day}/${moonViewModel.month}/${moonViewModel.year}"
                    )
                }
            }

            val city = bornCity ?: ""
            binding.loading.isVisible = true

            lifecycleScope.launch {
                moonViewModel.fetchTimezone(city)
            }
        }

        moonViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                showToast(context?: return@observe, errorMessage)
            }
            binding.loading.isVisible = false
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gpsButton.setOnClickListener {
           checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                context ?: return,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getCityNameFromLocation(location)
                } else {
                    requestNewLocationData()
                }
            }
    }

    private fun getCityNameFromLocation(location: Location) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val cityName = address.locality ?: address.adminArea ?: address.subAdminArea ?: "Unknown City"
            val citylog = listOf(address.locality , address.adminArea, address.subAdminArea)
            Log.i("Tag", "$citylog")
            binding.inputCity.editText?.setText(cityName)
            bornCity = cityName
            //Toast.makeText(requireContext(), "City: $cityName", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Unable to get city name", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted
                    getLastLocation()
                } else {
                    // Permission denied
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun initViews() {
        animatedCover()
        btnDatePicker = binding.btnDatePicker
        bornCity = binding.inputCity.editText?.text
        btnDatePicker?.setOnClickListener {
            showTimePicker()
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        DatePicker.showDatePicker(context ?: return) { selectedDate ->
            moonViewModel.setSelectedDate(selectedDate)
            if (selectedDate.isNotEmpty()) {
                binding.tvSelectedDate.isVisible = true
            }
        }
    }

    private fun showTimePicker() {
        TimePicker.showTimePicker(context ?: return) { selectedTime ->
            binding.timeSelected.text = "Time of Birth: $selectedTime"
            moonViewModel.selectedTime = selectedTime
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun showResult() {
        val moonSignResourceId = resources.getIdentifier(moonSymbol, "string", context?.packageName)
        val moonSignDescription = if (moonSignResourceId != 0) {
            getString(moonSignResourceId)
        } else {
            // Default string in case the resource is not found
            getString(R.string.zodDesc)
        }

        val moonSignImageId = resources.getIdentifier(
            moonSymbol.lowercase(),
            "drawable",
            context?.packageName
        )

        intentHelper.showResultActivity(
            context = context ?: return,
            description = moonSignDescription,
            imageResource = moonSignImageId
        )
    }

    private fun animatedCover() {
        simpleVideoView = binding.mooncover
        simpleVideoView?.setVideoURI(
            Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.covermoon)
        )
        simpleVideoView?.start()
        simpleVideoView?.setOnPreparedListener { mp -> mp.isLooping = true }
    }

    override fun onResume() {
        super.onResume()
        animatedCover()
        binding.loading.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        moonViewModel.clearData()
        _binding = null
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        moonViewModel.clearData()
    }
}
