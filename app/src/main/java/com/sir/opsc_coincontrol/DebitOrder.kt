package com.sir.opsc_coincontrol

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
    private lateinit var btnAddDebitOrder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debit_order)

        rvDebit = findViewById(R.id.rv_entries2)
        rvDebit.layoutManager = LinearLayoutManager(this)

        txtDue = findViewById(R.id.txtDUE)
        txtTotal = findViewById(R.id.txtTotal)
        btnAddDebitOrder = findViewById(R.id.btnAdd)

        val userID = 7 // Replace with actual Category ID or user ID
        fetchDebitOrders(userID)
        fetchTotalDueForCurrentMonth()
        fetchTotalDebitAmount()

        btnAddDebitOrder.setOnClickListener {
            showAddDebitOrderDialog()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnAddDebit)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showAddDebitOrderDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_add_debit_order, null)
        val debitName = dialogView.findViewById<EditText>(R.id.DebitName)
        val debitAmount = dialogView.findViewById<EditText>(R.id.DebitAmount)
        val debitDate = dialogView.findViewById<EditText>(R.id.Due_Date)
        val dueDate = dialogView.findViewById<EditText>(R.id.Date)

        // Set up the date pickers
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
                    addDebitOrder(name, amount, debitDateValue, dueDateValue)
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
                // Format the selected date based on whether it's debitDate or dueDate
                val dateString = if (isDueDate) {
                    // Format due date as ISO 8601
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

    private fun addDebitOrder(name: String, amount: Double, debitDate: String, dueDate: String) {
        // Create a new DebitOrderClass object with the correct format
        val newDebitOrder = DebitOrderClass(
            debit_OrderID = 0,         // New debit order ID should be 0
            debit_Date = debitDate,    // Should be in yyyy-MM-dd format
            due_Date = dueDate,        // Should be in ISO 8601 format
            debit_Name = name,
            debit_Amount = amount,
            userID = 7,                // Replace with actual userID
            notifications = emptyList(), // Empty list for notifications
            user = null                // Null for user as per your example
        )

        // Log the JSON body for debugging purposes
        val gson = Gson()
        val requestBody = gson.toJson(newDebitOrder)
        Log.d("NewDebitOrderPayload", requestBody)

        // Send the new debit order to the server
        RetrofitClient.instance.createDebitOrder(newDebitOrder)
            .enqueue(object : Callback<DebitOrderClass> {
                override fun onResponse(call: Call<DebitOrderClass>, response: Response<DebitOrderClass>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DebitOrder, "Debit order added successfully", Toast.LENGTH_SHORT).show()
                        fetchDebitOrders(7) // Refresh the list with the user ID
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


    private fun fetchDebitOrders(userID: Int) {
        RetrofitClient.instance.getDebit(userID)
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

    private fun fetchTotalDebitAmount() {
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

    private fun fetchTotalDueForCurrentMonth() {
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
