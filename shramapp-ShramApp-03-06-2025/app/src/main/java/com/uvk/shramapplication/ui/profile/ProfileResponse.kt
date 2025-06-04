package com.uvk.shramapplication.ui.profile

data class ProfileResponse (
        val code:String,
        val status:String,
        val message:String,
        val data:List<ProfileData>
    )

    data class  ProfileData (
        val profile_image: String,
        val name: String,
        val mobile_no: String,
        val email: String,
        val address: String,
        val state_name: String,
        val district_name: String,
        val state_id: Int,
        val state: Int,
        val district_id: Int,
        val district: Int,
        val pincode: String,
        val gender: String,
        val gender_id: Int,
        val education_id: Int,
        val education: String,
        val experience: String,
        val main_category_id: String,
        val main_category_name: String,
        val categories: List<Category>,
        val sub_categories: List<SubCategory>,
        val skill: String,
        val role: String,
        val role_id: String,
        val company_name: String,
        val designation: String,
        val aadhar_image: String,
        val aadhar_name: String,
        val aadhar_no: String
    )

data class Category(
    val id: Int,
    val name: String
)

data class SubCategory(
    val id: Int,
    val name: String
)

data class CategoryResponse(
    val code: Int,
    val status: String,
    val data: List<CategoryData>
)

data class CategoryData(
    val id: Int,
    val category_name: String
)


data class PrfileRequest(
    val user_id: String,
    val mobile_no: String,
    val email: String,
    val address: String,
    val state: Int,
    val district: Int,
    val gender_id: Int,
    val education_id: Int,
    val pincode: String,
    val main_category_id: String,
    val category_id: List<Int>,
    val sub_category_id: List<Int>,
    val skill: String,
    val experience: String,
    val role: String,
    val aadhar_image: String,
    val aadhar_name: String,
    val aadhar_no: String
)