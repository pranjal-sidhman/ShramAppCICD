package com.uvk.shramapplication.ui.employeer.job_post

data class AddJobPostResponse (
val code: Int,
val status: Boolean,
val message: String
)

data class AddJobPostRequest(
    val user_id: String,
    val title: String,
    val description: String,
    val state_id: Int,
    val district_id: Int,
    val gender_id: Int,
    val location: String,
    val job_type_ids: List<Int>,
    val company_name: String,
    val salary_type_id: Int,
    val salary_range_id: Int,
    val categories: List<CategoryVacancy>,  // Now using categories with vacancies
    val main_category_id: String,
    val sub_categories: List<SubCategoryVacancy>, // Now using sub_categories with vacancies
    val key_responsibilities: String,
    val education_id: Int,
    val experience: String,
    val qualification: String,
    val company_description: String,
    val job_expiry_date: String,
    val job_post_image: String,
    val salary_amount: String
)

data class CategoryVacancy(
    val category_id: Int,
    val vacancies: Int
)

data class SubCategoryVacancy(
    val sub_category_id: Int,
    val vacancies: Int
)
