package com.sir.opsc_coincontrol

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)

        rvCategories = findViewById(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fetchCategories()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun fetchCategories() {
        // Hardcoded UserID for now
        val userID = 7

        RetrofitClient.instance.getCategoriesByUser(userID).enqueue(object : Callback<List<CategoryClass>> {
            override fun onResponse(call: Call<List<CategoryClass>>, response: Response<List<CategoryClass>>) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    categoryAdapter = CategoryAdapter(categories) { category ->
                        // Handle category click
                        Toast.makeText(this@Category, "Clicked: ${category.categoryName}", Toast.LENGTH_SHORT).show()
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