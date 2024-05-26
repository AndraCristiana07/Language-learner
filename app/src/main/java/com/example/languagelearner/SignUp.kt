package com.example.languagelearner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.languagelearner.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var name: EditText
    private lateinit var password: EditText
    private lateinit var repassword: EditText
    private lateinit var mail: EditText
    private lateinit var phone: EditText
    private lateinit var register:Button
    private lateinit var databaseHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        databaseHelper = DatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameEditText = findViewById<EditText>(R.id.editTextText)
        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword2)
        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextTextPassword3)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        val registerButton = findViewById<Button>(R.id.register)


        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val phone = phoneEditText.text.toString()

            if (password == confirmPassword) {
                val user = User(name = name, email = email, password = password, phone = phone)
                val success = databaseHelper.insertUser(user)
                if (success != -1L) {
                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()

            }

        }
        binding.button7.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
}