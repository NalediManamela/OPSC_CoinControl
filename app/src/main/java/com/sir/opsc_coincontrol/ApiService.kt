// ApiService.kt
package com.sir.opsc_coincontrol

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Define the POST request to create a new category
    @POST("api/categories")
    fun postCategory(@Body category: CategoryClass): Call<CategoryClass>

    // Define a GET request to fetch all categories for the user
    @GET("api/categories")
    fun getCategories(): Call<List<CategoryClass>>

    // Define a GET request to fetch a single category by ID
    @GET("api/categories/{id}")
    fun getCategoryById(@Path("id") id: Int): Call<CategoryClass>

    @GET("api/categories")
    fun getCategoriesByUser(@Query("userID") userId: Int): Call<List<CategoryClass>>

    @POST("api/debitorders")
    fun postDebitOrders(@Body debitOrder: DebitOrderClass): Call<DebitOrderClass>

    @GET("api/transaction/category/{categoryId}")
    fun getTransactionsByCategory(@Path("categoryId") categoryId: Int): Call<List<TransactionsClass>>

    @POST("api/transaction")
    fun postTransaction(@Body transaction: TransactionsClass): Call<TransactionsClass>



}
