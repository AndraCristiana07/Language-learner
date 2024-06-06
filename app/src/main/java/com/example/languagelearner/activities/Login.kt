package com.example.languagelearner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.languagelearner.auth.LoginRequest
import com.example.languagelearner.auth.LoginResponse
import com.example.languagelearner.auth.RetrofitInstance
import com.example.languagelearner.auth.User
import com.example.languagelearner.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editTextEmailAddress.setText("andra@gmail.com")
        binding.editTextPassword.setText("andra123")

        sharedPreferences = getSharedPreferences("nameForUser", Context.MODE_PRIVATE)

        binding.login.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginUser(email, password)
        }

        binding.signup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
        RetrofitInstance.service.loginUser(loginRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.error == false) {

                        val user = response.body()?.user
                        user?.let {
                            saveUserDetails(it)
//                            Log.d("LoginActivity", "Retrieved userName: $userName")
                            Toast.makeText(this@Login, "Login successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Login, LessonsPage::class.java)
                            val userName = response.body()?.user?.name ?: "User"
                        intent.putExtra("USER_NAME", userName)
                            startActivity(intent)
                            finish()
                        }
//                        val userName = response.body()?.user?.name ?: "User"

                    } else {
                        Toast.makeText(this@Login, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@Login, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveUserDetails(user: User) {
        with(sharedPreferences.edit()) {
            putString("userName", user.name)
            putString("userEmail", user.email)
            putString("userPhone", user.phone)
            apply()
        }
    }
}
