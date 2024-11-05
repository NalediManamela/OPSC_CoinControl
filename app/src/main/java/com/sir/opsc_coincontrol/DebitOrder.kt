package com.sir.opsc_coincontrol

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DebitOrder : AppCompatActivity() {

    private lateinit var rvDebit: RecyclerView
    private lateinit var debitAdapter: debitAdapter
    private lateinit var txtDue: TextView
    private lateinit var txtTotal: TextView
    private lateinit var btnAddDebitOrder: ImageButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnCategory: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var btnHomeDebit: ImageButton
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debit_order)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        btnCategory = findViewById(R.id.btnCategory)
        btnSettings = findViewById(R.id.btnSettings)
        btnHomeDebit = findViewById(R.id.btnDashboardDebit)

        rvDebit = findViewById(R.id.rv_entries2)
        rvDebit.layoutManager = LinearLayoutManager(this)

        txtDue = findViewById(R.id.txtDUE)
        txtTotal = findViewById(R.id.txtTotal)
        btnAddDebitOrder = findViewById(R.id.btnadd)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", -1)
        Log.d(TAG, "Fetched User ID from SharedPreferences: $userId")

        btnCategory.setOnClickListener {
            val intent = Intent(this, Category::class.java)
            startActivity(intent)
        }

        btnHomeDebit.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }


        // Fetch data related to debit orders
        DebitOrders().fetchDebitOrders()
        DebitOrders().fetchTotalDueForCurrentMonth()
        DebitOrders().fetchTotalDebitAmount()

        btnAddDebitOrder.setOnClickListener {
            showAddDebitOrderDialog()
        }

    }

    private fun showAddDebitOrderDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_add_debit_order, null)
        val debitName = dialogView.findViewById<EditText>(R.id.DebitName)
        val debitAmount = dialogView.findViewById<EditText>(R.id.DebitAmount)
        val debitDate = dialogView.findViewById<EditText>(R.id.Due_Date)
        val dueDate = dialogView.findViewById<EditText>(R.id.Date)


        debitDate.setOnClickListener { showDatePickerDialog(debitDate, false) }
        dueDate.setOnClickListener { showDatePickerDialog(dueDate, true) }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Debit Order")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = debitName.text.toString()
                val amount = debitAmount.text.toString().toDoubleOrNull()
                val debitDateValue = debitDate.text.toString()
                val dueDateValue = dueDate.text.toString()

                if (name.isNotEmpty() && amount != null && debitDateValue.isNotEmpty() && dueDateValue.isNotEmpty()) {
                    DebitOrders().addDebitOrder(name, amount, debitDateValue, dueDateValue)
                } else {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showDatePickerDialog(editText: EditText, isDueDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val dateString = if (isDueDate) {

                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                        .format(Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, selectedDay)
                        }.time)
                } else {
                    // Format debit date as yyyy-MM-dd
                    SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        .format(Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, selectedDay)
                        }.time)
                }
                editText.setText(dateString)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private inner class DebitOrders {

        fun addDebitOrder(name: String, amount: Double, debitDate: String, dueDate: String) {
            // DebitOrderClass object
            val newDebitOrder = DebitOrderClass(
                debit_OrderID = 0,
                debit_Date = debitDate,
                due_Date = dueDate,
                debit_Name = name,
                debit_Amount = amount,
                userId,
                notifications = emptyList(),
                user = null
            )

            val gson = Gson()
            val requestBody = gson.toJson(newDebitOrder)
            Log.d("NewDebitOrderPayload", requestBody)

            // Send the new debit order to the server
            RetrofitClient.instance.createDebitOrder(newDebitOrder)
                .enqueue(object : Callback<DebitOrderClass> {
                    override fun onResponse(call: Call<DebitOrderClass>, response: Response<DebitOrderClass>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@DebitOrder, "Debit order added successfully", Toast.LENGTH_SHORT).show()
                            fetchDebitOrders() // Refresh the list with the user ID
                        } else {
                            Log.e("Error", "Error: ${response.code()} - ${response.message()}")
                            Toast.makeText(this@DebitOrder, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<DebitOrderClass>, t: Throwable) {
                        Log.e("Error", "Failed: ${t.message}")
                        Toast.makeText(this@DebitOrder, "Failed to add debit order: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        fun fetchDebitOrders() {
            RetrofitClient.instance.getDebit(userId)
                .enqueue(object : Callback<List<DebitOrderClass>> {
                    override fun onResponse(
                        call: Call<List<DebitOrderClass>>,
                        response: Response<List<DebitOrderClass>>
                    ) {
                        if (response.isSuccessful) {
                            val debitOrders = response.body() ?: emptyList()
                            debitAdapter = debitAdapter(debitOrders)
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

        fun fetchTotalDebitAmount() {
            RetrofitClient.instance.getTotalDebitOrders()
                .enqueue(object : Callback<Double> {
                    override fun onResponse(call: Call<Double>, response: Response<Double>) {
                        if (response.isSuccessful) {
                            val totalAmount = response.body() ?: 0.0
                            txtTotal.text = "Total: $totalAmount"
                        } else {
                            Toast.makeText(this@DebitOrder, "Failed to load total amount", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Double>, t: Throwable) {
                        Toast.makeText(this@DebitOrder, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        fun fetchTotalDueForCurrentMonth() {
            RetrofitClient.instance.getTotalDueForCurrentMonth()
                .enqueue(object : Callback<Double> {
                    override fun onResponse(call: Call<Double>, response: Response<Double>) {
                        if (response.isSuccessful) {
                            val totalDueAmount = response.body() ?: 0.0
                            txtDue.text = "Total Due this Month: $totalDueAmount"
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
}
