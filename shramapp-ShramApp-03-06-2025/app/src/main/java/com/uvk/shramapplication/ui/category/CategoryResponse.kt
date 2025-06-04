package com.uvk.shramapplication.ui.category


data class CategoryRequest(val main_category_ids: List<Int>)

data class CategoryResponse(
    val code: Int,
    val status: String,
    val data: List<Category>,
    val message: String
)

data class Category(
    val id: Int,
    val category_name: String,
    val main_category_id: Int,
    val main_category_name: String,
    var isSelected: Boolean = false,
    var vacancies: Int //= 0 // Add this
)

data class SelectedCategory(val category_id: Int, val vacancies: Int)

data class SelectedCategoryVacancy(
    val categoryId: Int,
    val categoryName: String,
    var vacancies: Int //= 0
)

