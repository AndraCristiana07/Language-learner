package com.example.languagelearner.auth

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name")
    var name: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    var password: String,
//    @SerializedName("confirmPassword")
//    var confirmPassword: String,
    @SerializedName("phone")
    var phone: String

)
