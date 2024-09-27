package com.sir.opsc_coincontrol

import android.adservices.adid.AdId
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

class DebitOrder : AppCompatActivity() {


    private lateinit var rvDebit: RecyclerView
    private lateinit var debitAdapter: debitAdapter
    private lateinit var txtDue: TextView
    private lateinit var total_Amount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_debit_order)

        rvDebit = findViewById(R.id.rv_entries2)
        rvDebit.layoutManager = LinearLayoutManager(this)

        txtDue = findViewById(R.id.txtDue)
        total_Amount= findViewById(R.id.txtTotal)

        val userID = 7 // Change this to the actual Category ID you want to test with
        fetchTransactions(userID)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchTransactions(userID: Int) {
        // Fetch debit orders and display in RecyclerView
        RetrofitClient.instance.getDebitOrders(userID)
            .enqueue(object : Callback<List<DebitOrderClass>> {
                override fun onResponse(
                    call: Call<List<DebitOrderClass>>,
                    response: Response<List<DebitOrderClass>>
                ) {
                    if (response.isSuccessful) {
                        val transactions = response.body() ?: emptyList()
                        debitAdapter = debitAdapter(transactions)
                        rvDebit.adapter = debitAdapter
                    } else {
                        Toast.makeText(this@DebitOrder, "Failed to load transactions", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<DebitOrderClass>>, t: Throwable) {
                    Toast.makeText(this@DebitOrder, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        // Fetch total debit amount and display in txtTotal
        RetrofitClient.instance.getTotalDebitOrders()
            .enqueue(object : Callback<Double> {
                override fun onResponse(call: Call<Double>, response: Response<Double>) {
                    if (response.isSuccessful) {
                        val totalAmount = response.body() ?: 0f
                        total_Amount.text = "Total: $totalAmount" // Display total amount
                    } else {
                        Toast.makeText(this@DebitOrder, "Failed to load total amount", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Double>, t: Throwable) {
                    Toast.makeText(this@DebitOrder, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })




    }

}
