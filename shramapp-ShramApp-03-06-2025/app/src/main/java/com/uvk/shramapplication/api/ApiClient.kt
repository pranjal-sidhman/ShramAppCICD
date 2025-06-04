package com.uvk.shramapplication.api

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var retrofit: Retrofit? = null

    suspend fun init(): Boolean {
        val fetchedBaseUrl = BaseUrlProvider.fetchBaseUrl()
        Log.d("ApiClient", "Base URL: $fetchedBaseUrl")
        return if (!fetchedBaseUrl.isNullOrEmpty()) {
            retrofit = Retrofit.Builder()
                .baseUrl(fetchedBaseUrl)
                .client(
                    OkHttpClient.Builder()
                        .dns(Dns.SYSTEM) //  Add system DNS resolution
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build()
                )
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            true
        } else {
            false
        }
    }
    val apiService: ApiInterface?
        get() = retrofit?.create(ApiInterface::class.java)

   // val apiService: ApiInterface get() = retrofit!!.create(ApiInterface::class.java)

  /*  // Base URL for the API
    private val BASE_URL = "https://callisol.com/Shram/AppController/"
    //private val BASE_URL = "https://mahindramdrona.com/Shram/AppController/"

    // Setting up HTTP logging interceptor
    private val mHttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    // OkHttpClient with interceptor
   *//* private val mOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(mHttpLoggingInterceptor)
        .build()*//*
    private val mOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(mHttpLoggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)  // Timeout for establishing a connection
        .readTimeout(30, TimeUnit.SECONDS)     // Timeout for reading from the server
        .writeTimeout(30, TimeUnit.SECONDS)    // Timeout for writing to the server
        .build()


    // Gson instance for converting the response
    private val gson = GsonBuilder()
        .setLenient()  // Lenient parsing in case the response is not strict
        .create()

    // Retrofit instance (singleton)
    private var mRetrofit: Retrofit? = null

    // The client getter property
    val client: Retrofit?
        get() {
            // Only initialize if mRetrofit is null
            if (mRetrofit == null) {
                // Build the Retrofit instance
                mRetrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(mOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }

            // Log details for debugging
          //  Log.d(TAG, "Base URL: ${mRetrofit?.baseUrl()}")
          //  Log.d(TAG, "OkHttpClient: $mOkHttpClient")
         //   Log.d(TAG, "Gson: $gson")

            // Return the Retrofit instance
            return mRetrofit
        }

    // Tag for logging
    private const val TAG = "ApiClient"

    val apiService: ApiInterface = client!!.create(ApiInterface::class.java)*/
}

