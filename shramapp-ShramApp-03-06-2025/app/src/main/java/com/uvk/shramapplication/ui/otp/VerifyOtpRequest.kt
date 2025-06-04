package com.uvk.shramapplication.ui.otp

data class VerifyOtpRequest(
    val mobile_no: String,
    val otp: String
)

data class VerifyOtpResponse(
    val status: String,
    val message: String,
    val otp: String,
    val role_id: String,
    val user_id: String,
    val is_logged_in: String,
    val code: Int // Replace with appropriate type if the API returns specific data
)
