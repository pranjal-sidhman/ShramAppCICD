package com.uvk.shramapplication.ui.subcategory



data class SubCategoryRequest(val category_ids: List<Int>)

data class SubCategoryResponse(
    val code: Int,
    val status: String,
    val data: List<SubcategoryModel>,
    val message: String
)
data class SubcategoryModel(
    val id: Int,
    val name: String,
    val category_id: String,
    val category_name: String,
    var isSelected: Boolean = false, // Add this for selection handling
    var vacancies: Int = 0
)


