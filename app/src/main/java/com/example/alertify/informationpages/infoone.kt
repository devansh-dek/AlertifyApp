package com.example.alertify.informationpages

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.alertify.R
import com.example.alertify.databinding.ActivityInfooneBinding

class infoone : AppCompatActivity() {

    private val sharedPrefName = "MySharedPref"
    private val isFirstTimeKey = "isFirstTime"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if it's the first time the user is launching the app
        val sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val isFirstTime = sharedPref.getBoolean(isFirstTimeKey, true)

        if (isFirstTime) {
            // If it's the first time, show the infoone activity
            setContentView(R.layout.activity_infoone)

            var btn = findViewById<Button>(R.id.btn1)
            btn.setOnClickListener {
                // Save that the activity has been shown
                with(sharedPref.edit()) {
                    putBoolean(isFirstTimeKey, false)
                    apply()
                }

                // Start the next activity (infotwo)
                val intent = Intent(this, infotwo::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            // If it's not the first time, directly start the next activity (infotwo)
            val intent = Intent(this, infotwo::class.java)
            startActivity(intent)
            finish()
        }
    }
}
