package com.example.languagelearner.auth

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val user: User?
)
