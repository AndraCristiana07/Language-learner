package com.example.languagelearner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.languagelearner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



//        databaseHelper = DatabaseHelper(this)
//
//        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
//        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
//
//        binding.login.setOnClickListener {
//                val email = emailEditText.text.toString()
//                val password = passwordEditText.text.toString()
//                val user = User(name="", email = email, password = password, phone = "")
//                val userExists = databaseHelper.readUser(user)
//                if(userExists) {
//                    Toast.makeText(this, "User logged in successfully", Toast.LENGTH_LONG).show()
//                    val intent = Intent(this, LessonsPage::class.java)
//                    startActivity(intent)
//                } else {
//                    Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show()
//                }
//
//
//
//        }
//
//        binding.signup.setOnClickListener {
//            val intent = Intent(this, SignUp::class.java)
//            startActivity(intent)
//        }


    }
}