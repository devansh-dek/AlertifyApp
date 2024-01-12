package com.example.alertify

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.alertify.databinding.ActivityUserBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class mainActive : AppCompatActivity() , OnMapReadyCallback {
    lateinit var dialog : Dialog

    private var _binding: ActivityUserBinding? = null
    private val binding: ActivityUserBinding
        get() = _binding!!
    private lateinit var dbref : DatabaseReference

    private lateinit var mMap: GoogleMap
var ringg=0
var mapset=0
    private var service: Intent?=null

    private val ShowDialog: Button? = null
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

        dialog = Dialog(this)
        dialog.setContentView(R.layout.questionbox)
//        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.ambulance))
        var buttonGood = dialog.findViewById<Button>(R.id.btn1)
        buttonGood.setOnClickListener {
            dialog.dismiss()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setContentView(binding.root)
//_binding = ActivityUserBinding.inflate(layoutInflater)
//        service = Intent(this,LocationService::class.java)
        service = Intent(this,LocationService::class.java)
        dbref = FirebaseDatabase.getInstance().getReference("EmergencyVehicleLocation")
        binding.apply {
            btnStartLocationTracking.setOnClickListener {
                checkPermissions()
                Toast.makeText(applicationContext,"Started Sharing Location!! ",Toast.LENGTH_SHORT).show()
            }

            btnStopLocationTracking.setOnClickListener {
                stopService(service)
                Toast.makeText(applicationContext,"Location Sharing Stopped! ",Toast.LENGTH_SHORT).show()


            }
            fab.setOnClickListener {
//                Toast.makeText(applicationContext,"Question! ",Toast.LENGTH_SHORT).show()

                dialog.show()



            }
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
//        Log.e("@@@@@","RECIVED LOCATION")


        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){

                        val user = userSnapshot.getValue(LocationEvent::class.java)
                        Log.e("$$$$$$$$$","the value of lat and long are ${user?.latitude}")
                        val distance = calculateDistance(locationEvent.latitude!!, locationEvent.longitude!!, user?.latitude!!,user?.longitude!!)
                        if (distance <= 0.5) {
//                            Log.e("(((((((((","Came inside the location under five zone")
                            mMap.clear()

                            val sydney = LatLng(locationEvent?.latitude!!, locationEvent?.longitude!!)
                            val UserLOcation = LatLng(user?.latitude!!, user?.longitude!!)
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
                            mMap.addMarker(MarkerOptions().position(sydney).title("YOU"))
                            mMap.addMarker(MarkerOptions().position(UserLOcation).title("EMERGENCY VEHICLE").icon(icon))

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                            val zoomLevel = 18.0f
// Create a CameraUpdate object to zoom to the specified level
                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel)
                            val cameraUpdate2 = CameraUpdateFactory.newLatLngZoom(UserLOcation, zoomLevel)

// Apply the camera update
                            mMap.moveCamera(cameraUpdate)




// Devices are within half a mile radius
                            // Perform your desired action here
                            Log.e("&&&&&&&&&&&","vechicle nearby moye moye")
                        }
                        else{
                            Toast.makeText(applicationContext,"No EmergencyVehicles Nearby!",Toast.LENGTH_SHORT).show()
                            Log.e("&&&&&&&&&&&","vechicle IS NOT NEARBY moye moye")

                            mMap.clear()

                            val sydney = LatLng(locationEvent?.latitude!!, locationEvent?.longitude!!)
                            val UserLOcation = LatLng(user?.latitude!!, user?.longitude!!)
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
                            mMap.addMarker(MarkerOptions().position(sydney).title("YOU"))

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                            val zoomLevel = 17.0f
// Create a CameraUpdate object to zoom to the specified level
                            val cameraUpdate2 = CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel)

// Apply the camera update
                            mMap.moveCamera(cameraUpdate2)
                        }

                    }



                }
                else{
                    Toast.makeText(applicationContext,"No EmergencyVehicles Nearby!",Toast.LENGTH_SHORT).show()
                    Log.e("&&&&&&&&&&&","vechicle IS NOT NEARBY moye moye")

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
                    mMap.addMarker(MarkerOptions().position(sydney).title("YOU"))

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

// Apply the camera update
                    if(mapset==0){
                        val zoomLevel = 18.0f
// Create a CameraUpdate object to zoom to the specified level
                        val cameraUpdate2 = CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel)

                        mMap.moveCamera(cameraUpdate2)

                    }
                    mapset++
                    if(mapset==15){
                        mapset=0
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })


    }

    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double {
        val R = 6371.0 // Earth radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c * 0.621371 // Convert distance to miles
    }
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        // Add a marker in Sydney and move the camera

    }



}
