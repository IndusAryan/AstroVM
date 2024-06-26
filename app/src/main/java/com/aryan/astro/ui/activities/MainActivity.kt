package com.aryan.astro.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.aryan.astro.R
import com.aryan.astro.databinding.ActivityMainBinding
import com.aryan.astro.helpers.CrashLogHelper
import com.aryan.astro.ui.dialogs.ErrorDialog.errorDialog
import com.aryan.astro.ui.fragments.HomeFragment
import com.aryan.astro.ui.preferences.SettingsFragment
import com.aryan.astro.utils.ExceptionHandler
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val settingsManager = SettingsFragment()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For A12 and above, apply Material You style
            setTheme(R.style.AppTheme_MaterialYou)
        } else {
            setTheme(R.style.AppTheme)
        }

        if (auth.currentUser != null) {
            // User is signed in, navigate to HomeFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, HomeFragment())
                .commit()
        } else {
            // No user is signed in, navigate to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val isDarkModeEnabled = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean("dark_mode", true)

        val customTitleView =
            LayoutInflater.from(this).inflate(R.layout.custom_title_layout, null)
        val navView: BottomNavigationView = binding.navView

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

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }
}
