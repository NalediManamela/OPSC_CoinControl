package com.sir.opsc_coincontrol

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sir.opsc_coincontrol.adapters.TransactionsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class Transaction : AppCompatActivity() {


    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionsAdapter
    private lateinit var txtAmountSpent: TextView
    private lateinit var txtAmountBudgeted: TextView
    private lateinit var txtAverageSpent: TextView
    private lateinit var btnAddTransaction: ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        rvTransactions = findViewById(R.id.rvTransactions)
        rvTransactions.layoutManager = LinearLayoutManager(this)

        txtAmountBudgeted = findViewById(R.id.txtViewBudgeted)
        txtAmountSpent = findViewById(R.id.txtViewSpent)
        txtAverageSpent = findViewById(R.id.txtViewAverage)

        btnAddTransaction = findViewById(R.id.btnAddTransaction)


        val categoryId = intent.getIntExtra("categoryId", -1)
        if (categoryId != -1) {
            fetchTransactions(categoryId)
            fetchCategoryDetails(categoryId)
        } else {
            Toast.makeText(this, "Invalid Category ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnAddTransaction.setOnClickListener {
            showAddTransactionDialog(categoryId)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnAddDebit)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addTransaction(categoryId: Int, name: String, amount: Double, date: String) {
        val newTransaction = TransactionsClass(
            transaction_ID = 0,
            date = date,
            transaction_Name = name,
            transaction_Amount = amount,
            cat_ID = categoryId
        )

        RetrofitClient.instance.postTransaction(newTransaction)
            .enqueue(object : Callback<TransactionsClass> {
                override fun onResponse(
                    call: Call<TransactionsClass>,
                    response: Response<TransactionsClass>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@Transaction,
                            "Transaction added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Refresh transactions list
                        fetchTransactions(categoryId)
                        fetchCategoryDetails(categoryId)
                    } else {
                        Toast.makeText(
                            this@Transaction,
                            "Failed to add transaction",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<TransactionsClass>, t: Throwable) {
                    Toast.makeText(this@Transaction, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
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
                        transactionAdapter = TransactionsAdapter(transactions) { transaction ->

                        }

                        // Long-click listener for deleting transactions
                        transactionAdapter.setOnItemLongClickListener { transaction ->
                            showDeleteTransactionConfirmationDialog(transaction)
                        }

                        rvTransactions.adapter = transactionAdapter
                    } else {
                        Toast.makeText(this@Transaction, "No transactions", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<TransactionsClass>>, t: Throwable) {
                    Toast.makeText(this@Transaction, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showDeleteTransactionConfirmationDialog(transaction: TransactionsClass) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Delete Transaction")
        dialogBuilder.setMessage("Are you sure you want to delete this transaction?")

        dialogBuilder.setPositiveButton("Delete") { _, _ ->
            deleteTransaction(transaction)
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun deleteTransaction(transaction: TransactionsClass) {
        RetrofitClient.instance.deleteTransaction(transaction.transaction_ID)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@Transaction, "Transaction deleted successfully!", Toast.LENGTH_SHORT).show()

                        transaction.cat_ID?.let { fetchTransactions(it) }
                    } else {
                        Toast.makeText(this@Transaction, "Failed to delete transaction", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(this@Transaction, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchCategoryDetails(categoryId: Int) {
        RetrofitClient.instance.getCategoryById(categoryId)
            .enqueue(object : Callback<CategoryClass> {
                override fun onResponse(
                    call: Call<CategoryClass>,
                    response: Response<CategoryClass>
                ) {
                    if (response.isSuccessful) {
                        val category = response.body()
                        if (category != null) {

                            val amountSpent = category.amountSpent ?: 0.0
                            val budget = category.budget ?: 0.0
                            val averageSpending = category.average ?: 0.0

                            val formattedAverage = String.format("%.2f", averageSpending) // Limit to 2 decimals

                            txtAmountSpent.text = "Amount Spent: R${String.format("%.2f", amountSpent)}"
                            txtAmountBudgeted.text = "Budget: R${String.format("%.2f", budget)}"
                            txtAverageSpent.text = "Average Spending: R$formattedAverage"


                            // Set the color to red if amount spent is greater than the budget
                            if (amountSpent > budget) {
                                txtAmountBudgeted.setTextColor(Color.parseColor("#FF0000"))  // Assuming you have a red color in your resources
                            } else {
                                txtAmountBudgeted.setTextColor(getColor(android.R.color.black)) // Default color
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@Transaction,
                            "Failed to load category details",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CategoryClass>, t: Throwable) {
                    Toast.makeText(this@Transaction, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    //Pop up dialog for adding transactions
    private fun showAddTransactionDialog(categoryId: Int) {

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Add New Transaction")


        val dialogView = layoutInflater.inflate(R.layout.dialog_add_transaction, null)
        dialogBuilder.setView(dialogView)

        val edtTransactionName = dialogView.findViewById<EditText>(R.id.edtTransactionName)
        val edtTransactionAmount = dialogView.findViewById<EditText>(R.id.edtTransactionAmount)
        val edtTransactionDate = dialogView.findViewById<EditText>(R.id.edtTransactionDate)


        edtTransactionDate.isFocusable = false
        edtTransactionDate.isClickable = true
        edtTransactionDate.setOnClickListener {
            showDatePickerDialog(edtTransactionDate)
        }

        dialogBuilder.setPositiveButton("Add") { dialog, _ ->
            val transactionName = edtTransactionName.text.toString().trim()
            val transactionAmount = edtTransactionAmount.text.toString().trim().toDoubleOrNull()
            val transactionDate = edtTransactionDate.text.toString().trim()

            if (transactionName.isEmpty() || transactionAmount == null || transactionDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                addTransaction(categoryId, transactionName, transactionAmount, transactionDate)
                dialog.dismiss()
            }
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }


    private fun showDatePickerDialog(edtTransactionDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                edtTransactionDate.setText(formattedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

}

