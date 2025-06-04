package com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list



data class JobResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: List<JobItem>
)

data class JobItem(
    val id: Int,
    val job_title: String,
    val job_id: String,
    val employer_id: Int,
    val employer_name: String,
    val user_name: String,
    val title: String,
    val name: String,
    val mobile_no: String,
    val address: String,
    val skill: String,
    val job_request_date: String,
    val apply_date: String,
    val email: String,
    val profile_image: String,
    val company_name: String,
    val description: String,
    val empdata: List<EmployeeData>,
    val available_status:String,
    val selected_status:String,
    val designation:String,
    val select_status:String,
    val location:String,
    val salary:String,
    val salary_type:String,
    val created_at:String,
    val job_types: List<JobType>,
    val categories: List<Category>,
    val sub_categories: List<SubCategory>

)

data class EmployeeData(
    val job_request_id: String?,
    val id: String?,
    val job_id: Int?,
    val job_request_status: String?,
    val job_request_date: String?,
    val status_date: String?,
    val user_id: Int,
    val apply_id: String,
    val address: String,
    val profile_image: String,
    val designation: String,
    val user_name: String,
    val mobile_no: String,
    val select_status: String?,
    val employer_select_status: String?,
    val apply_status: String?,
    val apply_date: String?
)


data class UpdateStatusRequest(
    val token:String,
    val employer_ids: List<Int>,
    val employee_id: String,
    val job_ids: List<Int>
)

data class UpdateStatusResponse(
    val code: Int,
    val status: String,
    val message: String
)

data class JobType(
    val job_type_ids: Int,
    val job_type_names: String
)

data class Category(
    val category_ids: Int,
    val category_names: String,
    val name: String,
)

data class SubCategory(
    val sub_category_ids: Int,
    val sub_category_names: String,
    val name: String
)

data class EmployeerRequest(
    val token:String,
    val user_id: String,
    val job_id: Int,
    val employee_ids: List<Int>
)