package com.uvk.shramapplication.ui.networkconnection

data class Network(
    val name: String,
    val description: String,
    val institution: String,
    val imageUrl: Any
)


data class NetworkResponse(
    val code:String,
    val status:String,
    val count:String,
    val message:String,
    val data:List<NetworkData>
)

data class  NetworkData (
    val id:String,
    val name:String,
    val profile_image:String,
    val designation:String,
    val connected_user_designation:String,

    val email:String,
    val address:String,
    val company_name:String,
    val connected_user_company_name:String,
    var request_status : String,
    val network_connection_id : String,
    val network_id : String,
    val connected_user_id : String,
    val connected_user_name : String,
    val pincode : String,
    val aadhar_image : String,
    val skill : String,
    val state_name : String,
    val district_name : String
)
