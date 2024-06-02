package com.example.languagelearner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.example.languagelearner.databinding.ActivityAnimalQuizBinding
import com.example.languagelearner.questions.Answer
import com.example.languagelearner.questions.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimalQuizBinding
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private lateinit var questionTextView: TextView
//    private lateinit var radioButton1 : RadioButton
//    private lateinit var radioButton2: RadioButton
//    private lateinit var radioButton3: RadioButton
//    private lateinit var radioButton4: RadioButton
    private lateinit var radioGroup: RadioGroup
    private lateinit var sharedPreferences: SharedPreferences

    //    private var score = 0
//    private var categoryName = "Animals"
private var categoryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_animal_quiz)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityAnimalQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        categoryName = "Animals"
        categoryName = intent.getStringExtra("categoryName").toString()
        Log.d("categ", categoryName)
        questionTextView = findViewById(R.id.question)
//        radioButton1 = findViewById(R.id.radioButton)
//        radioButton2 = findViewById(R.id.radioButton2)
//        radioButton3 = findViewById(R.id.radioButton3)
//        radioButton4 = findViewById(R.id.radioButton4)
        radioGroup = findViewById(R.id.radioGroup)
        sharedPreferences = getSharedPreferences("quizProgress", Context.MODE_PRIVATE)
        currentQuestionIndex = sharedPreferences.getInt("$categoryName-index",0)
        fetchQuestions(categoryName)

        binding.buttonNext.setOnClickListener {
            val selectedRadioButtonId = binding.radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedAnswer = selectedRadioButton.tag as Answer
                if (selectedAnswer.isCorrect) {
                    Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()


                    if (currentQuestionIndex < questions.size - 1) {
                        currentQuestionIndex++
                        saveProgress()
                        displayQuestion(questions[currentQuestionIndex])
                    } else {
                        clearProgress()
//                        Toast .makeText(this, "Quiz completed!", Toast.LENGTH_SHORT).show()
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

    private fun displayQuestion(question: Question) {
        questionTextView.text = question.questionLabel
        Log.d("AA", question.questionLabel)

        radioGroup.removeAllViews()
        question.answers.forEach { answer ->
            val radioButton = RadioButton(this).apply {
                text = answer.answerLabel
                tag = answer
            }
            radioGroup.addView(radioButton)

//            radioButton1 = RadioButton(this)
//            radioButton1.text = answer.answerLabel
//            radioGroup.addView(radioButton1)
//
//            radioButton2 = RadioButton(this)
//            radioButton2.text = answer.answerLabel
//            radioGroup.addView(radioButton2)
//
//
//            radioButton3 = RadioButton(this)
//            radioButton3.text = answer.answerLabel
//            radioGroup.addView(radioButton3)
//
//            radioButton4 = RadioButton(this)
//            radioButton4.text = answer.answerLabel
//            radioGroup.addView(radioButton4)
        }


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