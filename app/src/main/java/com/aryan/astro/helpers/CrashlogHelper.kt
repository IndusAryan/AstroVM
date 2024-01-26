package com.aryan.astro.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

const val ERROR_LOG = ""

object CrashLogHelper {

    private lateinit var settings: SharedPreferences

    fun initialize(context: Context) {
        settings = getDefaultSharedPreferences(context)
    }

    private fun putString(key: String, value: String) {
        settings.edit(commit = true) { putString(key, value) }
    }

    fun saveErrorLog(log: String) {
        putString(ERROR_LOG, log)
    }

    private fun getString(key: String?, defValue: String): String {
        return settings.getString(key, defValue) ?: defValue
    }

    fun getErrorLog(): String {
        return getString(ERROR_LOG, "")
    }
}