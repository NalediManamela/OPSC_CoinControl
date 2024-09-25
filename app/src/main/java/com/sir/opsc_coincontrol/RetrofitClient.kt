package com.sir.opsc_coincontrol

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://prgopscapi.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api:ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
