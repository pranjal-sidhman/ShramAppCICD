package com.uvk.shramapplication.ui.registration

data class SignUpResponse(
    val code: String,
    val message: String,
    val status: String,
    val available_status: String
)

data class SignUpRequest(
    val profile_image: String,
    val name: String,
    val mobile_no: String,
    val email: String,
    val address: String,
    val state: Int,
    val district: Int,
    val pincode: String,
    val gender_id: Int,
    val education_id: Int,
    val experience: String,
    val main_category_id: String,
    val category_id: List<Int>,
    val sub_category_id: List<Int>,
    val skill: String,
    val role: String,
    val company_name: String,
    val designation: String,
    val aadhar_image: String,
    val other_category_name : String
    //val other_category_name : List<String>
)