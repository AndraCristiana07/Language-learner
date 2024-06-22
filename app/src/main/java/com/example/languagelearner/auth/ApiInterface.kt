package com.example.languagelearner.auth

import com.example.languagelearner.ImageUpdateRequest
import com.example.languagelearner.Sentence
import com.example.languagelearner.questions.Question
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiInterface {
    @POST("/register")
      fun registerUser(@Body registerRequest: RegisterRequest): Call<DefaultResponse>

    @POST("/login")
      fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>


    @GET("/questions/all")
        fun getAllQuestions(): Call<List<Question>>

    @GET("questions/random")
        fun getRandomQuestions() : Call<List<Question>>

    @GET("/categories")
        fun getCategories() : Call<List<String>>

    @GET("/categories/{categoryName}/questions")
        fun getQuestionsByCategory(@Path("categoryName") categoryName: String): Call<List<Question>>

    @GET("/sentences")
        fun getSentences(): Call<List<Sentence>>

    @GET("sentences/random")
        fun getRandomSentences(): Call<List<Sentence>>

    @PUT("/profile")
        fun changeProfile(@Body profileUpdateRequest: ProfileUpdateRequest): Call<DefaultResponse>

    @PUT("/image")
        fun changeProfileImage(@Body imageUpdateRequest: ImageUpdateRequest): Call<DefaultResponse>
}