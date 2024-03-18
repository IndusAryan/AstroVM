package com.aryan.astro.db

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

class DataStoreHelper(private val context: Context) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveData(data: String) {
        val currentData = getData().orEmpty().split("\n").filter { it.isNotEmpty() }.toMutableList()
        if (currentData.size >= 10) {
            currentData.removeAt(currentData.size - 1)
        }
        currentData.add(0, data) // Add new data at the beginning
        val updatedData = currentData.joinToString(separator = "\n")
        sharedPreferences.edit().putString(KEY_DATA, updatedData).apply()
    }

    fun getData(): String? {
        return sharedPreferences.getString(KEY_DATA, null)
    }

    fun clearData() {
        sharedPreferences.edit().remove(KEY_DATA).apply()
        Toast.makeText(context, "History Cleared", LENGTH_SHORT).show()
    }

    companion object {
        private const val PREFS_NAME = "SIGN_NAME"
        private const val KEY_DATA = "zodiac_date"
    }
}
