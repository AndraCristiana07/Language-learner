package com.example.languagelearner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Base64.decode
import android.util.Log
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.languagelearner.R
import com.example.languagelearner.auth.RetrofitInstance
import com.example.languagelearner.databinding.ActivityQuizBinding
import com.example.languagelearner.questions.Answer
import com.example.languagelearner.questions.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private lateinit var questionTextView: TextView
    private lateinit var radioGroup1: RadioGroup
    private lateinit var radioGroup2: RadioGroup
    private lateinit var imageViews: List<ImageView>
    private lateinit var radioButtons: List<RadioButton>


    private lateinit var sharedPreferences: SharedPreferences

    //    private var categoryName = "Animals"
    private var categoryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        categoryName = intent.getStringExtra("categoryName").toString()
        Log.d("categ", categoryName)
        questionTextView = findViewById(R.id.question)
        radioGroup1 = findViewById(R.id.radioGroup1)
        radioGroup2 = findViewById(R.id.radioGroup2)
        sharedPreferences = getSharedPreferences("quizProgress", Context.MODE_PRIVATE)
        currentQuestionIndex = sharedPreferences.getInt("$categoryName-index",0)



        imageViews = listOf(
            findViewById(R.id.image_view1),
            findViewById(R.id.image_view2),
            findViewById(R.id.image_view3),
            findViewById(R.id.image_view4)
        )

        radioButtons = listOf(
            findViewById(R.id.radio_button1),
            findViewById(R.id.radio_button2),
            findViewById(R.id.radio_button3),
            findViewById(R.id.radio_button4)
        )

        radioListener()

        fetchQuestions(categoryName)

        binding.buttonNext.setOnClickListener {
            val selectedRadioButtonId1 = binding.radioGroup1.checkedRadioButtonId
            val selectedRadioButtonId2 = binding.radioGroup2.checkedRadioButtonId

            if (selectedRadioButtonId1 != -1 || selectedRadioButtonId2 != -1) {
                val selectedRadioButton: RadioButton? =
                    if (selectedRadioButtonId1 != -1) {
                        findViewById(selectedRadioButtonId1)
                    } else {
                        findViewById(selectedRadioButtonId2)
                    }
                val selectedAnswer = selectedRadioButton?.tag as Answer

                if (selectedAnswer.isCorrect) {
                    Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()


                    if (currentQuestionIndex < questions.size - 1) {
                        currentQuestionIndex++
                        saveProgress()
                        displayQuestion(questions[currentQuestionIndex])
                    } else {
                        clearProgress()
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.setMessage(" $categoryName quiz completed!")
                        alertDialogBuilder.setPositiveButton("Go back to lessons"){_,_ ->
                            val intent = Intent(this, LessonsPage::class.java)
                            startActivity(intent)
                            finish()

                        }
                        val alertDialogBox = alertDialogBuilder.create()
                        alertDialogBox.show()

                    }
                } else {
                    Toast.makeText(this, "Incorrect! Try again!", Toast.LENGTH_SHORT).show()

                }

            } else {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
            }
        }



        binding.buttonQuit.setOnClickListener {
            saveProgress()
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage("Do you want to quit the lesson?")
            alertDialogBuilder.setPositiveButton("Yes"){_,_ ->
                val intent = Intent(this, LessonsPage::class.java)
                startActivity(intent)
                finish()
            }
            alertDialogBuilder.setNegativeButton("No"){_,_->
                Toast.makeText(this,"Clicked no", Toast.LENGTH_SHORT).show()
            }
            val alertDialogBox = alertDialogBuilder.create()
            alertDialogBox.show()

        }
    }

    private fun fetchQuestions(categoryName: String) {
        RetrofitInstance.service.getQuestionsByCategory(categoryName)
            .enqueue(object : Callback<List<Question>> {
                override fun onResponse(
                    call: Call<List<Question>>,
                    response: Response<List<Question>>
                ) {
                    if (response.isSuccessful) {
                        questions = response.body()!!
                        if (questions.isNotEmpty()) {
                            sharedPreferences.edit().putInt("$categoryName-totalQuestions", questions.size).apply()
                            displayQuestion(questions[currentQuestionIndex])

                        }
                    } else {
                        Log.e("Fetch", "Failed to fetch questions")
                        Toast.makeText(
                            this@QuizActivity,
                            "Failed to fetch questions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                    Toast.makeText(
                        this@QuizActivity,
                        "Failed to fetch questions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

    }

    private fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val imageBytes = decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: IllegalArgumentException) {
            Log.e("decode error", "Invalid base64 str")
            null
        }
    }

    private fun displayQuestion(question: Question){
        questionTextView.text = question.questionLabel
        radioGroup1.clearCheck()
        radioGroup2.clearCheck()
        for ((i, answer) in question.answers.withIndex()) {
            val decodeImage = stringToBitmap(answer.image)
            if (decodeImage != null) {
                imageViews[i].setImageBitmap(decodeImage)
            } else {
                Log.e("ImageDecodeError", "Failed to decode image for ${answer.answerLabel}")
            }
            radioButtons[i].text = answer.answerLabel;
            radioButtons[i].tag = answer;
        }
    }
    private val listener1: RadioGroup.OnCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        if (checkedId != -1) {
            radioGroup2.setOnCheckedChangeListener(null)
            radioGroup2.clearCheck()
            radioGroup2.setOnCheckedChangeListener(listener2)
        }
    }

    private val listener2: RadioGroup.OnCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        if (checkedId != -1) {
            radioGroup1.setOnCheckedChangeListener(null)
            radioGroup1.clearCheck()
            radioGroup1.setOnCheckedChangeListener(listener1)
        }
    }
    private fun radioListener(){
        radioGroup1.clearCheck();
        radioGroup2.clearCheck();
        radioGroup1.setOnCheckedChangeListener(listener1);
        radioGroup2.setOnCheckedChangeListener(listener2);
    }

    private fun saveProgress(){
        with(sharedPreferences.edit()) {
            putInt("$categoryName-index", currentQuestionIndex)
            apply()
        }
    }

    private fun clearProgress(){
        with(sharedPreferences.edit()) {
            remove("$categoryName-index")
            remove("$categoryName-totalQuestions")
            apply()
        }
    }

}