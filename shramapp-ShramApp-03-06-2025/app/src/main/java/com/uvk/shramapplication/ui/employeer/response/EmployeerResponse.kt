package com.uvk.shramapplication.ui.employeer.response

data class EmployeerResponse(
    val code:String,
    val status:String,
    val count:String,
    val message:String,
    val data:List<EmployeerData>
)

data class EmployeerData (
    val id:Int,
    val user_id:Int,
    val available_status:String,
    val selected_status:String,
    val user_name:String,
    val mobile_no:String,
    val email:String,
    val skill:String,
    val designation:String,
    val address:String,
    val profile_image:String,
    val image:String,
    val job_request_date:String,
    val title:String,
    val description:String,
    val location:String,
    val name:String,
    val company_name:String,
    val salary_amount:String,
    val salary:String,
    val salary_type:String,
    val created_at:String,
    val job_types: List<JobType>,
    val categories: List<Category>,
    val sub_categories: List<SubCategory>,

    val job_title:String,
    val job_id:String,
    val empdata:List<EmpData>


)

data class EmpData (
    val job_request_id: String?,
    val job_id: String?,
    val job_request_status: String?,
    val job_request_date: String?,
    val status_date: String?,
    val user_id: String,
    val user_name: String,
    val mobile_no: String,
    val email: String,
    val select_status: String?,
    val apply_status: String?,
    val apply_date: String?

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