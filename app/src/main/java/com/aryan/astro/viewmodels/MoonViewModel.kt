package com.aryan.astro.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.astro.repository.TimezoneAPI
import com.aryan.astro.repository.VedAstroAPI
import com.aryan.astro.repositorydata.FetchedTimezone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoonViewModel : ViewModel() {

    private val selectedDate = MutableLiveData<String>()

    // timezoneDB live data
    private val _timezone = MutableLiveData<FetchedTimezone?>()
    var timezone: LiveData<FetchedTimezone?> = _timezone

    // user inputs
    var bornCity: Any? = null
    var selectedTime: String? = null
    var day: String? = null
    var month: String? = null
    var year: String? = null

    private val _moonSign = MutableLiveData<String?>(null)
    val moonSign : LiveData<String?> = _moonSign

    // for error toasts
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage : LiveData<String?> = _errorMessage

    fun getSelectedDate(): LiveData<String> {
        return selectedDate
    }

    fun setSelectedDate(date: String) {
        selectedDate.value = date
    }

    init {
        // log
        timezone.observeForever { newVal ->
            Log.i("MVM", "timezone: ${newVal?.userTimezone}")
        }
    }

    // get timezone from user inputs
    fun fetchTimezone(city: Any?) {
        if (areInputsValid()) {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val fetchedTimezone = TimezoneAPI().getTimezone(city)
                        val timezoneData = FetchedTimezone(fetchedTimezone)
                        _timezone.postValue(timezoneData)
                        if (fetchedTimezone.isNotBlank()) {
                            fetchSign(fetchedTimezone)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _errorMessage.postValue("Error fetching timezone: ${e.message}")
                        _timezone.postValue(null)
                    }
                }
        }
    }

    // get moon sign from ved astro
    private fun fetchSign(timezone: String) {
        val city = bornCity.toString()
        val cityOfBirth = city.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }

        Log.i("MVM", "$selectedTime $cityOfBirth $day $month $year $timezone")
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val moonSignData = VedAstroAPI().fetchMoonSign(
                    cityOfBirth,
                    selectedTime,
                    day,
                    month,
                    year,
                    timezone,
                )
                _moonSign.postValue(moonSignData?.moonSign)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    // no calling if inputs are invalid || insufficient
    private fun areInputsValid(): Boolean {
        if (bornCity == null) {
            _errorMessage.postValue("Enter city of birth")
            return false
        }

        if (selectedTime.isNullOrBlank()) {
            _errorMessage.postValue("Enter time of birth")
            return false
        }

        if (day.isNullOrBlank()) {
            _errorMessage.postValue("Enter date of birth")
            return false
        }

        return true
    }

    fun clearData() {
        _timezone.value = null
        _moonSign.value = null
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        clearData()
    }
}