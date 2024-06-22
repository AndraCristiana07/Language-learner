package com.example.languagelearner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.LocaleList
import android.speech.tts.TextToSpeech
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class SpeechToTextActivity : AppCompatActivity() {
    private lateinit var tts: TextToSpeech
    private lateinit var speakButton: Button
    private lateinit var questionText: TextView
    private lateinit var nextButton: Button
    private lateinit var quitButton: Button
    private var currentSentenceIndex = 0
    private lateinit var sentences: List<Sentence>
    private lateinit var answerEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_speech_to_text)
        tts = TextToSpeech(this, TextToSpeech.OnInitListener{
            onInit(it)
        })
        speakButton = findViewById(R.id.button_speak)
        nextButton = findViewById(R.id.button_next_speech)
        quitButton = findViewById(R.id.button_quit_speech)
        questionText = findViewById(R.id.text_to_speech)
        answerEditText = findViewById(R.id.answer_speech)
        answerEditText.setImeHintLocales(LocaleList(Locale("ro","RO")))

        sharedPreferences = getSharedPreferences("speechProgress", Context.MODE_PRIVATE)

        val redo = intent.getBooleanExtra("redo", false)
        if(redo){
            resetProgress()
        } else {
            currentSentenceIndex = sharedPreferences.getInt("speech-index", 0)
        }
        fetchSentences()

        speakButton.setOnClickListener {
            tts?.setSpeechRate(1.0f)
            tts?.setPitch(0.8f)

            speakOut(sentences[currentSentenceIndex].sentenceTranslated)
        }

        nextButton.setOnClickListener {
            if(answerEditText.text.isNotEmpty()){
                val sentence = sentences[currentSentenceIndex].sentenceTranslated
                val userAnswer = answerEditText.text
                val trimmedSentence = userAnswer.toString().trim().lowercase()
                if(trimmedSentence == sentence.trim().lowercase()){
                    Toast.makeText(this, "Correct answer", Toast.LENGTH_SHORT).show()
                    if(currentSentenceIndex < sentences.size -1){
                        currentSentenceIndex++
                        saveProgress()
                        displaySentence(sentences[currentSentenceIndex])
                        answerEditText.text.clear()

                    } else {
//                        clearProgress()
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.setMessage("Lesson finished!")
                        alertDialogBuilder.setPositiveButton("Go back") { _, _ ->
                            val intent = Intent(this, LessonsPage::class.java)
                            startActivity(intent)
                            finish()
                        }
                        val alertDialogBox = alertDialogBuilder.create()
                        alertDialogBox.show()
                        sharedPreferences.edit().putInt("speech-index", sentences.size).apply()
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


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }

    private fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("ro","RO"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")

        }
    }

    private fun speakOut(text: String){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")

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
                            sharedPreferences.edit().putInt("speech-totalSpeech", sentences.size).apply()
                            displaySentence(sentences[currentSentenceIndex])
                        } else {
                            Toast.makeText(this@SpeechToTextActivity, "Failed to fetch sentences", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<List<Sentence>>, t: Throwable) {
                    Toast.makeText(this@SpeechToTextActivity, "Failed to fetch sentences", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displaySentence(sentence: Sentence){

        questionText.text = sentence.sentenceQuestion
    }

    private fun saveProgress(){
        with(sharedPreferences.edit()){
            putInt("speech-index", currentSentenceIndex)
            apply()
        }
    }

    private fun clearProgress(){
        with(sharedPreferences.edit()) {
            remove("speech-index")
            remove("speech-totalSpeech")
            apply()
        }
    }

    private fun resetProgress(){
        with(sharedPreferences.edit()){
            putInt("speech-index",0)
            apply()
        }
        currentSentenceIndex = 0
    }

}