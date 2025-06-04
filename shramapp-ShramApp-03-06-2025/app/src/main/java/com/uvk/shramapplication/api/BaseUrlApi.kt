package com.uvk.shramapplication.api

import retrofit2.http.GET

interface BaseUrlApi {
    //@GET("shramkart/shramkartbaseurl.php")
    @GET("shramkart.php")
    suspend fun getBaseUrl(): BaseUrlResponse
}
