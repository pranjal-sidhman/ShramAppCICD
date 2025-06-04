package com.uvk.shramapplication.ui.main_category




data class MainCategoryResponse(
    val code: Int,
    val status: String,
    val data: List<MainCategory>,
    val message: String
)

data class MainCategory(
    val id: Int,
    val name: String,
    var isSelected: Boolean = false
)


