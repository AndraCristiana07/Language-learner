package com.example.languagelearner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.LocaleList
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import kotlin.random.Random


class GrammarActivity : AppCompatActivity() {
    private var currentSentenceIndex = 0
    private lateinit var sentences: List<Sentence>
    private lateinit var translQuestion: TextView
    private lateinit var missingWordText: TextView
    private lateinit var answerText: EditText
    private lateinit var nextButton: Button
    private lateinit var quitButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private var correctAnswer: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_grammar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("grammarProgress", Context.MODE_PRIVATE)

        val redo = intent.getBooleanExtra("redo", false)
        if(redo){

        }
        currentSentenceIndex = sharedPreferences.getInt("grammar-index", 0)

        translQuestion = findViewById(R.id.transl_question)
        missingWordText = findViewById(R.id.missing_word_q)
        answerText = findViewById(R.id.answer_miss)
        nextButton = findViewById(R.id.button_next_missing)
        quitButton = findViewById(R.id.button_quit_missing)
        fetchSentences()
        answerText.setImeHintLocales(LocaleList(Locale("ro","RO")))

        nextButton.setOnClickListener {
            if(answerText.text.isNotEmpty()){
                val userAnswer = answerText.text
                val trimmedSentence = userAnswer.toString().trim()
                if (trimmedSentence.equals(correctAnswer, ignoreCase = true)){
                    Toast.makeText(this, "Correct answer", Toast.LENGTH_SHORT).show()
                    if(currentSentenceIndex < sentences.size -1){
                        currentSentenceIndex++
                        saveProgress()
                        displaySentence(sentences[currentSentenceIndex])

                    } else {
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.setMessage("Lesson finished!")
                        alertDialogBuilder.setPositiveButton("Go back") { _, _ ->
                            val intent = Intent(this, LessonsPage::class.java)
                            startActivity(intent)
                            finish()
                        }
                        val alertDialogBox = alertDialogBuilder.create()
                        alertDialogBox.show()
                        sharedPreferences.edit().putInt("grammar-index", sentences.size).apply()
                    }
                } else {
                    Toast.makeText(this, "Wrong answer, try again", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Input something first", Toast.LENGTH_SHORT).show()
            }
        }

        quitButton.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage("Do you want to quit the lesson?")
            alertDialogBuilder.setPositiveButton("Yes"){_,_ ->
                saveProgress()
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


    private fun fetchSentences(){
        RetrofitInstance.service.getSentences()
            .enqueue(object : Callback<List<Sentence>> {
                override fun onResponse(
                    call: Call<List<Sentence>>,
                    response: Response<List<Sentence>>
                ) {
                    if(response.isSuccessful){
                        sentences = response.body()!!
                        if(sentences.isNotEmpty()){
//                            Log.d("Senteneces: ", sentences.size.toString())
                            sharedPreferences.edit().putInt("grammar-totalGrammar", sentences.size).apply()
                            displaySentence(sentences[currentSentenceIndex])
                        } else {
                            Toast.makeText(this@GrammarActivity, "Failed to fetch sentences", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<List<Sentence>>, t: Throwable) {
                    Toast.makeText(this@GrammarActivity, "Failed to fetch sentences", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displaySentence(sentence: Sentence){
        translQuestion.text = sentence.sentenceQuestion
        val question = sentence.sentenceTranslated
        val words = question.split(" ")
        if (words.isNotEmpty()) {
            val randomIndex = Random.nextInt(words.size)
            correctAnswer = words[randomIndex]
            val missingSentence = words.toMutableList().apply {
                this[randomIndex] = "____"
            }.joinToString(" ")
            missingWordText.text = missingSentence
        }

    }

    private fun saveProgress(){
        with(sharedPreferences.edit()){
            putInt("grammar-index", currentSentenceIndex)
            apply()
        }
    }

    private fun clearProgress(){
        with(sharedPreferences.edit()){
            remove("grammar-index")
            remove("grammar-totalGrammar")
            apply()
        }
    }

    private fun resetProgress(){
        with(sharedPreferences.edit()){
            putInt("grammar-index",0)
            apply()
        }
        currentSentenceIndex = 0
    }

}