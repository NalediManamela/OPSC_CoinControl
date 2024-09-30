// models/TransactionClass.kt
package com.sir.opsc_coincontrol

data class TransactionsClass(
    val transaction_ID: Int,
    val date: String?,
    val transaction_Name: String?,
    val transaction_Amount: Double?,
    val cat_ID: Int?
)
