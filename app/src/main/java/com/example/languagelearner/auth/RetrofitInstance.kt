package com.example.languagelearner.auth

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    //input your url
    private const val BASE_URL = ""


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