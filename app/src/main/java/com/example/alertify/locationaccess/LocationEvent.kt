package com.example.alertify


data class LocationEvent(
    val latitude:Double?,
    val longitude:Double?
){
    // Empty constructor required by Firebase
    constructor() : this(null, null)
}