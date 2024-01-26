package com.aryan.astro.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.aryan.astro.R
import com.aryan.astro.databinding.ActivityMainBinding
import com.aryan.astro.helpers.CrashLogHelper
import com.aryan.astro.ui.preferences.SettingsFragment
import com.aryan.astro.utils.ExceptionHandler
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var settingsManager = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For A12 and above, apply Material You style
            setTheme(R.style.AppTheme_MaterialYou);
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isDarkModeEnabled = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean("dark_mode", true)

        val customTitleView = LayoutInflater.from(this).inflate(R.layout.custom_title_layout, null)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBar: MaterialToolbar = findViewById(R.id.appBarLayout)
        appBar.addView(customTitleView)

        if (isDarkModeEnabled) {
           settingsManager.enableDarkMode()
        } else {
            settingsManager.disableDarkMode()
        }

        CrashLogHelper.initialize(applicationContext)

        val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        val exceptionHandler = ExceptionHandler(defaultExceptionHandler)
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)

        CrashLogHelper.getErrorLog().ifBlank { null }?.let {
            errorDialog(this)
        }

        navView.setupWithNavController(navController)
    }

    private fun errorDialog(context: Context) {
            val errorLog = CrashLogHelper.getErrorLog()
            val clipboardManager =
                this.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager?
            // reset the error log
            CrashLogHelper.saveErrorLog("")

            MaterialAlertDialogBuilder(context)
                .setTitle("Error occurred")
                .setMessage(errorLog)
                .setNegativeButton("Ok", null)
                .setPositiveButton("Copy") { _, _ ->
                    clipboardManager?.setPrimaryClip(ClipData.newPlainText("Title", errorLog))
                    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
}
