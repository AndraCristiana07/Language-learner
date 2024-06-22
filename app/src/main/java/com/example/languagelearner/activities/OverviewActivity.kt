package com.example.languagelearner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.languagelearner.ProfileFragment
import com.example.languagelearner.R
import com.example.languagelearner.auth.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OverviewActivity : AppCompatActivity() {
    private lateinit var totalProgressBar: ProgressBar
    private lateinit var totalProgressTextView: TextView
    private lateinit var profileImage: ImageView
    private lateinit var profileUserName: TextView
    private lateinit var backButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private lateinit var sharedPreferences3: SharedPreferences
    private lateinit var sharedPreferences4: SharedPreferences
    private lateinit var sharedPreferences5: SharedPreferences

    private lateinit var categories: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_overview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        totalProgressBar = findViewById(R.id.total_progressBar)
        totalProgressTextView = findViewById(R.id.total_progress)
        profileImage = findViewById(R.id.profile_image2)
        profileUserName = findViewById(R.id.username2)
        backButton = findViewById(R.id.button_overview_back)

        sharedPreferences5 = this.getSharedPreferences("nameForUser", Context.MODE_PRIVATE)
        sharedPreferences = getSharedPreferences("quizProgress", Context.MODE_PRIVATE)
        sharedPreferences2 = getSharedPreferences("speechProgress", Context.MODE_PRIVATE)
        sharedPreferences3 = getSharedPreferences("sentenceProgress", Context.MODE_PRIVATE)
        sharedPreferences4 = getSharedPreferences("grammarProgress", Context.MODE_PRIVATE)

        fetchCategories()
        val userName = sharedPreferences5.getString("userName", "User")

        backButton.setOnClickListener {
            val intent = Intent(this, LessonsPage::class.java)
            startActivity(intent)
        }
        profileUserName.text = userName
        val userImage = sharedPreferences5.getString("userImage", "")
        val bitmapImage = userImage?.let { stringToBitmap(it) }
        if (bitmapImage != null){
            profileImage.setImageBitmap(bitmapImage)
        } else {
            profileImage.setImageResource(R.drawable.profile)
        }
    }

    private fun fetchCategories() {
        RetrofitInstance.service.getCategories().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    categories = response.body()!!
                    if (categories.isNotEmpty()) {
                        Log.d("Overview", "Categories fetched")
                        calcTotalProgress()
                    } else {
                        Log.e("Overview", "Failed fetching categories")
                    }
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("Overview", "Error fetching categories")
            }
        })
    }

    private fun calcTotalProgress() {
        var totalProgress = 0
        var totalPossible = 0

        for (category in categories) {
            val progressQuiz = sharedPreferences.getInt("$category-index", 0)
            val totalQuiz = sharedPreferences.getInt("$category-totalQuestions", 1)
            totalProgress += progressQuiz
            totalPossible += totalQuiz
        }

        val progressSpeech = sharedPreferences2.getInt("speech-index", 0)
        val totalSpeech = sharedPreferences2.getInt("speech-totalSpeech", 1)
        totalProgress += progressSpeech
        totalPossible += totalSpeech

        val progressSentence = sharedPreferences3.getInt("sentence-index", 0)
        val totalSentences = sharedPreferences3.getInt("sentence-totalSentences", 1)
        totalProgress += progressSentence
        totalPossible += totalSentences

        val progressGrammar = sharedPreferences4.getInt("grammar-index", 0)
        val totalGrammar = sharedPreferences4.getInt("grammar-totalGrammar", 1)
        totalProgress += progressGrammar
        totalPossible += totalGrammar

        val percentage = if (totalPossible > 0) {
            (totalProgress.toFloat() / totalPossible * 100).toInt()
        } else {
            0
        }

        totalProgressBar.progress = percentage
        totalProgressTextView.text = "$percentage %"
    }

    private fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val imageBytes = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: IllegalArgumentException) {
            Log.e("decode error", "Invalid base64 str")
            null
        }
    }
}
