package com.example.languagelearner.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.languagelearner.R
import com.example.languagelearner.Sentence
import com.example.languagelearner.auth.RetrofitInstance
import com.example.languagelearner.questions.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuickQuizActivity : AppCompatActivity() {

    private lateinit var scoreView: TextView
    private lateinit var quizQuestion: TextView
    private lateinit var quizAnswer: EditText
    private lateinit var nextButton: Button
    private lateinit var quitButton: Button
    private var score = 0
    private var currentQuestionIndex = 0
    private var currentSentenceIndex = 0
    private lateinit var questions: List<Question>
    private lateinit var sentences: List<Sentence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quick_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scoreView = findViewById(R.id.score_view)
        quizQuestion = findViewById(R.id.quiz_question)
        quizAnswer = findViewById(R.id.quiz_answer)
        nextButton = findViewById(R.id.button_next_question)
        quitButton = findViewById(R.id.button_quit_quiz)

        fetchRandomQuestions()
        fetchRandomSentences()


        nextButton.setOnClickListener {
            checkAnswer()
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++

                displayQuestion(questions[currentQuestionIndex])
            } else if (currentQuestionIndex >= questions.size - 1 && currentSentenceIndex < sentences.size - 1) {
                currentSentenceIndex++

                displaySentence(sentences[currentSentenceIndex])
            } else {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setMessage("Quiz finished! Your final score: $score")
                alertDialogBuilder.setPositiveButton("Go back to lessons"){_,_ ->
//                    val intent = Intent(this, LessonsPage::class.java)
//                    startActivity(intent)
                    finish()

                }
                val alertDialogBox = alertDialogBuilder.create()
                alertDialogBox.show()
//                Toast.makeText(this, "Quiz finished! Your final score: $score", Toast.LENGTH_LONG).show()
                nextButton.isEnabled = false
            }
        }

        quitButton.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage("Do you want to quit the quiz?")
            alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                finish()
            }
            alertDialogBuilder.setNegativeButton("No") { _, _ ->
                Toast.makeText(this, "Clicked no", Toast.LENGTH_SHORT).show()
            }
            val alertDialogBox = alertDialogBuilder.create()
            alertDialogBox.show()
        }
    }

    private fun fetchRandomQuestions() {
        RetrofitInstance.service.getRandomQuestions().enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                if (response.isSuccessful) {
                    questions = response.body()!!
                    if (questions.isNotEmpty()) {
                        displayQuestion(questions[currentQuestionIndex])
                    } else {
                        Toast.makeText(this@QuickQuizActivity, "No questions available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@QuickQuizActivity, "Failed to load questions", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                Toast.makeText(this@QuickQuizActivity, "Failed to load questions", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchRandomSentences() {
        RetrofitInstance.service.getRandomSentences().enqueue(object : Callback<List<Sentence>> {
            override fun onResponse(call: Call<List<Sentence>>, response: Response<List<Sentence>>) {
                if (response.isSuccessful) {
                    sentences = response.body()!!

                    if (sentences.isNotEmpty()) {
//                        displaySentence(sentences[currentSentenceIndex])
                            Log.d("Sentencess", " Loaded sentences")
                    } else {
                        Toast.makeText(this@QuickQuizActivity, "No sentences available", Toast.LENGTH_SHORT).show()
                        Log.e("Sentencess", "No sentences available")
                    }
                } else {
                    Toast.makeText(this@QuickQuizActivity, "Failed to load sentences", Toast.LENGTH_SHORT).show()
                    Log.e("Sentencess", "Failed to load sentences")
                }
            }

            override fun onFailure(call: Call<List<Sentence>>, t: Throwable) {
                Toast.makeText(this@QuickQuizActivity, "Failed to load sentences", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayQuestion(question: Question) {
        quizQuestion.text = question.questionLabel
        quizAnswer.text.clear()
    }

    private fun displaySentence(sentence: Sentence) {
        quizQuestion.text = sentence.sentenceQuestion
        quizAnswer.text.clear()
    }

    @SuppressLint("SetTextI18n")
    private fun checkAnswer() {
        var correctAnswer = ""
        if (currentQuestionIndex < questions.size - 1) {
            val question = questions[currentQuestionIndex]
             correctAnswer = question.answers.find { it.isCorrect }?.answerLabel ?: ""
        } else if (currentQuestionIndex >= questions.size - 1 && currentSentenceIndex < sentences.size  ) {
            val sentence = sentences[currentSentenceIndex]
             correctAnswer = sentence.sentenceTranslated
            Log.d("sentence idx: ", currentSentenceIndex.toString())
            Log.d("correct", correctAnswer)
        }
        if (quizAnswer.text.toString().trim().equals(correctAnswer, ignoreCase = true)) {
            score += 10
            Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect answer :(", Toast.LENGTH_SHORT).show()
            Log.d("ans", correctAnswer)
            Log.d("user ans", quizAnswer.text.toString().trim())
        }
//            currentSentenceIndex++


        scoreView.text = "Score: $score"
        quizAnswer.text.clear()
    }


    @SuppressLint("SetTextI18n")
    private fun checkAnswer2() {
        var correctAnswer = ""

        if (currentQuestionIndex < questions.size - 1) {
            val question = questions[currentQuestionIndex]

            correctAnswer = question.answers.find { it.isCorrect }?.answerLabel ?: ""
//            currentQuestionIndex++
//            if (currentQuestionIndex >= questions.size) {
//                fetchRandomSentences()
//            }
        } else if (currentQuestionIndex >= questions.size - 1 && currentSentenceIndex < sentences.size - 1) {
            fetchRandomSentences()
            val sentence = sentences[currentSentenceIndex]

            correctAnswer = sentence.sentenceTranslated
        }
        if (quizAnswer.text.toString().trim().equals(correctAnswer, ignoreCase = true)) {
            score += 10
            Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect answer :(", Toast.LENGTH_SHORT).show()
        }
//            currentSentenceIndex++


        scoreView.text = "Score: $score"
        quizAnswer.text.clear()
    }
}
