// models/TransactionClass.kt
package com.sir.opsc_coincontrol

data class TransactionsClass(
    val transaction_ID: Int,
    val date: String?, // Using String since JSON date comes as a string, you can parse it later if needed
    val transaction_Name: String?,
    val transaction_Amount: Double?,
    val cat_ID: Int?
)
