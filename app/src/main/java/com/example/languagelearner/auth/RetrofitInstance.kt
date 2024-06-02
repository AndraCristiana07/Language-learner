package com.example.languagelearner.auth

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private const val BASE_URL = "http://[2a02:8084:d002:bc00:9339:328c:b442:9240]:3000"


    val retrofit: Retrofit by lazy {
            Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: ApiInterface by lazy {
     retrofit.create(ApiInterface::class.java)
    }
}