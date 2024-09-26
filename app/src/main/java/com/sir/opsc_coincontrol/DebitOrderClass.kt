package com.sir.opsc_coincontrol

class DebitOrderClass (

    var debit_OrderID: Int = 0, // ID should be 0 or omitted since it's auto-generated by the server
    var debit_Name: String,
    var userID: Int = 0, // This can be 0 or omitted, as the backend will assign it
    var debit_Amount: Double,
    var total_Amount: Double = 0.0

)