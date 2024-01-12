package com.example.alertify.locationaccess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.alertify.LocationEvent
import com.example.alertify.R
import com.example.alertify.databinding.ActivityEmergencyRegisterBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EmergencyRegister : AppCompatActivity() {
    lateinit var binding : ActivityEmergencyRegisterBinding
    private lateinit var deviceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)




        binding.loginButton.setOnClickListener {

            var usernamee = binding.username.text.toString()
            var passwordd = binding.password.text.toString()
            var namee = binding.fullname.text.toString()
            val regis = userdeatails(usernamee,passwordd)
            var databaseRef : DatabaseReference = Firebase.database.reference

            databaseRef.child("Vehicles").child(namee).setValue(regis)
                .addOnSuccessListener {
                      Toast.makeText(this,"Wriiten on data base ",Toast.LENGTH_SHORT).show()
                Log.e("^^^^^^^^^","DATA are ${namee} and ${passwordd} and ${usernamee}")

                }
                .addOnFailureListener {
                    Toast.makeText(this,"COULDNT WRITE  on data base ", Toast.LENGTH_SHORT).show()
//Log.e("##########","Didnt wrote on database")
                }



            Toast.makeText(this,"User added Successfully",Toast.LENGTH_SHORT).show()
            var intent = Intent(this,EmergencyVehicle::class.java)
            startActivity(intent)
            finish()

        }



        binding.loginInstead.setOnClickListener {
            var intent = Intent(this,EmergencySignIn::class.java)
            startActivity(intent)
            finish()



        }

    }
}