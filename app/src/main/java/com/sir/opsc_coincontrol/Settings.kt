package com.sir.opsc_coincontrol

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AppCompatDelegate

class Settings : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var darkModeSwitch: Switch
    private lateinit var buttonDebitOrder: Button // Declare the button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        darkModeSwitch = findViewById(R.id.darkLightSwitch)
        buttonDebitOrder = findViewById(R.id.aboutAppButton) // Initialize the button

        // Set the switch based on saved preference
        darkModeSwitch.isChecked = sharedPreferences.getBoolean("DARK_MODE", false)

        // Set the listener for the switch
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("DARK_MODE", isChecked)
            editor.apply()
            updateTheme(isChecked)
        }

        // Apply the theme based on saved preference
        updateTheme(darkModeSwitch.isChecked)

        // Set up the button click listener
        buttonDebitOrder.setOnClickListener {
            val intent = Intent(this, DebitOrder::class.java)
            startActivity(intent) // Start the DebitOrder activity
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateTheme(isDarkMode: Boolean) {
        // Set the appropriate UI mode
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES // Enable dark mode
        } else {
            AppCompatDelegate.MODE_NIGHT_NO // Disable dark mode
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
