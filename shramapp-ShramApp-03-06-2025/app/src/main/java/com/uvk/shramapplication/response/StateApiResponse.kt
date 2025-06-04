package com.uvk.shramapplication.response

data class StateApiResponse(
    val code: Int,
    val status: String,
    val data: List<StateData>
)

data class StateData(
    val id: Int,
    val state_name: String,
    val state_code: String
)