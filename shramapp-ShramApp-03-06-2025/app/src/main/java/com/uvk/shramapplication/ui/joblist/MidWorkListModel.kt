package com.uvk.shramapplication.ui.joblist


data class MidWorkListModel(
    val title: String,
    val companyName: String,
    val vacancy: String,
    val salary: String,
    val location: String,
    val qualification : String,
    val experiance : String,
    val time : String,
    val phoneNumber: String
)

data class JobListResp(
    val code: Int,
    val status: String,
    val data: List<JobData>,
    val message: String
)

data class JobData (

    val id: String,
    val title: String,
    val job_type_id: String,
    val description: String,
    val location: String,
    val aadhar_no: String,
    val company_name : String,
    val image : String,
    val salary_range : String,
    val category_id: String,
    val sub_category_id: String,
    val category_name: String,
    val sub_category_name: String,
    val job_type: String
)


data class JobListResponse(
    val code: Int,
    val status: String,
    val data: List<Job>,
    val message: String
)

data class Job(
    val id: String,
    val user_id: String,
    val user_name: String,
    val name: String,
    val profile_image: String,
    val save_job_id: String,
    val title: String,
    val description: String,
    val location: String,
    val created_date: String,
    val aadhar_no: String,
    val mobile_no: String,
    val company_name: String,
    val gender: String,
    val education: String,
    val experience: String,
    val image: String,
    val salary_range: String,
    val salary: String,
    val salary_amount: String,
    val salary_type: String,
    val key_responsibilities: String,
    val qualification: String,
    val company_description: String,
    val main_category_name: String,
    val apply_status: String,
    val save_status: String,
    val job_request_status: String,
    val job_request_date: String,
    val jab_request_id: String,
    val job_id: String,
    val save_date: String,
    val apply_date: String,
    val job_types: List<JobType>,
    val categories: List<Category>,
    val sub_categories: List<SubCategory>
)

data class JobType(
    val job_type_ids: Int,
    val id: Int,
    val job_type_names: String,
    val job_type: String
)

data class Category(
    val category_ids: Int,
    val vacancies: Int,
    val category_names: String
)

data class SubCategory(
    val vacancies: Int,
    val sub_category_ids: Int,
    val sub_category_names: String
)



