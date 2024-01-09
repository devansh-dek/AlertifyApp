package com.example.alertify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.example.alertify.locationaccess.EmergencyVehicle

class ChoosingOption : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choosing_option)

        val cd1 = findViewById<CardView>(R.id.card1)
        val cd2 = findViewById<CardView>(R.id.card2)

        cd1.setOnClickListener {
            val intent = Intent(this,EmergencyVehicle::class.java)
            startActivity(intent)
        }
    cd2.setOnClickListener {
            val intent = Intent(this,mainActive::class.java)
        startActivity(intent)

        }



    }
}