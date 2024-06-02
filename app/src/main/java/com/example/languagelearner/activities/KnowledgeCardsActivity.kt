package com.example.languagelearner.activities

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.languagelearner.R
import com.example.languagelearner.auth.RetrofitInstance
import com.example.languagelearner.questions.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KnowledgeCardsActivity : AppCompatActivity() {

    private lateinit var categoryNameView : TextView
    private lateinit var translationListView : ListView
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private var currentCategoryIndex = 0
    private lateinit var categories: List<String>
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_knowledge_cards)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
       categoryNameView = findViewById(R.id.category_name)
        translationListView = findViewById(R.id.translation_list)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        backButton = findViewById(R.id.button_back)

        fetchCategories()

        backButton.setOnClickListener {
            finish()
        }
        nextButton.setOnClickListener {
            if(currentCategoryIndex < categories.size - 1){
                currentCategoryIndex++
                fetchQuestions(categories[currentCategoryIndex])
            } else {
                Toast.makeText(this, "No more categories", Toast.LENGTH_SHORT).show()

            }
        }

        prevButton.setOnClickListener {
            if(currentCategoryIndex > 0){
                currentCategoryIndex--
                fetchQuestions(categories[currentCategoryIndex])
            } else  {
                Toast.makeText(this, "No more categories", Toast.LENGTH_SHORT).show()
            }
        }





    }

    private fun fetchCategories(){
        RetrofitInstance.service.getCategories()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    if(response.isSuccessful){
                        categories = response.body()!!
                        if(categories.isNotEmpty()){
                            fetchQuestions(categories[currentCategoryIndex])
                        }
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Log.e("KnowledgeCardActivity", "Failed fetching categories")
                }
            })
    }

    private fun fetchQuestions(categoryName: String) {
        RetrofitInstance.service.getQuestionsByCategory(categoryName).enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                if (response.isSuccessful) {
                    val questions = response.body()!!
                    displayQuestions(categoryName, questions)
                }
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                Log.e("KnowledgeCardActivity", "Failed fetching questions for category $categoryName")
            }
        })
    }

    private fun displayQuestions(categoryName: String, questions:List<Question>){
        categoryNameView.text = categoryName
        val translations = questions.map { question ->
            val englishWord = question.questionLabel.split("'")[1]
            val correctTranslation = question.answers.find { it.isCorrect }?.answerLabel

            "$englishWord = $correctTranslation"
        }

        val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, translations)
        translationListView.adapter = arrayAdapter

    }




}


