package com.sir.opsc_coincontrol

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

class AddNewCategory : AppCompatActivity() {

    private lateinit var edtCategoryName: EditText
    private lateinit var edtBudget: EditText
    private lateinit var btnAddCategory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_new_category)

        edtCategoryName = findViewById(R.id.edtCategoryName)
        edtBudget = findViewById(R.id.edtBudget)
        btnAddCategory = findViewById(R.id.btnAddCategory)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    interface ApiService {
        @POST("api/categories")
        fun postCategory(@Body category: Category): Call<Category>
    }
}