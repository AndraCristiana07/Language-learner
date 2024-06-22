package com.example.languagelearner.activities

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat

import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.forEach
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
    private lateinit var shuffleWordsGridLayout: GridLayout
    private lateinit var sentenceAnswerContainer: LinearLayout
    private lateinit var nextButton: Button
    private lateinit var quitButton: Button
    private var currentSentenceIndex = 0
    private lateinit var sentences: List<Sentence>
    private lateinit var sharedPreferences: SharedPreferences


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
        nextButton = findViewById(R.id.button_next_sentence)
        quitButton = findViewById(R.id.button_quit_sentence)
        sentenceAnswerContainer = findViewById(R.id.sentence_answer_container)
        sharedPreferences = getSharedPreferences("sentenceProgress", Context.MODE_PRIVATE)

        val redo = intent.getBooleanExtra("redo", false)
        if (redo) {
            resetProgress()
        } else {
            currentSentenceIndex = sharedPreferences.getInt("sentence-index", 0)

        }

        fetchSentences()

        nextButton.setOnClickListener {
            val sentence = sentenceAnswerContainer.children.joinToString(separator = " ", transform = {
                    view: View ->  (view as TextView).text
            })
            if (sentenceAnswerContainer.childCount != 0){
                if (sentence == sentences[currentSentenceIndex].sentenceTranslated) {

                    shuffleWordsGridLayout.removeAllViews()
                    sentenceAnswerContainer.removeAllViews()
                    currentSentenceIndex++
                    saveProgress()

                    Log.d("bbb", "%d %d".format(currentSentenceIndex, sentences.size))
                    if (currentSentenceIndex == sentences.size) {
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.setMessage("Lesson finished!")
                        alertDialogBuilder.setPositiveButton("Go back") { _, _ ->
                            val intent = Intent(this, LessonsPage::class.java)
                            startActivity(intent)
                            finish()
                        }
                        val alertDialogBox = alertDialogBuilder.create()
                        alertDialogBox.show()

                        sharedPreferences.edit().putInt("sentence-index", sentences.size).apply()
                    } else {
                        Toast.makeText(this@SentencesActivity, "Correct answer", Toast.LENGTH_SHORT).show()
                        displaySentence(sentences[currentSentenceIndex])

                    }
                } else {
                    Toast.makeText(this@SentencesActivity, "Wrong answer", Toast.LENGTH_SHORT).show()
                    for (i in (0..<sentenceAnswerContainer.childCount).reversed()) {
                        val v = sentenceAnswerContainer.getChildAt(i)
                        sentenceAnswerContainer.removeView(v)
                        shuffleWordsGridLayout.addView(v)
                    }
                }
            } else if (sentenceAnswerContainer.childCount == 0) {
//                Log.d("AAAAAAAAAA", "fara copil")
                Toast.makeText(this@SentencesActivity, "Please input something first", Toast.LENGTH_SHORT).show()
            }

        }

        quitButton.setOnClickListener {
//            saveProgress()
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage("Do you want to quit the lesson?")
            alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                saveProgress()
                val intent = Intent(this, LessonsPage::class.java)

                startActivity(intent)
                finish()
            }
            alertDialogBuilder.setNegativeButton("No") { _, _ ->
                Toast.makeText(this, "Clicked no", Toast.LENGTH_SHORT).show()
            }
            val alertDialogBox = alertDialogBuilder.create()
            alertDialogBox.show()

        }
        sentenceAnswerContainer.setOnDragListener { v, event ->
            val view = event.localState as View
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    view.visibility = View.INVISIBLE
                    (v as? LinearLayout)?.setBackgroundColor(Color.CYAN)
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    (v as? LinearLayout)?.setBackgroundColor(Color.GREEN)
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    (v as? LinearLayout)?.setBackgroundColor(Color.CYAN)


                }

                DragEvent.ACTION_DROP -> {
                    view.x = event.x
                    shuffleWordsGridLayout.removeView(view)
                    sentenceAnswerContainer.removeView(view)
                    addViewInRightOrderLinear(sentenceAnswerContainer, view)
                    (v as? LinearLayout)?.setBackgroundColor(Color.TRANSPARENT)
                    view.visibility = View.VISIBLE
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    view.visibility = View.VISIBLE
                }

                else -> {}
            }
            true
        }



        shuffleWordsGridLayout.setOnDragListener { v, event ->
            val view = event.localState as View
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    view.visibility = View.INVISIBLE
                }


                DragEvent.ACTION_DROP -> {
                    view.x = event.x
                    shuffleWordsGridLayout.removeView(view)
                    sentenceAnswerContainer.removeView(view)
                    addViewInRightOrder(shuffleWordsGridLayout, view)
                    view.visibility = View.VISIBLE
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    view.visibility = View.VISIBLE
                }

                else -> {}
            }
            true


        }
    }

    private fun calcIndex(event: DragEvent, target: LinearLayout) : Int{
        for (i in 0 until target.childCount){
            val child = target.getChildAt(i)
            val childLoc = IntArray(2)
            child.getLocationOnScreen(childLoc)
            if(event.x < childLoc[0] + child.width / 2){
                return i
            }
        }
        return target.childCount
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
                            sharedPreferences.edit().putInt("sentence-totalSentences", sentences.size).apply()
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
    private fun addViewInRightOrder(container: GridLayout, view: View) {
        val views = mutableListOf<View>();
        views.addAll(container.children);
        views.add(view)
        views.sortBy { v -> v.x }
        container.removeAllViews()
        views.forEach { v: View -> container.addView(word2view((v as TextView).text.toString())) }
    }

    private fun addViewInRightOrderLinear(container: LinearLayout, view: View) {
        val views = mutableListOf<View>();
        views.addAll(container.children);
        views.add(view)
        views.sortBy { v -> v.x }
        container.removeAllViews()
        views.forEach { v: View -> container.addView(word2view((v as TextView).text.toString())) }
    }

    private fun word2view(word: String): TextView {
        return TextView(this).apply {
            text = word
            tag = word
            setBackgroundColor(Color.MAGENTA)
//                setTextColor(Color.WHITE)
            setPadding(16,16,16, 16)
            setOnLongClickListener { v ->
                val item = ClipData.Item(v.tag as? CharSequence)
                val dragData = ClipData(v.tag as? CharSequence, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
                val myShadow = View.DragShadowBuilder(v)
                v.startDragAndDrop(dragData, myShadow, v, 0)
                v.visibility = View.INVISIBLE
                true
            }
        }
    }


    private fun displaySentence(sentence: Sentence){
        sentenceQuestion.text = sentence.sentenceQuestion

        val words = sentence.sentenceTranslated.split(" ").shuffled()
        shuffleWordsGridLayout.removeAllViews()

        words.forEach{ word ->
            val textView = word2view(word)
            shuffleWordsGridLayout.addView(textView)
        }


    }

    private fun saveProgress(){
        with(sharedPreferences.edit()){
            putInt("sentence-index", currentSentenceIndex)
            apply()
        }
    }

    private fun resetProgress(){
        with(sharedPreferences.edit()){
            putInt("sentence-index",0)
            apply()
        }
        currentSentenceIndex = 0
    }



}