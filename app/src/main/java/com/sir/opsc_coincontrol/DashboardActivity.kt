package com.sir.opsc_coincontrol

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.sir.opsc_coincontrol.adapters.CategoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var txtTotalSpending: TextView
    private lateinit var txtBudgetUtilization: TextView
    private lateinit var txtMostFrequentCategories: TextView
    private lateinit var rvFavoriteCategories: RecyclerView
    private lateinit var favouriteCategoriesAdapter: CategoryAdapter
    private lateinit var btnAddNewCategory: ImageButton
    private lateinit var btnDebit: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var btnCategoryDash: ImageButton

    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Int = -1

    private var categories: List<CategoryClass> = emptyList()
    private var transactions: List<TransactionsClass> = emptyList()
    private var transactionsDTO: List<TransactionDTO> = emptyList()

    private var completedCalls = 0
    private val NOTIFICATION_THRESHOLD = 3000.0
    private val CHANNEL_ID = "CATEGORY_THRESHOLD_NOTIFICATION"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        pieChart = findViewById(R.id.pieChart)
        txtTotalSpending = findViewById(R.id.txtTotalSpending)
        txtBudgetUtilization = findViewById(R.id.txtBudgetUtilization)
        txtMostFrequentCategories = findViewById(R.id.txtMostFrequentCategories)

        btnAddNewCategory = findViewById(R.id.btnAddNewCategory)
        btnDebit = findViewById(R.id.btnDebitOrders)
        btnSettings = findViewById(R.id.btnSet)
        btnCategoryDash = findViewById(R.id.btnCategoryDash)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", -1)

        if (userId != -1) {
            fetchDashboardData(userId)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            // Redirect to login if necessary
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

        btnCategoryDash.setOnClickListener {
            val intent = Intent(this, Category::class.java)
            startActivity(intent)
        }

        rvFavoriteCategories = findViewById(R.id.rvFavouriteCategories)
        rvFavoriteCategories.layoutManager = LinearLayoutManager(this)

        favouriteCategoriesAdapter = CategoryAdapter(
            categories = listOf(),
            onItemClick = { category ->
                // Handle click to open transactions for the category
                openCategoryTransactions(category)
            },
            onItemLongClickListener = null,
            onFavouriteClick = null,
            isEditable = false // Set to false to disable favorite toggling
        )

        rvFavoriteCategories.adapter = favouriteCategoriesAdapter


        rvFavoriteCategories.adapter = favouriteCategoriesAdapter

        // Existing code...

        // Fetch favorite categories
        fetchFavoriteCategories(userId)

        createNotificationChannel()
    }

    private fun displayFavoriteCategories(categories: List<CategoryClass>) {
        categories.forEach { category ->
            Log.d("DashboardActivity", "Category: ${category.categoryName}")
        }
        favouriteCategoriesAdapter.updateCategories(categories)
    }




    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Category Threshold Notifications"
            val descriptionText = "Notifies when a category exceeds the specified threshold"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun sendThresholdNotification(category: String, amount: Double) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.grocery)
            .setContentTitle("Spending Alert")
            .setContentText("Category '$category' has exceeded R$NOTIFICATION_THRESHOLD with spending of R${String.format("%.2f", amount)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setLights(Color.RED, 3000, 3000)

        try {
            with(NotificationManagerCompat.from(this)) {
                // Check for notification permission on Android 13 and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        notify(category.hashCode(), notificationBuilder.build())
                    }
                } else {
                    notify(category.hashCode(), notificationBuilder.build())
                }
            }
        } catch (e: SecurityException) {
            Log.e("DashboardActivity", "Failed to send notification: ${e.message}")
            Toast.makeText(this, "Notification permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCategoryTransactions(category: CategoryClass) {
        // Implement the logic to open the transactions for the selected category
        val intent = Intent(this, Transaction::class.java)
        intent.putExtra("categoryId", category.cat_ID)
        startActivity(intent)
    }

    private fun fetchDashboardData(userId: Int) {
        Log.d("DashboardActivity", "Fetching data for userId: $userId")
        RetrofitClient.instance.getCategoriesByUser(userId)
            .enqueue(object : Callback<List<CategoryClass>> {
                override fun onResponse(
                    call: Call<List<CategoryClass>>,
                    response: Response<List<CategoryClass>>
                ) {
                    if (response.isSuccessful) {
                        categories = response.body() ?: emptyList()
                    } else {
                        Toast.makeText(
                            this@DashboardActivity,
                            "Failed to load categories",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    checkIfDataFetchComplete()
                }

                override fun onFailure(call: Call<List<CategoryClass>>, t: Throwable) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Error fetching categories: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    checkIfDataFetchComplete()
                }
            })

        RetrofitClient.instance.getTransactionsByUser(userId)
            .enqueue(object : Callback<List<TransactionDTO>> {
                override fun onResponse(call: Call<List<TransactionDTO>>, response: Response<List<TransactionDTO>>) {
                    if (response.isSuccessful) {
                        transactionsDTO = response.body() ?: emptyList()
                    } else {
                        val statusCode = response.code()
                        val errorBody = response.errorBody()?.string()
                        Log.e("DashboardActivity", "Error fetching transactions. Status code: $statusCode, Error: $errorBody")
                        Toast.makeText(this@DashboardActivity, "Failed to load transactions", Toast.LENGTH_SHORT).show()
                    }
                    checkIfDataFetchComplete()
                }

                override fun onFailure(call: Call<List<TransactionDTO>>, t: Throwable) {
                    Toast.makeText(this@DashboardActivity, "Error fetching transactions: ${t.message}", Toast.LENGTH_SHORT).show()
                    checkIfDataFetchComplete()
                }
            })
    }

    private fun checkIfDataFetchComplete() {
        completedCalls++

        if (completedCalls >= 2) {
            if (categories.isNotEmpty() && transactionsDTO.isNotEmpty()) {
                processDashboardData()
            } else {
                Toast.makeText(this, "Insufficient data to display dashboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processDashboardData() {
        val categoryMap = categories.associateBy { it.cat_ID }
        val spendingMap = calculateSpendingPerCategory(transactionsDTO, categoryMap)
        val totalSpending = calculateTotalSpending(transactionsDTO)
        val totalBudget = calculateTotalBudget(categories)
        val mostFrequentCategories = getMostFrequentCategories(transactionsDTO, categoryMap)

        // Check spending thresholds for each category
        spendingMap.forEach { (categoryName, amount) ->
            if (amount > NOTIFICATION_THRESHOLD) {
                sendThresholdNotification(categoryName, amount)
            }
        }

        setupPieChart(spendingMap)
        updateSummaryUI(totalSpending, totalBudget, mostFrequentCategories)
    }

    private fun calculateSpendingPerCategory(
        transactions: List<TransactionDTO>,
        categoryMap: Map<Int, CategoryClass>
    ): Map<String, Double> {
        val spendingMap = mutableMapOf<String, Double>()

        transactions.forEach { transaction ->
            val categoryName = categoryMap[transaction.cat_ID]?.categoryName ?: "Unknown"
            val amount = transaction.transaction_Amount ?: 0.0

            spendingMap[categoryName] = (spendingMap[categoryName] ?: 0.0) + amount
        }

        return spendingMap
    }

    private fun calculateTotalSpending(transactions: List<TransactionDTO>): Double {
        return transactions.sumOf { it.transaction_Amount ?: 0.0 }
    }

    private fun calculateTotalBudget(categories: List<CategoryClass>): Double {
        return categories.sumOf { it.budget }
    }

    private fun getMostFrequentCategories(
        transactions: List<TransactionDTO>,
        categoryMap: Map<Int, CategoryClass>
    ): List<String> {
        val frequencyMap = transactions.groupingBy { it.cat_ID }.eachCount()
        val sortedCategories = frequencyMap.entries.sortedByDescending { it.value }
        val topCategories = sortedCategories.take(3)
        return topCategories.map { categoryMap[it.key]?.categoryName ?: "Unknown" }
    }

    private fun setupPieChart(spendingMap: Map<String, Double>) {
        val entries = ArrayList<PieEntry>()

        for ((category, amount) in spendingMap) {
            entries.add(PieEntry(amount.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "Spending by Category")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.isRotationEnabled = true
        pieChart.centerText = "Spending"
        pieChart.setCenterTextSize(18f)
        pieChart.animateY(1000, Easing.EaseInOutQuad)
        pieChart.invalidate() // Refresh the chart
    }

    private fun updateSummaryUI(
        totalSpending: Double,
        totalBudget: Double,
        mostFrequentCategories: List<String>
    ) {
        txtTotalSpending.text = "Total Spending: R${String.format("%.2f", totalSpending)}"
        txtBudgetUtilization.text = "Budget Utilization: R${String.format("%.2f", totalSpending)} / R${String.format("%.2f", totalBudget)}"
        txtMostFrequentCategories.text = "Most Frequent Categories: ${mostFrequentCategories.joinToString(", ")}"
    }

    private fun fetchFavoriteCategories(userId: Int) {
        RetrofitClient.instance.getFavouriteCategoriesByUser(userId)
            .enqueue(object : Callback<List<CategoryClass>> {
                override fun onResponse(
                    call: Call<List<CategoryClass>>,
                    response: Response<List<CategoryClass>>
                ) {
                    if (response.isSuccessful) {
                        val favoriteCategories = response.body() ?: emptyList()
                        displayFavoriteCategories(favoriteCategories)
                    } else {
                        Toast.makeText(
                            this@DashboardActivity,
                            "Failed to load favorite categories",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<CategoryClass>>, t: Throwable) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun fetchAllCategoriesAndFilterFavorites(userId: Int) {
        RetrofitClient.instance.getCategoriesByUser(userId)
            .enqueue(object : Callback<List<CategoryClass>> {
                override fun onResponse(call: Call<List<CategoryClass>>, response: Response<List<CategoryClass>>) {
                    if (response.isSuccessful) {
                        val allCategories = response.body() ?: emptyList()
                        val favoriteCategories = allCategories.filter { it.isFavourite == true }
                        displayFavoriteCategories(favoriteCategories)
                    } else {
                        Toast.makeText(this@DashboardActivity, "No categories", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<CategoryClass>>, t: Throwable) {
                    Toast.makeText(this@DashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }





}

