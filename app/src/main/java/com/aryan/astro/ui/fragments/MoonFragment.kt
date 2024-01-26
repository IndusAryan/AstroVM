package com.aryan.astro.ui.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.VideoView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aryan.astro.R
import com.aryan.astro.api.TimezoneAPI
import com.aryan.astro.api.VedAstroAPI
import com.aryan.astro.databinding.FragmentMoonBinding
import com.aryan.astro.helpers.IntentHelper
import com.aryan.astro.ui.models.MoonViewModel
import com.aryan.astro.utils.DatePicker
import com.aryan.astro.utils.TimePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("SetTextI18n")
class MoonFragment : Fragment() {
    private var timezone: String? = null
    private val binding get() = _binding!!
    private val intentHelper = IntentHelper()
    private val moonSignFetcher = VedAstroAPI()
    private var simpleVideoView: VideoView? = null
    private var _binding: FragmentMoonBinding? = null

    private lateinit var day: Any
    private lateinit var month: Any
    private lateinit var bornCity: Any
    private lateinit var year: Any
    private lateinit var birthTime: String
    private lateinit var moonSymbol: String
    private lateinit var btnDatePicker: Button
    private lateinit var btnSelectTime: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var moonViewModel: MoonViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoonBinding.inflate(inflater, container, false)

        val root: View = binding.root
        initViews()

        moonViewModel = ViewModelProvider(this)[MoonViewModel::class.java]

        moonViewModel.getSelectedDate().observe(viewLifecycleOwner) { selectedDate ->

            val textView: TextView = binding.parsedData

            moonViewModel.text.observe(viewLifecycleOwner) {
                textView.text = it
            }

            tvSelectedDate.text = "DOB: $selectedDate"
            val splitDOB = selectedDate.split("/").map { it }
            day = splitDOB[0]
            month = splitDOB[1]
            year = splitDOB[2]

            //Toast.makeText(context, "$day + $month + $year + $selectedDate", Toast.LENGTH_LONG).show()
            binding.calculate.setOnClickListener {

                binding.loading.isVisible = true
                //Toast.makeText(context, "$bornCity", Toast.LENGTH_LONG).show()
                val city = bornCity.toString().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }
                lifecycleScope.launch(Dispatchers.IO) {

                    try {
                        timezone = TimezoneAPI().getTimezone(city)
                        activity?.runOnUiThread {
                            textView.text = "$timezone $city"
                        }

                        fetchMoonSign()

                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.loading.isVisible = false
                    }
                }
            }
        }

        return root
    }

    private fun initViews() {
        animatedCover()
        btnDatePicker = binding.btnDatePicker
        tvSelectedDate = binding.tvSelectedDate
        bornCity = binding.inputCity.editText?.text ?: null!!
        btnDatePicker.setOnClickListener {
            showDatePicker()
        }
        btnSelectTime = binding.btnSelectTime
        btnSelectTime.setOnClickListener{
            showTimePicker()
        }
    }

    private fun showDatePicker() {
        DatePicker.showDatePicker(requireContext()) { selectedDate ->
            moonViewModel.setSelectedDate(selectedDate)
            if (selectedDate.isNotEmpty()) { binding.tvSelectedDate.isVisible = true }
        }
    }

    private fun showTimePicker() {
        TimePicker.showTimePicker(requireContext()) { selectedTime ->
            binding.timeSelected.text = "Time of Birth: $selectedTime"
            birthTime = selectedTime
            //selectedTime.split(":")
        }
    }

    private fun fetchMoonSign() {
        val city = bornCity.toString().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        //binding.loading.isVisible = true

        lifecycleScope.launch(Dispatchers.IO) {
            moonSignFetcher.fetchMoonSign(
                bornCity = city,
                birthTime = birthTime,
                day = day,
                month = month,
                year = year,
                timezone = timezone ?: "",
                onMoonSignFetched = { moonSign ->
                    if (isActive) {
                        moonSymbol = moonSign
                        showResult()
                    }
                }

            ) { errorMessage ->
                if (isActive) {
                    Log.e("TAG", errorMessage)
                    binding.loading.isVisible = false
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun showResult() {
        val moonSignResourceId = resources.getIdentifier(moonSymbol, "string", requireContext().packageName)
        val moonSignDescription = if (moonSignResourceId != 0) {
            getString(moonSignResourceId)} else {
            // Default string in case the resource is not found
            getString(R.string.zodDesc)
        }

        val moonSignImageId= resources.getIdentifier(moonSymbol.lowercase(), "drawable", requireContext().packageName)

        intentHelper.showResultActivity(
            context = requireContext(),
            description = moonSignDescription,
            imageResource = moonSignImageId
        )
    }

    private fun animatedCover() {
        simpleVideoView = binding.mooncover
        simpleVideoView?.setVideoURI(
            Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.covermoon))
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
        _binding = null
    }
}
