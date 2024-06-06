package com.example.languagelearner.auth

data class ProfileUpdateRequest(
    val name: String,
    val email: String,
    val phone: String
)
