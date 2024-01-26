package com.aryan.astro.ui.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MoonViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val text: LiveData<String> = _text


    private val dateSelected = MutableLiveData<String>().apply {
        //value = "dd/mm/yyyy"
    }

    val date: LiveData<String> = dateSelected

    private val selectedDate = MutableLiveData<String>()

    fun getSelectedDate(): LiveData<String> {
        return selectedDate
    }

    fun setSelectedDate(date: String) {
        selectedDate.value = date
    }
}