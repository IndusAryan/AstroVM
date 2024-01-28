package com.aryan.astro.ui.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val selectedDate = MutableLiveData<String>()

    fun getSelectedDate(): LiveData<String> {
        return selectedDate
    }

    fun setSelectedDate(date: String) {
        selectedDate.value = date
    }


    fun getSunSign(dob: CharSequence): String {

        val (day, month, year) = dob.split('/').map { it.toInt() }
        Log.d("DOB_Debug", "Input date: Day=$day, Month=$month, Year=$year")

        return when (month) {
            1 -> if (day >= 20) "Aquarius" else "Capricorn"
            2 -> if (day >= 19) "Pisces" else "Aquarius"
            3 -> if (day >= 21) "Aries" else "Pisces"
            4 -> if (day >= 20) "Taurus" else "Aries"
            5 -> if (day >= 21) "Gemini" else "Taurus"
            6 -> if (day >= 21) "Cancer" else "Gemini"
            7 -> if (day >= 23) "Leo" else "Cancer"
            8 -> if (day >= 23) "Virgo" else "Leo"
            9 -> if (day >= 23) "Libra" else "Virgo"
            10 -> if (day >= 23) "Scorpio" else "Libra"
            11 -> if (day >= 22) "Sagittarius" else "Scorpio"
            12 -> if (day >= 22) "Capricorn" else "Sagittarius"
            else -> "Invalid Date"
        }
    }
}