package com.sir.opsc_coincontrol

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

class Settings : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var darkModeSwitch: Switch

    private lateinit var privacyPolicyButton: Button
    private lateinit var termsButton: Button
    private lateinit var aboutAppButton: Button
    private lateinit var languageSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        darkModeSwitch = findViewById(R.id.darkLightSwitch)
        languageSwitch = findViewById(R.id.Languageswitch)
        privacyPolicyButton = findViewById(R.id.privacyPolicyButton)
        termsButton = findViewById(R.id.termsButton)
        aboutAppButton = findViewById(R.id.aboutAppButton)

        // Set initial state of the language switch based on saved preference
        val isAfrikaans = sharedPreferences.getBoolean("isAfrikaans", false)
        languageSwitch.isChecked = isAfrikaans
        setLocale(if (isAfrikaans) "af" else "en")

        // Listener for language switch
        languageSwitch.setOnCheckedChangeListener { _, isChecked ->
            val newLanguage = if (isChecked) "af" else "en"
            sharedPreferences.edit().putBoolean("isAfrikaans", isChecked).apply()
            setLocale(newLanguage)
            recreate() // Refresh activity to apply language change
        }

        darkModeSwitch.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }

        privacyPolicyButton.setOnClickListener { showPrivacyPolicyDialog() }
        termsButton.setOnClickListener { showTermsAndConditionsDialog() }
        aboutAppButton.setOnClickListener { showAboutAppDialog() }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    // Function to display the AlertDialog with Privacy Policy
    private fun showPrivacyPolicyDialog() {
        val privacyPolicyMessage = """
            Privacy Policy
            
            Your privacy is important to us. This Privacy Policy explains how the Finance Manager app collects, uses, and protects your information when you use our app.
            
            1. Information We Collect
            - Personal Information: When you register using Google SSO, we collect your name, email address, and Google profile information.
            - Financial Data: The app collects financial information that you input, such as expenses, budgets, and transactions. This data is securely stored for app functionality purposes.
            - Device Information: We may collect information about your device, including its operating system, device type, and app usage data, to help improve app performance.
            
            2. How We Use Your Information
            - We use your personal and financial data to provide, improve, and personalize the services of the Finance Manager app.
            - Your data is used to offer features such as budget tracking, expense management, and real-time notifications (e.g., when you approach a budget limit or detect unusual transactions).
            - We may use anonymized data for analytics to enhance app functionality, but this data cannot be linked back to individual users.
            
            3. Data Security
            - Your personal and financial data is stored securely using industry-standard encryption.
            - We utilize REST APIs to store transaction data securely and ensure your information is safe from unauthorized access.
            - The app uses HTTPS to encrypt all data transmitted between the app and our servers.
            
            4. Data Sharing and Disclosure
            - We do not share your personal or financial data with third parties unless required by law or for essential services (e.g., Google SSO for login purposes).
            - We may share anonymized, aggregated data with our partners for research and analysis purposes, but this data will not identify you personally.
            
            5. Data Retention
            - We retain your data for as long as you use the app. If you decide to delete your account, we will remove all personal and financial data from our servers within 30 days.
            
            6. Your Rights
            - You have the right to access, update, or delete your personal data at any time. If you want to modify your data, you can do so through the app's settings.
            - If you wish to delete your account or have questions regarding your data, you can contact us at support@financemanagerapp.com.
            
            7. Changes to This Policy
            - We may update this Privacy Policy from time to time. Any changes will be reflected in the app, and continued use of the app will signify your acceptance of the updated policy.
            
            8. Contact Us
            - If you have any questions or concerns about this Privacy Policy, please contact us at support@financemanagerapp.com.
            
            By using the Finance Manager app, you agree to this Privacy Policy.
        """.trimIndent()

        // Create and show the AlertDialog
        AlertDialog.Builder(this)
            .setTitle("Privacy Policy")
            .setMessage(privacyPolicyMessage)
            .setPositiveButton("OK", null)
            .show()
    }

    // Function to display the AlertDialog with Terms and Conditions
    private fun showTermsAndConditionsDialog() {
        val termsAndConditionsMessage = """
            Terms and Conditions
            
            Welcome to the Finance Manager app. By downloading or using this app, these terms will automatically apply to you. Please ensure you read them carefully before using the app.
            
            1. User Responsibilities
            - You agree to use the app in compliance with applicable laws and not to misuse the app or attempt to gain unauthorized access to any part of the app.
            - You are responsible for maintaining the confidentiality of your login credentials (Google SSO) and must notify us immediately of any unauthorized access or use.
            
            2. Data Collection and Privacy
            - The app uses Google’s SSO for secure login. By using the app, you agree to Google's Privacy Policy.
            - We collect only necessary transaction data to provide the core features of the app (e.g., storing expenses, budgets).
            - All financial data you provide through the app is securely stored via a REST API on our servers. We use this data only for app functionality and do not share it with third parties unless required by law.
            
            3. Offline Mode
            - The app supports offline mode for recording expenses. Data synchronization happens automatically once an internet connection is available. Please note that financial reports and transaction histories may not be up-to-date while offline.
            
            4. Limitation of Liability
            - While we strive to provide a smooth and accurate financial management experience, the app is provided "as is." We make no warranties or guarantees that the app will be free from errors, bugs, or interruptions.
            - We are not responsible for any loss of financial data or any damages arising from the use of the app, including but not limited to financial inaccuracies or budgeting errors caused by user input or technical issues.
            
            5. Modification of Terms
            - We reserve the right to modify these terms and conditions at any time. Changes will be effective upon posting. Continued use of the app following the changes will constitute your acceptance of the updated terms.
            
            6. Contact Us
            - If you have any questions about these Terms and Conditions, you can contact us at support@financemanagerapp.com.
        """.trimIndent()

        // Create and show the AlertDialog
        AlertDialog.Builder(this)
            .setTitle("Terms and Conditions")
            .setMessage(termsAndConditionsMessage)
            .setPositiveButton("Accept", null)
            .setNegativeButton("Decline", null)
            .show()
    }

    // Function to display the AlertDialog with the app details
    private fun showAboutAppDialog() {
        val aboutAppMessage = """
            Finance management can be overwhelming, but the Finance Manager app is here to 
            make things a little easier. The user-friendly and full-featured app helps users manage 
            their finances by charting expenses against set budgets. This is especially useful when 
            saving up for a big buy or just taking control of monthly expenditure. With the Finance 
            Manager app, you have what you need at your fingertips to hit your financial goals out 
            of the park.

            Some of its major features include the very fluent registration and login processes: 
            users can register and log in quickly via Google's SSO service for a secure and easy 
            entry point into financial management. Once logged in, users can access the full 
            financial dashboard where they can input their income, expenses, and set budgets. The 
            app also features useful charts and reports that help visualize the financial situation, 
            allowing users to make informed decisions.

            Our goal is to continuously improve the app by integrating user feedback, so feel free 
            to share your experience with us!
            
            Version: 1.0.0
            Developer: Your Name
            Contact: support@financemanagerapp.com
        """.trimIndent()

        // Create and show the AlertDialog
        AlertDialog.Builder(this)
            .setTitle("About the App")
            .setMessage(aboutAppMessage)
            .setPositiveButton("OK", null)
            .show()
    }
}
