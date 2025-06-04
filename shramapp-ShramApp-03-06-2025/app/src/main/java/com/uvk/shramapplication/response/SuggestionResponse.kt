package com.uvk.shramapplication.response



data class SuggestionResponse(
    val data: List<SuggestionData>,
    val status: String,
    val code: String
)

data class SuggestionData (
    val id: Int,
    val name: String,
    val type: String
)


