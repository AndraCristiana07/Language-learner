package com.example.languagelearner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.languagelearner.R
import com.example.languagelearner.auth.DefaultResponse
import com.example.languagelearner.auth.RegisterRequest
import com.example.languagelearner.auth.RetrofitInstance
import com.example.languagelearner.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
//    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        sharedPreferences = this.getSharedPreferences(R.id.editTextName.toString(), Context.MODE_PRIVATE)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        sharedPreferences = getSharedPreferences("nameForUser", Context.MODE_PRIVATE)

        binding.button7.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        val nameEditText = binding.editTextName
        val emailEditText = binding.editTextEmailAddress2
        val passwordEditText = binding.editTextPassword2
        val confirmPasswordEditText = binding.editTextPassword3
        val phoneEditText = binding.editTextPhone

        binding.register.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!email.contains("@")) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.length != 10) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUpUser(name, email, password, phone)
        }
    }

    private fun signUpUser(name: String, email: String, password: String, phone: String) {
        val registerRequest = RegisterRequest(name, email, password, phone)

        RetrofitInstance.service.registerUser(registerRequest)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        val defaultResponse = response.body()
                        if (defaultResponse != null && !defaultResponse.error) {

                            Toast.makeText(this@SignUp, "Registration successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignUp, Login::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@SignUp, "Registration1 failed: ${defaultResponse?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(this@SignUp, "Registration2 failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(this@SignUp, "Registration3 failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}


//        val nameEditText = findViewById<EditText>(R.id.editTextText)
//        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress2)
//        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword2)
//        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextTextPassword3)
//        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
//        val registerButton = findViewById<Button>(R.id.register)



//
//        registerButton.setOnClickListener {
//            val name = nameEditText.text.toString()
//            val email = emailEditText.text.toString()
//            val password = passwordEditText.text.toString()
//            val confirmPassword = confirmPasswordEditText.text.toString()
//            val phone = phoneEditText.text.toString()
//
//            if (password == confirmPassword) {
//                val user = User(name = name, email = email, password = password, phone = phone)
//                val success = databaseHelper.insertUser(user)
//                if (success != -1L) {
//                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_LONG).show()
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
//                } else {
//                    Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG).show()
//                }
//
//            } else {
//                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
//
//            }
//
//        }
//        binding.button7.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }



