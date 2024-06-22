package com.example.languagelearner

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.example.languagelearner.activities.GrammarActivity
import com.example.languagelearner.activities.QuizActivity
import com.example.languagelearner.activities.SentencesActivity
import com.example.languagelearner.activities.SpeechToTextActivity
import com.example.languagelearner.auth.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var categoriesLayout: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private lateinit var sharedPreferences3: SharedPreferences
    private lateinit var sharedPreferences4: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sharedPreferences = requireContext().getSharedPreferences("quizProgress", Context.MODE_PRIVATE)
        sharedPreferences2 = requireContext().getSharedPreferences("speechProgress", Context.MODE_PRIVATE)
        sharedPreferences3 = requireContext().getSharedPreferences("sentenceProgress", Context.MODE_PRIVATE)
        sharedPreferences4 = requireContext().getSharedPreferences("grammarProgress", Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        categoriesLayout = view.findViewById(R.id.categories_layout)

        createButtonAndProgressBar(
            requireContext(),
            "Sentences",
            SentencesActivity::class.java,
            sharedPreferences3,
            "sentence-index",
            "sentence-totalSentences"
        )

        createButtonAndProgressBar(
            requireContext(),
            "Speech",
            SpeechToTextActivity::class.java,
            sharedPreferences2,
            "speech-index",
            "speech-totalSpeech"
        )

        createButtonAndProgressBar(
            requireContext(),
            "Grammar",
            GrammarActivity::class.java,
            sharedPreferences4,
            "grammar-index",
            "grammar-totalGrammar"
        )

        fetchCategories()

        return view
    }

    private fun createButtonAndProgressBar(context: Context, categoryName: String, activityClass: Class<*>, sharedPreferences: SharedPreferences, index:String, total:String){
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
            }
        }

        val button = Button(context).apply {
            text = categoryName
            tag = categoryName
            setBackgroundColor(Color.rgb(228,178,247))

            layoutParams = LinearLayout.LayoutParams(
                450,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 12)
            }
            setPadding(16, 16, 16, 16)
            setOnClickListener {
                val progress = sharedPreferences.getInt(index, 0)
                val totalQuestions = sharedPreferences.getInt(total, 1)
                if (progress >= totalQuestions) {
                    showRedoDialog(context, activityClass, sharedPreferences, index, total)
                } else {
                    val intent = Intent(context, activityClass)
                    startActivity(intent)
                }

            }
        }

        val progressBar =
            ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                layoutParams = LinearLayout.LayoutParams(
                    843,
                    12
                )
                max = 100

//                val prog = sharedPreferences.getInt(index, 0)
//                val totalQuestions = sharedPreferences.getInt(total, 1)
//                progress = (prog.toFloat() / totalQuestions * 100).toInt()
            }
        val progress = sharedPreferences.getInt(index, 0)
        val totalQuestions = sharedPreferences.getInt(total, 1)

        if (totalQuestions == 0) {
            progressBar.progress = 0
        }else {
            progressBar.progress = (progress.toFloat() / totalQuestions * 100).toInt()
        }
        Log.d("Progress -> ", progressBar.progress.toString())
        layout.addView(button)
        layout.addView(progressBar)
        categoriesLayout.addView(layout)
    }

    private fun fetchCategories() {
        RetrofitInstance.service.getCategories().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    if (categories != null) {
                        createCategoryViews(categories)
                    }
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("HomeFragment", "Error fetching categories")
            }
        })
    }

    private fun createCategoryViews(categories: List<String>) {

        categories.forEach { category ->
            createCategoryView(requireContext(), category)

        }
    }

    private fun createCategoryView(context: Context, categoryName: String) {
        val categoryLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setPadding(0, 16, 0, 16)
            }
        }

        val button = Button(context).apply {
            text = categoryName
            tag = categoryName
            setBackgroundColor(Color.rgb(118, 35, 150))

            layoutParams = LinearLayout.LayoutParams(
                450,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 12)
            }
            setPadding(16, 16, 16, 16)
            setOnClickListener {
                val progress = sharedPreferences.getInt("$categoryName-index", 0)
                val totalQuestions = sharedPreferences.getInt("$categoryName-totalQuestions", 1)
                if(progress >= totalQuestions){
                    showRedoDialog(context, QuizActivity::class.java, sharedPreferences, "$categoryName-index", "$categoryName-totalQuestions")
                } else {
                    val intent = Intent(context, QuizActivity::class.java)
                    intent.putExtra("categoryName", categoryName)
                    startActivity(intent)
                }

            }
        }

        val progressBar =
            ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                layoutParams = LinearLayout.LayoutParams(
                    843,
                    12
                )
                max = 100
//                val prog = sharedPreferences.getInt("$categoryName-index", 0)
//                val totalQuestions = sharedPreferences.getInt("$categoryName-totalQuestions", 1)
//                progress = (prog.toFloat() / totalQuestions * 100).toInt()



            }

        val progress = sharedPreferences.getInt("$categoryName-index", 0)
        val totalQuestions = sharedPreferences.getInt("$categoryName-totalQuestions", 1)
          if (totalQuestions == 0) {
              progressBar.progress = 0
          }else {
              progressBar.progress = (progress.toFloat() / totalQuestions * 100).toInt()
          }


        Log.d("Progress -> ", progressBar.progress.toString())
        categoryLayout.addView(button)
        categoryLayout.addView(progressBar)
        categoriesLayout.addView(categoryLayout)
    }

    private fun showRedoDialog(context: Context, activityClass: Class<*>, sharedPreferences: SharedPreferences, index: String, total: String){
        val alertDialogBuilder = AlertDialog.Builder(context).apply {
            setMessage("Redo lesson?")
            setPositiveButton("Yes"){dialog,_ ->
                resetProgress(sharedPreferences, index, total)
                dialog.dismiss()

                val intent = Intent(context, activityClass)
                intent.putExtra("redo", true)
                startActivity(intent)
            }
            setNegativeButton("No") {dialog,_ ->
                dialog.dismiss()
            }
        }
        val alertDialogBox = alertDialogBuilder.create()
        alertDialogBox.show()
    }

    private fun resetProgress(sharedPreferences: SharedPreferences, index: String,total: String){
        with(sharedPreferences.edit()){
            putInt(index, 0)
            putInt(total, 1)
            apply()
        }
//        updateProgressBar()
    }
//    private fun updateProgressBar()

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}