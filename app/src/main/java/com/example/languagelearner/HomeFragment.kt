package com.example.languagelearner

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.example.languagelearner.activities.QuizActivity
import com.example.languagelearner.activities.SentencesActivity
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
    private lateinit var sentenceButton: Button
    private lateinit var sentenceProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sharedPreferences = requireContext().getSharedPreferences("quizProgress", Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        categoriesLayout = view.findViewById(R.id.categories_layout)
        sentenceButton = view.findViewById(R.id.sentence_button)
        sentenceProgressBar = view.findViewById(R.id.sentence_progressBar)

        sentenceButton.setOnClickListener {
            val intent = Intent(activity, SentencesActivity::class.java)
            startActivity(intent)
        }

        fetchCategories()
//        val button_animal = view.findViewById<Button>(R.id.button_animals)
//        val button_food = view.findViewById<Button>(R.id.button_fruits)
//        val button_work = view.findViewById<Button>(R.id.button_work)
//        val button_numbers = view.findViewById<Button>(R.id.button_numbers)
//        val button_colors = view.findViewById<Button>(R.id.button_colors)
//        val button_seasons = view.findViewById<Button>(R.id.button_vegetables)
//        val button_sports = view.findViewById<Button>(R.id.button_sports)
//
//        button_animal.setOnClickListener{
//            val intent = Intent(activity, QuizActivity::class.java)
//            startActivity(intent)
//        }

        return view
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
            )
        }

        val button = Button(context).apply {
            text = categoryName
            tag = categoryName

            layoutParams = LinearLayout.LayoutParams(
                450,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                val intent = Intent(context, QuizActivity::class.java)
                intent.putExtra("categoryName", categoryName)
                startActivity(intent)
            }
        }

        val progressBar =
            ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                layoutParams = LinearLayout.LayoutParams(
                    843,
                    12
                )
                max = 100


            }
        val progress = sharedPreferences.getInt("$categoryName-index", 0)
        val totalQuestions = sharedPreferences.getInt("$categoryName-totalQuestions", 1)
        progressBar.progress = (progress.toFloat() / totalQuestions * 100).toInt()
        Log.d("VVV", progressBar.progress.toString())
        categoryLayout.addView(button)
        categoryLayout.addView(progressBar)
        categoriesLayout.addView(categoryLayout)
    }

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