package com.uvk.shramapplication.helper

data class JobResponse(
    val code: Int,
    val status: String,
    val data: List<JobType>,
    val message: String
)

data class JobType(
    val id: Int,
    val job_type: String
)

data class ExperienceResponse(
    val code: Int,
    val status: String,
    val data: List<Experience>,
    val message: String
)

data class Experience(
    val id: Int,
    val experience: String
)

data class GenderResponse(
    val code: Int,
    val status: String,
    val data: List<GenderType>,
    val message: String
)

data class GenderType(
    val id: Int,
    val gender: String
)

data class EducationResponse(
    val code: Int,
    val status: String,
    val data: List<Education>,
    val message: String
)

data class Education(
    val id: Int,
    val education: String
)


data class SalaryResponse(
    val code: Int,
    val status: String,
    val data: List<SalaryRange>,
    val message: String
)

data class SalaryRange(
    val id: Int,
    val salary: String,
    val salary_type: String
)





