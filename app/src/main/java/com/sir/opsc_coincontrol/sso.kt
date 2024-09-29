package com.sir.opsc_coincontrol

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback

class SSO : AppCompatActivity() {// sso

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var button: Button  // Declare the button here
    private val REQ_ONE_TAP = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the button after setting the content view
        button = findViewById(R.id.btnSignInWithGoogle)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

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
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.server_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

        // Set the onClickListener for the button
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
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(TAG, e.localizedMessage)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password



                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d(TAG, "Got ID token.")
                            verifyToken(idToken)

                        }

                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d(TAG, "Got password.")
                        }

                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    // Handle the exception
                    Log.e(TAG, "Error: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun verifyToken(idToken: String) {
        RetrofitClient.instance.verifyToken(idToken).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    val userId = userResponse?.UserId
                    Log.d(TAG, "User ID: ${userResponse?.UserId}, User Name: ${userResponse?.Username}, User Email: ${userResponse?.UserEmail}")

                    if (userId != null) {
                        sharedPreferences.edit().apply {
                            putBoolean("isLoggedIn", true)
                            putInt("userId", userId)
                            putString("userName", userResponse.Username)
                            apply()
                        }
                    }

                    // Navigate to the categories screen after successful login
                    if (userId != null) {
                        navigateToCategories(userId)
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

    private fun navigateToCategories(userId: Int) {
        val intent = Intent(this, Category::class.java)
        intent.putExtra("USER_ID", userId)  // Pass the user ID
        startActivity(intent)
        finish()
    }

}


