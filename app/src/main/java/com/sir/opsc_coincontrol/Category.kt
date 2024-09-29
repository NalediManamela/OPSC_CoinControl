package com.sir.opsc_coincontrol

import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)

        rvCategories = findViewById(R.id.rvCategories)
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory)
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Check if the activity was started with a refresh intent
        val shouldRefresh = intent.getBooleanExtra("refresh", false)
        if (shouldRefresh) {
            val userId = sharedPreferences.getInt("userId", -1)
            if (userId != -1) {
                fetchCategories(userId) // Refresh the categories if necessary
            }
        } else {
            val userId = sharedPreferences.getInt("userId", -1)
            if (userId != -1) {
                fetchCategories(userId) // Call fetchCategories with the userId from SharedPreferences
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        btnAddNewCategory.setOnClickListener {
            val intent = Intent(this, AddNewCategory::class.java)
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
                        // Handle category click
                        val intent = Intent(this@Category, Transaction::class.java)
                        intent.putExtra("categoryId", category.cat_ID) // Pass the categoryId to Transaction activity
                        startActivity(intent)
                    }
                    rvCategories.adapter = categoryAdapter
                } else {
                    Toast.makeText(this@Category, "Failed to fetch categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CategoryClass>>, t: Throwable) {
                Toast.makeText(this@Category, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}