package com.example.alertify.locationaccess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.alertify.LocationEvent
import com.example.alertify.R
import com.example.alertify.databinding.ActivityEmergencySignInBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmergencySignIn : AppCompatActivity() {
    private lateinit var dbref : DatabaseReference

    private fun loginUser() {
        val enteredUsername = binding.username.text.toString()
        val enteredPassword = binding.password.text.toString()
        dbref = FirebaseDatabase.getInstance().getReference("Vehicles")

        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isUserFound = false
//                Toast.makeText(applicationContext, "Inside the database", Toast.LENGTH_SHORT).show()

                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(userdeatails::class.java)

                    // Add a null check for user
                    if (user != null) {
//                        Log.e("***********", "the username is ${user.username} and password is ${user.password}")
//                        Toast.makeText(applicationContext, "the username is ${user.username} and password is ${user.password}", Toast.LENGTH_SHORT).show()

                        // Check if the entered credentials match with any user in the database
                        if (user.username == enteredUsername && user.password == enteredPassword) {
                            isUserFound = true
                            break
                        }
                    }
                }

                if (isUserFound) {
                    Toast.makeText(this@EmergencySignIn, "Login Successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@EmergencySignIn, EmergencyVehicle::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@EmergencySignIn, "Login Failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error, if any
                Toast.makeText(this@EmergencySignIn, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private lateinit var binding :ActivityEmergencySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmergencySignInBinding.inflate(layoutInflater)



        binding.loginButton.setOnClickListener(View.OnClickListener {
//            if (binding.username.text.toString() == "user" && binding.password.text.toString() == "1234"){
//                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this,EmergencyVehicle::class.java)
//                startActivity(intent)
//                finish()
//
//            } else {
//                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
//            }
            loginUser()
        })


        setContentView(binding.root)


    }
}