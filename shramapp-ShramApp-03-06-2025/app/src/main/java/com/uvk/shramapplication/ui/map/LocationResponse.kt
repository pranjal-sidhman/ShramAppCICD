package com.uvk.shramapplication.ui.map

data class LocationResponse(
    val code: String,
    val status: String,
    val message : String,
    val count : String,
    val data:List<LocationData>
)
data class LocationData(
    val user_id : String,
    val user_name : String,
    val mobile_no : String,
    val company_name : String,
    val address : String,
    val longitude : String,
    val latitude : String,
    val designation : String
)