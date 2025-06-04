package com.uvk.shramapplication.ui.home

data class HomeJobResponse(
    val code: Int,
    val status: String,
    val data: List<Job>,
    val message: String
)

data class Job(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val aadhar_no: String,
    val company_name: String,
    val image: String,
    val salary_range: String,
    val job_types: JobTypes,
    val categories: Categories,
    val sub_categories: SubCategories
)

data class JobTypes(
    val job_type_ids: List<Int>,
    val job_type_names: List<String>
)

data class Categories(
    val category_ids: List<Int>,
    val category_names: List<String>
)

data class SubCategories(
    val sub_category_ids: List<Int>,
    val sub_category_names: List<String>
)

