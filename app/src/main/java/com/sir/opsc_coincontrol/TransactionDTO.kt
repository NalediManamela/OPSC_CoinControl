package com.sir.opsc_coincontrol

data class TransactionDTO(
    val transaction_ID: Int,
    val date: String?, // Adjust type if necessary
    val transaction_Name: String?,
    val transaction_Amount: Double?,
    val cat_ID: Int?,
    val categoryName: String?
)

