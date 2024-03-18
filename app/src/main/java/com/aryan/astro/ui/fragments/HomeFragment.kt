package com.aryan.astro.ui.fragments

import com.aryan.astro.db.DataStoreHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import com.aryan.astro.helpers.IntentHelper
import com.aryan.astro.R
import com.aryan.astro.databinding.FragmentHomeBinding
import com.aryan.astro.ui.models.HomeViewModel
import com.aryan.astro.utils.DatePicker
import com.google.android.material.button.MaterialButton
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private var btnDatePicker: MaterialButton? = null
    private var tvSelectedDate: TextView? = null
    private var coverImage: ImageView? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n", "DiscouragedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        coverImage = binding.suncover
        val intentHelper = IntentHelper()

        initViews()

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.getSelectedDate().observe(viewLifecycleOwner) { selectedDate ->

            tvSelectedDate?.text = "Selected Date: $selectedDate"

            binding.calculate.setOnClickListener {
                if (selectedDate != null) {

                    val sunSign = homeViewModel.getSunSign(selectedDate)
                    Toast.makeText(activity, sunSign, Toast.LENGTH_SHORT).show()

                    val saveSunsign = DataStoreHelper(requireContext())
                    saveSunsign.saveData("SunSign -> $sunSign $selectedDate")

                    val sunSignResourceId = resources.getIdentifier(sunSign.lowercase(Locale.ROOT),
                        "string", requireContext().packageName)

                    val sunSignDescription = if (sunSignResourceId != 0) {
                        getString(sunSignResourceId)} else {
                        // Default string in case the resource is not found
                        getString(R.string.zodDesc)
                    }

                    val sunSignImageId = resources.getIdentifier(sunSign.lowercase(),
                        "drawable", requireContext().packageName)

                    intentHelper.showResultActivity(
                        context = requireContext(),
                        description = sunSignDescription,
                        imageResource = sunSignImageId
                    )
                }
            }
        }

        return root
    }

    private fun initViews() {
        animateCover()
        btnDatePicker = binding.btnDatePicker
        tvSelectedDate = binding.tvSelectedDate
        btnDatePicker?.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        DatePicker.showDatePicker(requireContext()) { selectedDate ->
            homeViewModel.setSelectedDate(selectedDate)
        }
    }

    private fun animateCover() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                animateView()
                handler.postDelayed(this, 45000)
            }
        })
    }

    @SuppressLint("ObjectAnimatorBinding", "Recycle")
    private fun animateView() {
        // Translation Animation: Move back to the left
        val translationXLeft = ObjectAnimator.ofFloat(coverImage, "translationX", 200f, 0f)
        translationXLeft.duration = 10000
        translationXLeft.interpolator = DecelerateInterpolator()

        // Scale Animation: Zoom in
        val scaleXIn = ObjectAnimator.ofFloat(coverImage, "scaleX", 1f, 1.5f)
        scaleXIn.duration = 5000
        scaleXIn.interpolator = DecelerateInterpolator()

        val scaleYIn = ObjectAnimator.ofFloat(coverImage, "scaleY", 1f, 1.5f)
        scaleYIn.duration = 10000
        scaleYIn.interpolator = DecelerateInterpolator()

        // Scale Animation: Zoom out
        val scaleXOut = ObjectAnimator.ofFloat(coverImage, "scaleX", 1.5f, 1f)
        scaleXOut.duration = 10000
        scaleXOut.interpolator = DecelerateInterpolator()

        val scaleYOut = ObjectAnimator.ofFloat(coverImage, "scaleY", 1.5f, 1f)
        scaleYOut.duration = 10000
        scaleYOut.interpolator = DecelerateInterpolator()

        // Combine animations in a sequence
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(
            scaleXIn,           // Zoom in
            translationXLeft,   // Move back to the left
            scaleXOut           // Zoom out
        )

        animatorSet.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
