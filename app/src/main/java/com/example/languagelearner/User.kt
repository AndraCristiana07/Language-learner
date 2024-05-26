package com.example.languagelearner

import android.provider.ContactsContract.CommonDataKinds.Email

data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)
