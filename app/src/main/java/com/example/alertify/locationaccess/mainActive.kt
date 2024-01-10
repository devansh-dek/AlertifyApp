package com.example.alertify

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.alertify.databinding.ActivityUserBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class mainActive : AppCompatActivity() {

    private var _binding: ActivityUserBinding? = null
    private val binding: ActivityUserBinding
        get() = _binding!!



    private var service: Intent?=null

    private val backgroundLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {

            }
        }

    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    }

                }
                it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {

                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
//_binding = ActivityUserBinding.inflate(layoutInflater)
//        service = Intent(this,LocationService::class.java)
        service = Intent(this,LocationService::class.java)

        binding.apply {
            btnStartLocationTracking.setOnClickListener {
                checkPermissions()
                Toast.makeText(applicationContext,"Location Tracking Started! ",Toast.LENGTH_SHORT).show()
            }

//            btnRemoveLocationTracking.setOnClickListener {
//                stopService(service)
//            }
        }

    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }else{
                startService(service)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent){
//        binding.tvLatitude.text = "Latitude -> ${locationEvent.latitude}"
//        binding.tvLongitude.text = "Longitude -> ${locationEvent.longitude}"
        Log.e("@@@@@","RECIVED LOCATION")
    }

}
