package com.sir.opsc_coincontrol

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sir.opsc_coincontrol.adapters.TransactionsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Transaction : AppCompatActivity() {


    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionsAdapter
    private lateinit var txtAmountSpent: TextView
    private lateinit var txtAmountBudgeted: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction)

        rvTransactions = findViewById(R.id.rvTransactions)
        rvTransactions.layoutManager = LinearLayoutManager(this)

        txtAmountBudgeted = findViewById(R.id.txtViewBudgeted)
        txtAmountSpent= findViewById(R.id.txtViewSpent)

        val categoryId = 7 // Change this to the actual Category ID you want to test with
        fetchTransactions(categoryId)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchTransactions(categoryId: Int) {
        RetrofitClient.instance.getTransactionsByCategory(categoryId)
            .enqueue(object : Callback<List<TransactionsClass>> {
                override fun onResponse(
                    call: Call<List<TransactionsClass>>,
                    response: Response<List<TransactionsClass>>
                ) {
                    if (response.isSuccessful) {
                        val transactions = response.body() ?: emptyList()
                        transactionAdapter = TransactionsAdapter(transactions)
                        rvTransactions.adapter = transactionAdapter
                    } else {
                        Toast.makeText(this@Transaction, "Failed to load transactions", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<TransactionsClass>>, t: Throwable) {
                    Toast.makeText(this@Transaction, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })



        /*
        val categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        if (categoryId != -1) {
            fetchTransactions(categoryId)
        } else {
            Toast.makeText(this, "Invalid Category ID", Toast.LENGTH_SHORT).show()
        }
         */
    }
}