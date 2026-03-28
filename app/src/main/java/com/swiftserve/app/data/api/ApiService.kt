package com.swiftserve.app.data.api

import com.swiftserve.app.data.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // ─── Auth ───────────────────────────────────────────

    @POST("api/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/logout")
    fun logout(@Header("Authorization") token: String): Call<Map<String, String>>

    // ─── Dashboard ──────────────────────────────────────

    @GET("api/dashboard")
    fun getDashboard(@Header("Authorization") token: String): Call<DashboardResponse>

    // ─── Profile ────────────────────────────────────────

    @GET("api/profile")
    fun getProfile(@Header("Authorization") token: String): Call<ProfileResponse>

    @PUT("api/profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Call<UpdateProfileResponse>

    @Multipart
    @POST("api/profile/photo")
    fun uploadPhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ): Call<UpdateProfileResponse>

    // ─── Change Password ────────────────────────────────

    @PUT("api/change-password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Call<ChangePasswordResponse>
}
