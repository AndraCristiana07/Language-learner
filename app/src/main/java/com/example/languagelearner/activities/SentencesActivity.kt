package com.example.languagelearner.activities

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.isEmpty
import androidx.core.view.setPadding
import com.example.languagelearner.R
import com.example.languagelearner.Sentence
import com.example.languagelearner.auth.RetrofitInstance
import com.example.languagelearner.questions.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SentencesActivity : AppCompatActivity() {

    private lateinit var infoView: TextView
    private lateinit var sentenceQuestion: TextView
//    private lateinit var sentenceAnswer: EditText
    private lateinit var shuffleWordsGridLayout: GridLayout
    private lateinit var sentenceAnswerContainer: LinearLayout
    private lateinit var nextButton: Button
    private lateinit var quitButton: Button
    private var currentSentenceIndex = 0
    private lateinit var sentences: List<Sentence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sentences)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        infoView = findViewById(R.id.info_view)
        sentenceQuestion = findViewById(R.id.sentence_question)
        shuffleWordsGridLayout = findViewById(R.id.shuffled_words_grid_layout)
//        sentenceAnswer = findViewById(R.id.sentence_answer)
        nextButton = findViewById(R.id.button_next_sentence)
        quitButton = findViewById(R.id.button_quit_sentence)
        sentenceAnswerContainer = findViewById(R.id.sentence_answer_container)

        fetchSentences()

        nextButton.setOnClickListener {

            if(currentSentenceIndex < sentences.size - 1){
                if(sentenceAnswerContainer.childCount == 0){
                    Toast.makeText(this, "Input something first", Toast.LENGTH_SHORT).show()
                } else {
                    val sentence = sentences[currentSentenceIndex].sentenceTranslated
                    Log.d("Translated correct", sentence)
                    val userAnswer = StringBuilder()
                    for (i in 0..<sentenceAnswerContainer.childCount){
                        val wordView = sentenceAnswerContainer.getChildAt(i) as TextView
                        userAnswer.append(wordView.text).append(" ")

                    }
                    val trimmedSentence = userAnswer.toString().trim()

                    Log.d("user answer", userAnswer.toString())
                    if(trimmedSentence == sentence){
                        Toast.makeText(this, "Correct answer", Toast.LENGTH_SHORT).show()
                        currentSentenceIndex++
                        displaySentence(sentences[currentSentenceIndex])

                        sentenceAnswerContainer.removeAllViews()
                    } else {
                        Toast.makeText(this, "Wrong answer, try again",Toast.LENGTH_SHORT).show()
                        sentenceAnswerContainer.removeAllViews()
                    }
                }


            } else {
                Toast.makeText(this, "Lesson finished!", Toast.LENGTH_SHORT).show()
            }
        }

        quitButton.setOnClickListener {
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

        sentenceAnswerContainer.setOnDragListener { v, event ->
            when(event.action){
                DragEvent.ACTION_DRAG_STARTED -> {
                    //can it accept the dragged data?
                    if(event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        (v as? LinearLayout)?.setBackgroundColor(Color.CYAN)
                        v.invalidate()
                        true
                    } else {
                        false
                    }

                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    (v as? LinearLayout)?.setBackgroundColor(Color.GREEN)
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    (v as? LinearLayout)?.setBackgroundColor(Color.CYAN)
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    val dragData = item.text
                    val droppedTextView = TextView(this).apply {
                        text = dragData
                        textSize = 16f
                        setPadding(16, 16, 16, 16)
                        setBackgroundColor(Color.LTGRAY)
                        tag = dragData
                    }
                    (v as LinearLayout).addView(droppedTextView)
                    (v as? LinearLayout)?.setBackgroundColor(Color.TRANSPARENT)
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    (v as? LinearLayout)?.setBackgroundColor(Color.TRANSPARENT)
                    v.invalidate()
                    when(event.result) {
                        true ->
                            Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG)
                        else ->
                            Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG)
                    }.show()
                    true
                }

                else -> {
                    Log.e("DragDrop Example", "Unknown action type received by View.OnDragListener.")
                    false
                }
            }
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
                            displaySentence(sentences[currentSentenceIndex])
                        } else {
                            Toast.makeText(this@SentencesActivity, "Failed to fetch sentences", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<List<Sentence>>, t: Throwable) {
                    Toast.makeText(this@SentencesActivity, "Failed to fetch sentences", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displaySentence(sentence: Sentence){
        sentenceQuestion.text = sentence.sentenceQuestion

        val words = sentence.sentenceTranslated.split(" ").shuffled()
        shuffleWordsGridLayout.removeAllViews()
        words.forEach{ word ->
            val textView = TextView(this).apply {
                text = word
                tag= word
                setPadding(8,8,8,8)
                setOnLongClickListener { v ->
                    val item = ClipData.Item(v.tag as? CharSequence)
                    val dragData = ClipData(v.tag as? CharSequence, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
                    val myShadow = View.DragShadowBuilder(v)
                    v.startDragAndDrop(dragData, myShadow, null, 0)
                    true
                }
            }
            shuffleWordsGridLayout.addView(textView)
        }


    }


}