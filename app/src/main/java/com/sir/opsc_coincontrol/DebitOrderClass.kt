package com.sir.opsc_coincontrol

import java.sql.Date

data class DebitOrderClass(
    val debit_OrderID: Int = 0,
    val debit_Date: String,
    val due_Date: String,
    val debit_Name: String,
    val debit_Amount: Double,
    val userID: Int,
    val notifications: List<String> = emptyList(),
    val user: Any? = null
)
