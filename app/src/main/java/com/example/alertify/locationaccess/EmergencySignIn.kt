package com.example.alertify.locationaccess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.alertify.R
import com.example.alertify.databinding.ActivityEmergencySignInBinding

class EmergencySignIn : AppCompatActivity() {


    private lateinit var binding :ActivityEmergencySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmergencySignInBinding.inflate(layoutInflater)
        binding.loginButton.setOnClickListener(View.OnClickListener {
            if (binding.username.text.toString() == "user" && binding.password.text.toString() == "1234"){
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,EmergencyVehicle::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
        })


        setContentView(binding.root)


    }
}