package com.uvk.shramapplication.ui.form


// Data structure to store selected data
data class SelectedCatData(
    val mainCategoryId: Int,
    val categories: List<CategorySelection1>
)

data class CategorySelection1(
    val categoryId: Int,
    val subCategoryIds: List<Int>
)

// Model for SubCategory
data class SubCategory1(
    val subCategoryId: Int,
    val subCategoryName: String
)

// Model for Category
data class Category1(
    val categoryId: Int,
    val categoryName: String,
    val subCategories: List<SubCategory1>
)

// Model for MainCategory
data class MainCategory1(
    val mainCategoryId: Int,
    val mainCategoryName: String,
    val categories: List<Category1>
)


data class SubcategoryModel(
    val subCategoryId: Int,
    val subCategoryName: String
)

data class CategoryModel(
    val categoryId: Int,
    val categoryName: String,
    val subCategories: List<SubcategoryModel>
)

data class MainCategoryModel(
    val mainCategoryId: Int,
    val mainCategoryName: String,
    val categories: List<CategoryModel>
)




