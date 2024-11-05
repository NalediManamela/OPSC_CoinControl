package com.sir.opsc_coincontrol

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var txtDontHaveAccount: TextView
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var button: Button
    private val REQ_ONE_TAP = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtDontHaveAccount = findViewById(R.id.txtDontHaveAccount)
        button = findViewById(R.id.btnSignInWithGoogle)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        sharedPreferences.edit().clear().apply()

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToCategories()
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        txtDontHaveAccount.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        // Initialize One Tap sign-in client
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.server_client_id)) // Your server's client ID
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()


        button.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    Log.d(TAG, e.localizedMessage ?: "No saved credentials found.")
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                if (resultCode == RESULT_OK && data != null) {
                    try {
                        val credential = oneTapClient.getSignInCredentialFromIntent(data)
                        val idToken = credential.googleIdToken
                        val username = credential.id
                        val password = credential.password



                        when {
                            idToken != null -> {
                                Log.d(TAG, "Got ID token.")
                                verifyToken(idToken)
                            }
                            password != null -> {
                                Log.d(TAG, "Got password.")
                            }
                            else -> {
                                Log.d(TAG, "No ID token or password!")
                            }
                        }
                    } catch (e: ApiException) {
                        Log.e(TAG, "Error: ${e.localizedMessage}")
                    }
                } else {
                    Log.d(TAG, "Sign-in failed or no data received.")
                }
            }
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

    private fun verifyToken(idToken: String) {
        RetrofitClient.instance.verifyToken(idToken).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    Log.d(TAG, "User ID: ${userResponse?.UserId}, User Name: ${userResponse?.UserName}, User Email: ${userResponse?.UserEmail}")

                    if (userResponse?.UserId != null) {
                        sharedPreferences.edit().apply {
                            putBoolean("isLoggedIn", true)
                            putInt("userId", userResponse.UserId)
                            putString("userName", userResponse.UserName)
                            apply()
                        }

                        Log.d(TAG, "User ID stored in SharedPreferences: ${userResponse.UserId}")
                        navigateToCategories()
                    }
                } else {
                    Log.e(TAG, "Verification failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e(TAG, "Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun navigateToCategories() {
        val intent = Intent(this,DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
