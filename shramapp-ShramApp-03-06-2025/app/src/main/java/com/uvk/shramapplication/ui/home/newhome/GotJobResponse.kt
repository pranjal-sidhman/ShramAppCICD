package com.uvk.shramapplication.ui.home.newhome



data class GotJobResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: List<Job>
)

data class Job(
    val id: String,

    val title: String,
    val description: String,
    val location: String,
    val created_date: String,
    val profile_image: String,
    val employee_name: String,
    val address: String,
    val designation: String,
    val job_title: String,
    val aadhar_no: String,
    val company_name: String,
    val image: String?,
    val save_status: String?,
    val salary_range: String,
    val salary: String,
    val salary_amount: String,
    val salary_type: String,
    val recently_added: Boolean,
    val job_types: List<JobType>,
    val categories: List<Category>,
    val sub_categories: List<SubCategory>
)

data class JobType(
    val job_type_ids: Int,
    val job_type_names: String
)

data class Category(
    val category_ids: Int,
    val category_names: String
)

data class SubCategory(
    val sub_category_ids: Int,
    val sub_category_names: String
)
