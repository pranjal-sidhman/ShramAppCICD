package com.uvk.shramapplication.response

data class TalukaResponse(
    val COMPLAINTS_ID: Int,
    val DATA: ArrayList<TalukaData>,
    val ID: Any,
    val ResponseCode: String,
    val ResponseMessage: String
)

data class TalukaData(
    val CITY_ID: Int,
    val STATE_ID: Int,
    val TALUKA_ID: Int,
    val TALUKA_NAME: String
)