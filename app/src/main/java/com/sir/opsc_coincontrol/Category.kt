package com.sir.opsc_coincontrol

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
    private lateinit var btnDashboard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)

        rvCategories = findViewById(R.id.rvCategories)
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory)
        btnDebit = findViewById(R.id.btnDebitOrders)
        btnSettings = findViewById(R.id.btnSet)
        btnDashboard = findViewById(R.id.btnDashboard)
        rvCategories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        categoryAdapter = CategoryAdapter(
            categories = mutableListOf(),
            onItemClick = { category ->
                val intent = Intent(this@Category, Transaction::class.java)
                intent.putExtra("categoryId", category.cat_ID)
                startActivity(intent)
            },
            onItemLongClickListener = { category ->
                showDeleteConfirmationDialog(category)
            },
            onFavouriteClick = { category ->
                toggleFavoriteStatus(category)
            }
        )

        rvCategories.adapter = categoryAdapter


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

        btnDashboard.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchCategories(userId: Int) {
        RetrofitClient.instance.getCategoriesByUser(userId)
            .enqueue(object : Callback<List<CategoryClass>> {
                override fun onResponse(
                    call: Call<List<CategoryClass>>,
                    response: Response<List<CategoryClass>>
                ) {
                    if (response.isSuccessful) {
                        val categories = response.body() ?: emptyList()
                        categoryAdapter.updateCategories(categories)
                    } else {
                        Toast.makeText(this@Category, "No categories", Toast.LENGTH_SHORT).show()
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
                deleteCategory(category.cat_ID)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteCategory(categoryId: Int) {
        RetrofitClient.instance.deleteCategory(categoryId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@Category,
                        "Category and its transactions deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    val updatedCategories =
                        categoryAdapter.retrieveCategories().filter { it.cat_ID != categoryId }
                    categoryAdapter.updateCategories(updatedCategories)

                } else {
                    Toast.makeText(
                        this@Category,
                        "Failed to delete category and its transactions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@Category, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleFavoriteStatus(category: CategoryClass) {
        // Safely toggle the favorite status, treating null as false
        category.isFavourite = !(category.isFavourite ?: false)

        // Check if categoryAdapter is initialized
        if (!::categoryAdapter.isInitialized) {
            Log.e("Category", "categoryAdapter is not initialized")
            return
        }

        // Update the favorite icon immediately (optimistic UI update)
        categoryAdapter.notifyDataSetChanged()

        // Ensure cat_ID and isFavorite are not null
        val catId = category.cat_ID
        val isFavorite = category.isFavourite

        if (catId != null && isFavorite != null) {
            // Make API call to update favorite status on the server
            RetrofitClient.instance.updateFavouriteStatus(catId, isFavorite)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (!response.isSuccessful) {
                            // If the update failed, revert the change
                            category.isFavourite = !(isFavorite)
                            categoryAdapter.notifyDataSetChanged()
                            Toast.makeText(
                                this@Category,
                                "Failed to update favorite status",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // If the update failed, revert the change
                        category.isFavourite = !(isFavorite)
                        categoryAdapter.notifyDataSetChanged()
                        Toast.makeText(
                            this@Category,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            Log.e("Category", "cat_ID or isFavorite is null")
            Toast.makeText(this, "Unable to update favorite status.", Toast.LENGTH_SHORT).show()
        }
    }

}
