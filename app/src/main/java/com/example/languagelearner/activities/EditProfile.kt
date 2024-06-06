package com.example.languagelearner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.languagelearner.R
import com.example.languagelearner.auth.DefaultResponse
import com.example.languagelearner.auth.ProfileUpdateRequest
import com.example.languagelearner.auth.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfile : AppCompatActivity() {
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var saveButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = this.getSharedPreferences("nameForUser", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "")
        val userEmail = sharedPreferences.getString("userEmail", "")
        val userPhone = sharedPreferences.getString("userPhone", "")

        editName = findViewById(R.id.edit_name)
        editEmail = findViewById(R.id.edit_email)
        editPhone = findViewById(R.id.edit_phone)
        saveButton = findViewById(R.id.button_save)

        editName.setText(userName)
        editEmail.setText(userEmail)
        editPhone.setText(userPhone)

        saveButton.setOnClickListener {
            val name = editName.text.toString()
            val email = editEmail.text.toString()
            val phone = editPhone.text.toString()
            var alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage("Are you sure ypu want to change?")
            alertDialogBuilder.setNegativeButton("Cancel") {_,_->

            }
            alertDialogBuilder.setPositiveButton("Yes"){_,_->
                updateProfile(name, email, phone)
            }
            val alertBox = alertDialogBuilder.create()
            alertBox.show()
        }


    }


    private fun updateProfile(name: String, email: String, phone: String) {
        val profileUpdateRequest = ProfileUpdateRequest(name, email, phone)
        RetrofitInstance.service.changeProfile(profileUpdateRequest)
            .enqueue(object: Callback<DefaultResponse> {

                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.isSuccessful){
                        val defaultResponse = response.body()
                        if(defaultResponse != null && !defaultResponse.error){
                            Toast.makeText(this@EditProfile, "Changed successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@EditProfile, LessonsPage::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@EditProfile, "Change failed: ${defaultResponse?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val error = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(this@EditProfile, "Change failed: $error?", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(this@EditProfile, "Change failed: ${t.message}", Toast.LENGTH_SHORT).show()

                }

            })
    }
}