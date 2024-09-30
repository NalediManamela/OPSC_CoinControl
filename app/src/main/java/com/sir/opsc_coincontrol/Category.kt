package com.sir.opsc_coincontrol

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sir.opsc_coincontrol.adapters.CategoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Category : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnAddNewCategory: ImageButton
    private lateinit var btnDebit: ImageButton
    private lateinit var btnSettings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)

        rvCategories = findViewById(R.id.rvCategories)
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory)
        btnDebit = findViewById(R.id.btnDebitOrders)
        btnSettings = findViewById(R.id.btnSet)
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Get userId from SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)
        Log.d(TAG, "Fetched User ID from SharedPreferences: $userId")

        // Fetch categories if userId is valid
        if (userId != -1) {
            fetchCategories(userId)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        btnAddNewCategory.setOnClickListener {
            val intent = Intent(this, AddNewCategory::class.java)
            startActivity(intent)
        }

        btnDebit.setOnClickListener {
            val intent = Intent(this, DebitOrder::class.java)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchCategories(userId: Int) {
        RetrofitClient.instance.getCategoriesByUser(userId).enqueue(object : Callback<List<CategoryClass>> {
            override fun onResponse(call: Call<List<CategoryClass>>, response: Response<List<CategoryClass>>) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    categoryAdapter = CategoryAdapter(categories) { category ->
                        // Handle category click to view transactions
                        val intent = Intent(this@Category, Transaction::class.java)
                        intent.putExtra("categoryId", category.cat_ID) // Pass the categoryId to Transaction activity
                        startActivity(intent)
                    }

                    rvCategories.adapter = categoryAdapter

                    // Set up the long-click listener for deleting categories
                    categoryAdapter.setOnItemLongClickListener { category ->
                        showDeleteConfirmationDialog(category)
                    }
                } else {
                    Toast.makeText(this@Category, "Failed to fetch categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CategoryClass>>, t: Throwable) {
                Toast.makeText(this@Category, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog(category: CategoryClass) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete ${category.categoryName}?")
            .setPositiveButton("Yes") { _, _ ->
                deleteCategory(category.cat_ID) // Replace with the correct property for category ID
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteCategory(categoryId: Int) {
        RetrofitClient.instance.deleteCategory(categoryId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Category, "Category deleted successfully", Toast.LENGTH_SHORT).show()
                    // Refresh the category list after deletion
                    val userId = sharedPreferences.getInt("userId", -1)
                    if (userId != -1) fetchCategories(userId)
                } else {
                    Toast.makeText(this@Category, "Failed to delete category", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@Category, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
