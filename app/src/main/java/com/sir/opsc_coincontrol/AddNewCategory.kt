package com.sir.opsc_coincontrol

import android.content.SharedPreferences
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

class AddNewCategory : AppCompatActivity() {

    private lateinit var edtCategoryName: EditText
    private lateinit var edtBudget: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_new_category)

        edtCategoryName = findViewById(R.id.edtCategoryName)
        edtBudget = findViewById(R.id.edtBudget)
        btnAddCategory = findViewById(R.id.btnAddCategory)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnAddCategory.setOnClickListener {
            val categoryName = edtCategoryName.text.toString().trim()
            val budgetCat = edtBudget.text.toString().trim().toDoubleOrNull()

            // Validate inputs
            if (categoryName.isEmpty() || budgetCat == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Retrieve the userId from SharedPreferences
            val userId = sharedPreferences.getInt("userId", -1)
            if (userId == -1) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the category object with the userId
            val category = CategoryClass(
                userID = userId,
                categoryName = categoryName,
                budget = budgetCat,
                amountSpent = 0.0 // Initially zero since the category is new
            )

            // Call the API to add the category
            postCategory(category)
        }
    }

    private fun postCategory(category: CategoryClass) {
        RetrofitClient.instance.postCategory(category).enqueue(object : Callback<CategoryClass> {
            override fun onResponse(call: Call<CategoryClass>, response: Response<CategoryClass>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@AddNewCategory,
                        "Category added successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Clear the input fields after successful addition
                    edtCategoryName.text.clear()
                    edtBudget.text.clear()
                    // You might want to finish the activity and return to the previous screen
                    finish()
                } else {
                    Toast.makeText(
                        this@AddNewCategory,
                        "Failed to add category",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CategoryClass>, t: Throwable) {
                Toast.makeText(
                    this@AddNewCategory,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
