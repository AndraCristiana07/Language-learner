package com.example.languagelearner.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.languagelearner.HomeFragment
import com.example.languagelearner.ProfileFragment
import com.example.languagelearner.R
import com.example.languagelearner.ReviewFragment
import com.example.languagelearner.databinding.ActivityLessonsPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class LessonsPage : AppCompatActivity() {
    // TODO : TEXT TO SPEECH
    // TODO : RESOLVE SENTENCE BUTTON
    // TODO : USER NAME IN LESSON PAGE - to work for dif users
    // TODO : IMG IN BACKEND

    private lateinit var binding: ActivityLessonsPageBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLessonsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = this.getSharedPreferences("nameForUser", Context.MODE_PRIVATE)

//        setContentView(R.layout.activity_lessons_page)
//        sharedPreferences = getSharedPreferences("userName", Context.MODE_PRIVATE)
//        val userName = sharedPreferences.getString("userName", "")
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        Log.d("LessonsPageActivity", "Received userName: $userName")
        binding.username.text = "Hi, $userName!"
        binding.username.text = "Hi, $userName"
        loadFragment(HomeFragment())
        bottomNav = findViewById(R.id.bottomNavigationView) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.review -> {
                    loadFragment(ReviewFragment())
                    true
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> true
            }
        }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

//        progressBar = findViewById(R.id.progressBar1)
//        progressBar.max = 100
//        progressBar.progress = 75
//        Thread {
//            while (progressBar.progress < progressBar.max) {
//                Thread.sleep(10)
//                progressBar.progress += 1
//            }
//
//            runOnUiThread {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//            }
//        }.start()



//        binding = ActivityLessonsPageBinding.inflate(layoutInflater)
//        setContentView(binding.root)

//        binding.button5.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout,
            R.string.menu_open,
            R.string.menu_close
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()

//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.flFragment,fragment)
//            commit()
//        }
    }


}