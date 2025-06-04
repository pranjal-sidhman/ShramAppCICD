package com.uvk.shramapplication.api

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BaseUrlProvider {

    //private const val FIXED_BASE_URL = "https://sidhman.com/"
    private const val FIXED_BASE_URL = "https://mahindramdrona.com"

   /* suspend fun fetchBaseUrl(): String? {
        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl(FIXED_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(BaseUrlApi::class.java)
            val response = service.getBaseUrl()
            Log.d("TAG", "Base URL response: $response")
            response.url
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }*/
   suspend fun fetchBaseUrl(): String? {
       return try {
           val retrofit = Retrofit.Builder()
               .baseUrl(FIXED_BASE_URL)
               .addConverterFactory(GsonConverterFactory.create())
               .build()

           val service = retrofit.create(BaseUrlApi::class.java)
           val response = service.getBaseUrl()

           Log.e("BaseUrlProvider", "Full Response: $response")
           Log.e("BaseUrlProvider", "Parsed URL: ${response.url}")

           response.url
       } catch (e: Exception) {
           Log.e("BaseUrlProvider", "Error fetching base URL: ${e.localizedMessage}")
           Log.e("BaseUrlProvider", "Error fetching base URL", e)
           null
       }
   }

}
