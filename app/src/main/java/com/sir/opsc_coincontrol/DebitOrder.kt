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
    private lateinit var txtTotal: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_debit_order)

        rvDebit = findViewById(R.id.rv_entries2)
        rvDebit.layoutManager = LinearLayoutManager(this)

        txtDue = findViewById(R.id.txtDUE)
        txtTotal= findViewById(R.id.txtTotal)

        val userID = 7 // Change this to the actual Category ID you want to test with
        fetchDebitOrders(userID)
        fetchTotalDueForCurrentMonth()
        fetchTotalDebitAmount()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchDebitOrders(userID: Int) {
        // Fetch debit orders for the specified user ID
        RetrofitClient.instance.getDebit(userID)
            .enqueue(object : Callback<List<DebitOrderClass>> {
                override fun onResponse(
                    call: Call<List<DebitOrderClass>>,
                    response: Response<List<DebitOrderClass>>
                ) {
                    if (response.isSuccessful) {
                        val debitOrders = response.body() ?: emptyList()
                        debitAdapter = debitAdapter(debitOrders) // Ensre your adapter is properly instantiated
                        rvDebit.adapter = debitAdapter
                    } else {
                        Toast.makeText(this@DebitOrder, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<DebitOrderClass>>, t: Throwable) {
                    Toast.makeText(this@DebitOrder, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun fetchTotalDebitAmount() {
        RetrofitClient.instance.getTotalDebitOrders()
            .enqueue(object : Callback<Double> {
                override fun onResponse(call: Call<Double>, response: Response<Double>) {
                    if (response.isSuccessful) {
                        val totalAmount = response.body() ?: 0f
                        txtTotal.text = "Total: $totalAmount" // Display total amount
                    } else {
                        Toast.makeText(this@DebitOrder, "Failed to load total amount", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Double>, t: Throwable) {
                    Toast.makeText(this@DebitOrder, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })


    }


    private fun fetchTotalDueForCurrentMonth() {
        // Fetch the total due for the current month and display it in txtDue
        RetrofitClient.instance.getTotalDueForCurrentMonth()
            .enqueue(object : Callback<Double> {
                override fun onResponse(call: Call<Double>, response: Response<Double>) {
                    if (response.isSuccessful) {
                        val totalDueAmount = response.body() ?: 0.0
                        txtDue.text = "Total Due this Month: $totalDueAmount" // Display total due amount
                    } else {
                        Toast.makeText(this@DebitOrder, "Failed to load total due amount", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Double>, t: Throwable) {
                    Toast.makeText(this@DebitOrder, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


}
