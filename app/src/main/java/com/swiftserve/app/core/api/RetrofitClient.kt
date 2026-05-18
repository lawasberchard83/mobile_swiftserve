package com.swiftserve.app.core.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ── Supabase project credentials ────────────────────────────────────────
    const val SUPABASE_URL  = "https://vboovedckhmcddoseufl.supabase.co"
    const val SUPABASE_KEY  = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZib292ZWRja2htY2Rkb3NldWZsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI4MTczODQsImV4cCI6MjA4ODM5MzM4NH0.xgDd7FFqOW-D3ZsOOa6gXumQvzRvoYV04c-t7bLsvm0"

    private const val BASE_URL = "$SUPABASE_URL/rest/v1/"

    // ── Attach required Supabase headers to every request ───────────────────
    private val supabaseHeaderInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "return=representation")
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(supabaseHeaderInterceptor)
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
