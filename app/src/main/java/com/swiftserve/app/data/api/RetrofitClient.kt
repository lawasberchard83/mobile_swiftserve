package com.swiftserve.app.data.api

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    // A MOCK INTERCEPTOR TO PRETEND WE HAVE A BACKEND SERVER
    private val mockInterceptor = Interceptor { chain ->
        val uri = chain.request().url.toUri().toString()
        val jsonMediaType = "application/json".toMediaTypeOrNull()
        
        // Simulating internet delay so loading spinners show up nicely
        Thread.sleep(1000)

        val responseJson = when {
            uri.contains("login", ignoreCase = true) -> """
                {
                  "token": "fake-jwt-token-123456789",
                  "message": "Login successful",
                  "user": {
                    "id": 1,
                    "name": "Alex Student",
                    "email": "alex@university.edu",
                    "phone": "+1 (555) 123-4567",
                    "address": "Dorm 4, Room 102"
                  }
                }
            """.trimIndent()
            
            uri.contains("register", ignoreCase = true) -> """
                {
                  "message": "Registration successful",
                  "user": {
                    "id": 2,
                    "name": "New Student",
                    "email": "new@university.edu"
                  }
                }
            """.trimIndent()
            
            else -> """
                {
                  "message": "Success",
                  "user": {
                    "id": 1,
                    "name": "Alex Student",
                    "email": "alex@university.edu",
                    "phone": "+1 (555) 123-4567",
                    "address": "Dorm 4, Room 102"
                  }
                }
            """.trimIndent()
        }

        Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(chain.request())
            .body(responseJson.toResponseBody(jsonMediaType))
            .build()
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(mockInterceptor) // Insert our pretend backend!
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun bearerToken(token: String): String = "Bearer $token"
}
