package com.uvk.shramapplication.ui.login

import com.google.gson.annotations.SerializedName

data class OtpResponse(
    val code: String,
    val status: String,
    val message : String,
    val data:List<LoginData>

)

data class VerifyOtpRequest(
    @SerializedName("mobile_no")
    var mobile_no: String
    )

data class LoginData(
    val token : String,
    val id : String,
    val name : String,
    val mobile_no : String,
    val email : String,
    val address : String,
    val state_id : String,
    val district_id : String,
    val profile_image : String,
    val designation : String,
    val company_name : String,
    val role_id : String,
    val role : String,
    val otp : String,
    val is_logged_in : Int
)