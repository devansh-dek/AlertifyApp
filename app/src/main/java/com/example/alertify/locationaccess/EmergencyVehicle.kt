package com.example.alertify.locationaccess

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.alertify.ChoosingOption
import com.example.alertify.LocationEvent
import com.example.alertify.LocationService
import com.example.alertify.R
import com.example.alertify.databinding.ActivityEmergencyVehicleBinding
import com.example.alertify.databinding.ActivityUserBinding
import com.example.alertify.gem.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class EmergencyVehicle : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var dialog : Dialog

    private var _binding: ActivityEmergencyVehicleBinding ? = null
    private val binding: ActivityEmergencyVehicleBinding
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
        _binding = ActivityEmergencyVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)
//_binding = ActivityUserBinding.inflate(layoutInflater)
//        service = Intent(this,LocationService::class.java)

        binding.fab2.setOnClickListener {
            var intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)

        }


        dialog = Dialog(this)
        dialog.setContentView(R.layout.infoboxes)
//        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.ambulance))
        var buttonGood = dialog.findViewById<Button>(R.id.btn1)
        buttonGood.setOnClickListener {
            dialog.dismiss()
        }

binding.fab.setOnClickListener {

    dialog.show()

}


        service = Intent(this, EmergencyService::class.java)
        supportActionBar?.apply {
            title = "Alertify"
            setDisplayHomeAsUpEnabled(true) // Enable the back button
        }

        binding.apply {
            btnStartLocationTracking.setOnClickListener {
                checkPermissions()
            }

            btnRemoveLocationTracking.setOnClickListener {
                stopService(service)
                Toast.makeText(applicationContext,"Location Sharing Stopped!",Toast.LENGTH_SHORT).show()
            }
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }
    override fun onSupportNavigateUp(): Boolean {
        var itent  = Intent(this,ChoosingOption::class.java)
        startActivity(itent)

        onBackPressed()
        return true
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
//        binding.tvLongitude.text = "Longitude -> ${locationEvent.longitude}
        mMap.clear()

        val sydney = LatLng(locationEvent?.latitude!!, locationEvent?.longitude!!)
        // Replace R.drawable.your_custom_marker_icon with the actual resource ID of your custom marker icon
        val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.ambulance)

// Specify the desired width and height for the custom icon
        val width = 150  // in pixels
        val height = 150 // in pixels

// Scale the original bitmap to the desired size
        val scaledIcon = Bitmap.createScaledBitmap(originalIcon, width, height, false)

// Create a BitmapDescriptor from the scaled bitmap
        val icon = BitmapDescriptorFactory.fromBitmap(scaledIcon)

// Add the marker with the custom icon
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney").icon(icon))

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        val zoomLevel = 18.0f
// Create a CameraUpdate object to zoom to the specified level
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel)

// Apply the camera update
        mMap.moveCamera(cameraUpdate)


        Log.e("@@@@@","RECIVED LOCATION")
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

    }

}