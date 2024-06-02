package com.example.languagelearner.activities

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationSet
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.languagelearner.R
import com.example.languagelearner.ReviewFragment
import com.example.languagelearner.auth.RetrofitInstance
import com.example.languagelearner.questions.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlashCardsActivity : AppCompatActivity() {

     lateinit var front_anim:AnimatorSet
     lateinit var back_anim:AnimatorSet
     private lateinit var front: TextView
     private lateinit var back: TextView
     var isFront = true
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_flash_cards)



        val scale = applicationContext.resources.displayMetrics.density

         front = findViewById<TextView>(R.id.card_front) as TextView
         back = findViewById<TextView>(R.id.card_back) as TextView
        val flip = findViewById<TextView>(R.id.flip_btn) as Button
        val nextButton = findViewById<Button>(R.id.button_next_flashcard)
        val prevButton = findViewById<Button>(R.id.button_prev_flashcard)
        val backButton = findViewById<Button>(R.id.button_go_back)
        fetchCardQuestions()

        front.cameraDistance = 8000 * scale
        back.cameraDistance = 8000 * scale

        front_anim = AnimatorInflater.loadAnimator(applicationContext, R.animator.front_animator) as AnimatorSet
        back_anim = AnimatorInflater.loadAnimator(applicationContext, R.animator.back_animator) as AnimatorSet

        backButton.setOnClickListener {
//            val intent = Intent(this, LessonsPage::class.java )
//            startActivity(intent)
            finish()
        }
        flip.setOnClickListener{
            if(isFront){
                front_anim.setTarget(front)
                back_anim.setTarget(back)
                front_anim.start()
                back_anim.start()
                isFront = false
            } else {
                front_anim.setTarget(back)
                back_anim.setTarget(front)
                back_anim.start()
                front_anim.start()
                isFront = true
            }

        }

        prevButton.setOnClickListener {
            if(currentQuestionIndex > 0) {
                currentQuestionIndex--
                if(!isFront){
                    front_anim.setTarget(back)
                    back_anim.setTarget(front)
                    back_anim.start()
                    front_anim.start()
                    isFront = true
                }
                displayCard(questions[currentQuestionIndex])
            }
        }
        nextButton.setOnClickListener {
            if(currentQuestionIndex <  questions.size - 1){
                currentQuestionIndex++
                if(!isFront){
                    front_anim.setTarget(back)
                    back_anim.setTarget(front)
                    back_anim.start()
                    front_anim.start()
                    isFront = true
                }
                displayCard(questions[currentQuestionIndex])
            }
        }
    }

    private fun fetchCardQuestions() {
        RetrofitInstance.service.getAllQuestions()
            .enqueue(object: Callback<List<Question>> {
                override fun onResponse(
                    call: Call<List<Question>>,
                    response: Response<List<Question>>
                ) {
                    if(response.isSuccessful) {
                        questions = response.body()!!
                        if(questions.isNotEmpty()){

                            displayCard(questions[currentQuestionIndex])
                        }

                    } else {
                        Toast.makeText(this@FlashCardsActivity, "Failed to fetch questions", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                    Toast.makeText(this@FlashCardsActivity, "Failed to fetch questions", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displayCard(question: Question) {
        front.text = question.questionLabel
        question.answers.forEach {answer ->
            if(answer.isCorrect){
                back.text = answer.answerLabel
            }
        }
    }
}
