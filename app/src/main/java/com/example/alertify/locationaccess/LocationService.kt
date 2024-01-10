package com.example.alertify

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
import com.example.alertify.locationaccess.EmergencyService
import com.google.android.gms.location.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationService : Service() {


    companion object {
        const val CHANNEL_ID = "12345"
        const val CHANNEL_IDD = "12245"
        const val NOTIFICATION_ID=12345
        const val NOTIFICATION_IDD=12245
    }
//    val deviceId = Settings.Secure.getString(
//        contentResolver,
//        Settings.Secure.ANDROID_ID
//    )
private lateinit var dbref : DatabaseReference

var notifcationChoose = 0


    private lateinit var deviceId: String
    private lateinit var userArrayList : ArrayList<LocationEvent>

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    var ring =0

    private var notificationManager: NotificationManager? = null

    private var location:Location?=null
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
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

    override fun onCreate() {
        super.onCreate()
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        userArrayList = arrayListOf<LocationEvent>()

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


            if(ring==0){
                Log.e("^^^^^^^^^^","Played first one ")
                val notificationChannel =
                    NotificationChannel(CHANNEL_ID, "locations", NotificationManager.IMPORTANCE_LOW)
                val notificationManager =
                    getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(notificationChannel)
                ring++
            }
            else{
                val notificationChannel2 =
                    NotificationChannel(CHANNEL_IDD, "locations", NotificationManager.IMPORTANCE_LOW)
                val notificationManager =
                    getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(notificationChannel2)
            }


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
        EventBus.getDefault().post(LocationEvent(
            latitude = location?.latitude,
            longitude = location?.longitude
        ))


        CheckForLocation(location?.latitude,location?.longitude)

        var databaseRef : DatabaseReference = Firebase.database.reference
        val locationlogging = LocationEvent(location?.latitude,location?.longitude)
        databaseRef.child("userlocation").child(deviceId).setValue(locationlogging)
            .addOnSuccessListener {
              //  Toast.makeText(this,"Wriiten on data base ",Toast.LENGTH_SHORT).show()
                Log.e("@@@@@@@@@@@@","DATA SENTTTTTTTTTTT")

            }
            .addOnFailureListener {
                //Toast.makeText(this,"COULDNT WRITE  on data base ",Toast.LENGTH_SHORT).show()
Log.e("##########","Didnt wrote on database")
            }
//        if(ring==0){
        startForeground(NOTIFICATION_ID,getNotification())
//        ring++
//        }
//        else{
//            startForeground(NOTIFICATION_ID,getNotification())
//ring++
//            if(ring==15){
//                ring=0
//            }
//        }
    }

    private fun CheckForLocation(latitude: Double?, longitude: Double?) {
        Log.e("$$$$$$$$$","CAME INSIDE THE FUNCTION")

        dbref = FirebaseDatabase.getInstance().getReference("EmergencyVehicleLocation")

        dbref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){

                        val user = userSnapshot.getValue(LocationEvent::class.java)
            Log.e("$$$$$$$$$","the value of lat and long are ${user?.latitude}")
                        val distance = calculateDistance(latitude!!, longitude!!, user?.latitude!!,user?.longitude!!)
                        if (distance <= 0.5) {
                            // Devices are within half a mile radius
                            // Perform your desired action here
                            notifcationChoose =1
                            Log.e("&&&&&&&&&&&","vechicle nearby moye moye")
                        }
                        else{
                            notifcationChoose =0
                            Log.e("&&&&&&&&&&&","vechicle IS NOT NEARBY moye moye")

                        }

                    }



                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })



    }

    fun getNotification(): Notification {
        if (notifcationChoose == 0) {
            Log.e("%%%%%%%%%", "Displayed first one ${notifcationChoose}")
            val notification = NotificationCompat.Builder(this, EmergencyService.CHANNEL_ID)
                .setContentTitle("Looking For Emergency Vehicles")
                .setContentText("Thanks for your service!!")
                .setSmallIcon(com.example.alertify.R.drawable.appicon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification.setChannelId(CHANNEL_ID)
            }
            return notification.build()
        } else {
            Log.e("%%%%%%%%%", "Displayed Second one ${notifcationChoose}")

            val notification = NotificationCompat.Builder(this, EmergencyService.CHANNEL_ID)
                .setContentTitle("Emergency Vehicle Nearby!!!!")
                .setContentText("Please Give Space Vehicle Approaching")
                .setSmallIcon(com.example.alertify.R.drawable.appicon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification.setChannelId(CHANNEL_ID)
            }


            return notification.build()
        }
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
    }
}