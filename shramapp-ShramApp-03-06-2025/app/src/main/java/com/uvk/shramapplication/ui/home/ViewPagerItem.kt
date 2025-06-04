package com.uvk.shramapplication.ui.home

data class ViewPagerItemGS(
    val imageResId: Int,
    val name: String,
    val title: String,
    val description: String,
    val location: String,
    val salary: String
)


data class ViewPagerItem(
    val imageResId: Int,
    val title: String,
    val description: String,
    val location: String
)