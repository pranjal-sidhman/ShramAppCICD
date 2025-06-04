package com.uvk.shramapplication.ui.form

data class WalkThroughResponse(
    val code: Int,
    val status: Boolean,
    val message: String
)

/*data class WalkThroughRequestBody(
    val main_categories: List<Int>,
    val gender_ids: MutableList<Int>,
    val experience: MutableList<Int>,
    val education: MutableList<Int>,
    val salary_range_ids: MutableList<Int>,
    val job_type_ids: MutableList<Int>
)

data class MainCategory(
    val main_category_id: Int,
    val categories: List<Category>
)

data class Category(
    val category_id: Int,
    val sub_categories: List<Int>
)*/

data class WalkThroughRequestBody(
    val main_categories: List<MainCategory>,
    val gender_ids: List<Int>,
    val experience: Int,
    val education: Int,
    val salary_range_ids: List<Int>,
    val job_type_ids: List<Int>
)

data class MainCategory(
    val main_category_id: Int,
    val categories: List<Category>
)

data class Category(
    val category_id: Int,
    val sub_categories: List<Int>
)


// Data models for MainCategory, Category, and Subcategory
data class SubCategory(
    val id: Int
)
/*data class Category(
    val id: Int,
    val subCategories: List<SubCategory>
)
data class MainCategory(
    val id: Int,
    val categories: List<Category>
)*/


