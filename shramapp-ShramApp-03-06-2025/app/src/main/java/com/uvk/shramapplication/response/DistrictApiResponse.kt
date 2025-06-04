package com.uvk.shramapplication.response

data class DistrictApiResponse(
    val data: ArrayList<DistrictData>,
    val status: String,
    val code: String
)

data class DistrictData (
    val id: Int,
    val district_name: String,
    var isSelected: Boolean = false
  /*  val CITY_ID: Int,
    val CITY_NAME: String,
    val STATE_ID: Int,
    var isSelected: Boolean = false*/
)