package com.example.alertify.locationaccess

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.alertify.LocationEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus

class EmergencyService : Service() {


    companion object {
        const val CHANNEL_ID = "12345"
        const val NOTIFICATION_ID=12345
    }
//    val deviceId = Settings.Secure.getString(
//        contentResolver,
//        Settings.Secure.ANDROID_ID
//    )

    private lateinit var deviceId: String

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

    private var notificationManager: NotificationManager? = null

    private var location: Location?=null
  lateinit  var databaseRef : DatabaseReference
    override fun onCreate() {
        super.onCreate()
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500)
                .build()
        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, "locations", NotificationManager.IMPORTANCE_LOW)
            val notificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }


    @Suppress("MissingPermission")
    fun createLocationRequest(){
        try {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest!!,locationCallback!!,null
            )
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun removeLocationUpdates(){
        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
        stopForeground(true)
        stopSelf()
    }

    private fun onNewLocation(locationResult: LocationResult) {
        location = locationResult.lastLocation
        EventBus.getDefault().post(
            LocationEvent(
            latitude = location?.latitude,
            longitude = location?.longitude
        )
        )
         databaseRef  = Firebase.database.reference
        val locationlogging = LocationEvent(location?.latitude,location?.longitude)
        databaseRef.child("EmergencyVehicleLocation").child(deviceId).setValue(locationlogging)
            .addOnSuccessListener {
                //  Toast.makeText(this,"Wriiten on data base ",Toast.LENGTH_SHORT).show()
                Log.e("@@@@@@@@@@@@","DATA SENTTTTTTTTTTT")

            }
            .addOnFailureListener {
                //Toast.makeText(this,"COULDNT WRITE  on data base ",Toast.LENGTH_SHORT).show()
                Log.e("##########","Didnt wrote on database")
            }
        startForeground(NOTIFICATION_ID,getNotification())
    }

    fun getNotification(): Notification {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Notifying other Vehicles")
            .setContentText(
//                "Latitude--> ${location?.latitude}\nLongitude --> ${location?.longitude}"
                "!!"
            )
            .setSmallIcon(com.example.alertify.R.drawable.appicon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSound(null) // Make the notification silent
            .setOngoing(true)
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            notification.setChannelId(CHANNEL_ID)
        }
        return notification.build()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        createLocationRequest()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
        databaseRef.child("EmergencyVehicleLocation").child(deviceId).removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "Child node deleted successfully")
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Error deleting child node: $error")
            }
    }
}