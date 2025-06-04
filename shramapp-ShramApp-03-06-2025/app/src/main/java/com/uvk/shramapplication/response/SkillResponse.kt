package com.uvk.shramapplication.response

data class SkillResponse(

    val code: Int,
    val status: Boolean,
    val message: String,
    val data: List<SkillData>
)

data class SkillData(
    val id: Int,
    val training_partner_name: String,
    val training_center_name: String,
    val training_center_address: String,
    val district: String,
    val state: String,
    val sector_name: String,
    val skill_image: String
)
