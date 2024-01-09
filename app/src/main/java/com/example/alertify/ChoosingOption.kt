package com.example.alertify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class ChoosingOption : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choosing_option)

        val cd1 = findViewById<CardView>(R.id.card1)
        val cd2 = findViewById<CardView>(R.id.card2)

        cd1.setOnClickListener {

        }
    cd2.setOnClickListener {
            val intent = Intent(this,mainActive::class.java)
        startActivity(intent)

        }



    }
}