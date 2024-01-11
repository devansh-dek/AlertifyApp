package com.example.alertify.locationaccess

data class userdeatails(val username : String?=null,val password : String?=null){
    // Empty constructor required by Firebase
    constructor() : this(null, null)
}
