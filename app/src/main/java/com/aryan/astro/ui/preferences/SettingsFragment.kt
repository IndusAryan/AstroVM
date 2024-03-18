package com.aryan.astro.ui.preferences

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.aryan.astro.R
import com.aryan.astro.db.DataStoreHelper
import com.aryan.astro.ui.dialogs.HistoryDialog
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : PreferenceFragmentCompat() {
    private val darkMode = "dark_mode"

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val darkModeSwitch: SwitchPreferenceCompat? = findPreference("dark_mode")
        darkModeSwitch?.isChecked = isDarkModeEnabled()

        darkModeSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val isChecked = newValue as Boolean
            if (isChecked) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
            true
        }

        val sourcePreference: Preference? = findPreference("source")
        sourcePreference?.setOnPreferenceClickListener {
            val url = "https://github.com/IndusAryan/AstroVM"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            true
        }

        val update: Preference? = findPreference("update")
        update?.setOnPreferenceClickListener {
            Snackbar.make(requireContext(), requireView(), "App is up to date", Snackbar.LENGTH_SHORT).show()
            true
        }

        val deviceInfo: Preference? = findPreference("deviceInfo")
        deviceInfo?.summary =
            "Manufacturer: ${Build.MANUFACTURER}\n" +
            "Model: ${Build.MODEL}\n" +
            "SDK: ${Build.VERSION.SDK_INT}\n" +
            "Board: ${Build.BOARD}\n" +
            "OS: Android ${Build.VERSION.RELEASE}\n" +
            "Arch: ${Build.SUPPORTED_ABIS[0]}\n" +
            "Product: ${Build.PRODUCT}"

        val crash: Preference? = findPreference("crash")
        crash?.setOnPreferenceClickListener {
            throw NullPointerException()
        }

        val historyPreference: Preference? = findPreference("history")
        historyPreference?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val settingsDataStore = DataStoreHelper(context ?: requireContext())
                val historyList = settingsDataStore.getData()?.split(", ") ?: emptyList()
                if (historyList.isEmpty()) {
                    showHistoryDialog(listOf("Empty"))
                } else {
                    showHistoryDialog(historyList)
                }
                true
            }
    }

    private fun isDarkModeEnabled(): Boolean {
        context?.let {
            return PreferenceManager.getDefaultSharedPreferences(it)
                .getBoolean(darkMode, false)
        }
        return false
    }

    private fun showHistoryDialog(historyList: List<String>) {
        val dialogFragment = HistoryDialog(historyList)
        dialogFragment.show(parentFragmentManager, "history_dialog")
    }

    fun enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
