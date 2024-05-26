package com.example.languagelearner

import retrofit2.Response
import retrofit2.http.POST

interface ApiInterface {
    @POST("/register")
    suspend fun registerUser(user:User): Response<User>

    @POST("/login")
    suspend fun loginUser(loginRequest: LoginRequest): Response<User>
}