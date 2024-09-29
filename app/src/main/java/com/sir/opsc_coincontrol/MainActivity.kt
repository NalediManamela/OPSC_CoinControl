package com.sir.opsc_coincontrol

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var txtDontHaveAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtDontHaveAccount = findViewById(R.id.txtDontHaveAccount)


        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        sharedPreferences.edit().clear().apply()

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // Navigate to the categories screen if already logged in
            navigateToCategories()
        }

        // Set up login button click listener
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Set OnClickListener for txtDontHaveAccount
        txtDontHaveAccount.setOnClickListener {
            // Intent to navigate to RegisterActivity
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            }

    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        RetrofitClient.instance.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    // Save user details in SharedPreferences
                    sharedPreferences.edit().apply {
                        putBoolean("isLoggedIn", true)
                        putInt("userId", loginResponse.userId)
                        putString("userName", loginResponse.userName)
                        apply()
                    }
                    Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_SHORT).show()
                    // Navigate to categories screen
                    navigateToCategories()
                } else {
                    Toast.makeText(this@MainActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToCategories() {
        val intent = Intent(this, Category::class.java)
        startActivity(intent)
        finish()
    }
}
