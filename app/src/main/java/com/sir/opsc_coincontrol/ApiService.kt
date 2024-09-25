// ApiService.kt
package com.sir.opsc_coincontrol

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
}
