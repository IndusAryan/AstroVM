package com.aryan.astro.utils

import android.app.TimePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar

object TimePicker {

    fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {

        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val formattedTime = SimpleDateFormat("HH:mm").format(cal.time)
            onTimeSelected(formattedTime)
        }

        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }
}