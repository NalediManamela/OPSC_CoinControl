package com.sir.opsc_coincontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {

    private lateinit var edtRegUserName: EditText
    private lateinit var edtRegEmail: EditText
    private lateinit var edtRegPassword: EditText
    private lateinit var edtRegConfPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        edtRegUserName = findViewById(R.id.edtRegUserName)
        edtRegEmail = findViewById(R.id.edtRegEmail)
        edtRegPassword = findViewById(R.id.edtRegPassword)
        edtRegConfPassword = findViewById(R.id.edtRegConfPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val username = edtRegUserName.text.toString().trim()
            val email = edtRegEmail.text.toString().trim()
            val password = edtRegPassword.text.toString().trim()
            val confirmPassword = edtRegConfPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call the Register API
            registerUser(username, email, password)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnAddDebit)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

    }

    private fun registerUser(username: String, email: String, password: String) {
        val registerRequest = RegisterClass(username, email, password)

        RetrofitClient.instance.registerUser(registerRequest)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@Register, "Registration successful!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@Register, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Register, "Registration failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@Register, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}