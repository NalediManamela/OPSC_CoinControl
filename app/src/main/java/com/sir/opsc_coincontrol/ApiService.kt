// ApiService.kt
package com.sir.opsc_coincontrol

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/categories")
    fun postCategory(@Body category: CategoryClass): Call<CategoryClass>


    @GET("api/categories")
    fun getCategories(): Call<List<CategoryClass>>

    @GET("api/categories/{id}")
    fun getCategoryById(@Path("id") id: Int): Call<CategoryClass>

    @GET("api/categories")
    fun getCategoriesByUser(@Query("userID") userId: Int): Call<List<CategoryClass>>

    @POST("api/debitorders")
    fun postDebitOrders(@Body debitOrder: DebitOrderClass): Call<DebitOrderClass>

    @GET("api/debitorders")
    fun getDebit(@Query("userID") userId: Int): Call<List<DebitOrderClass>>

    @GET("api/transaction/category/{categoryId}")
    fun getTransactionsByCategory(@Path("categoryId") categoryId: Int): Call<List<TransactionsClass>>

    @POST("api/transaction")
    fun postTransaction(@Body transaction: TransactionsClass): Call<TransactionsClass>

    @GET("api/debitorders/total")
    fun getTotalDebitOrders(): Call<Double>

    @GET("api/DebitOrders/due/current-month")
    fun getTotalDueForCurrentMonth(): Call<Double>

    @POST("api/auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("api/auth/verify-token")
    fun verifyToken(@Body idToken: String): Call<User>

    @POST("api/DebitOrders")
    fun createDebitOrder(@Body debitOrder: DebitOrderClass): Call<DebitOrderClass>

    @POST("api/auth/register")
    fun registerUser(@Body registerRequest: RegisterClass): Call<Void>

    @DELETE("api/categories/delete-category/{id}")
    fun deleteCategory(@Path("id") categoryId: Int): Call<Void>

    @DELETE("api/transaction/{id}")
    fun deleteTransaction(@Path("id") transactionId: Int): Call<Unit>
}
