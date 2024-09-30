package com.sir.opsc_coincontrol

import java.sql.Date

data class DebitOrderClass(
    val debit_OrderID: Int = 0,  // New debit order ID is 0
    val debit_Date: String,      // Should be in yyyy-MM-dd format
    val due_Date: String,        // Should be in ISO 8601 format
    val debit_Name: String,
    val debit_Amount: Double,
    val userID: Int,
    val notifications: List<String> = emptyList(),  // Default to an empty list
    val user: Any? = null                           // Nullable user field
)
