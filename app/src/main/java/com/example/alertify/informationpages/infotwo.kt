package com.example.alertify.informationpages

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.alertify.ChoosingOption
import com.example.alertify.R

class infotwo : AppCompatActivity() {

    private val sharedPrefName1 = "MySharedPrefe"
    private val isFirstTimeKey1 = "isFirstTimee"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if it's the first time the user is launching the app
        val sharedPref = getSharedPreferences(sharedPrefName1, Context.MODE_PRIVATE)
        val isFirstTime = sharedPref.getBoolean(isFirstTimeKey1, true)

        if (isFirstTime) {
            // If it's the first time, show the infoone activity
            setContentView(R.layout.activity_infotwo)

            var btn = findViewById<Button>(R.id.btn1)
            btn.setOnClickListener {
                // Save that the activity has been shown
                with(sharedPref.edit()) {
                    putBoolean(isFirstTimeKey1, false)
                    apply()
                }

                // Start the next activity (infotwo)
                val intent = Intent(this, ChoosingOption::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            // If it's not the first time, directly start the next activity (infotwo)
            val intent = Intent(this, ChoosingOption::class.java)
            startActivity(intent)
            finish()
        }
    }
}