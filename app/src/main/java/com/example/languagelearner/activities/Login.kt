package com.example.languagelearner.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.languagelearner.auth.LoginRequest
import com.example.languagelearner.auth.LoginResponse
import com.example.languagelearner.auth.RetrofitInstance
import com.example.languagelearner.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                        val userName = response.body()?.user?.name ?: "User"
                        Log.d("LoginActivity", "Retrieved userName: $userName")
                        Toast.makeText(this@Login, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Login, LessonsPage::class.java)
                        intent.putExtra("USER_NAME", userName)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Login, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@Login, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
