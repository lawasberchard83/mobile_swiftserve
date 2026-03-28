package com.swiftserve.app.data.model

import com.google.gson.annotations.SerializedName

// ─── Auth Models ───────────────────────────────────────

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("token") val token: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: UserData? = null
)

data class RegisterResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: UserData? = null
)

// ─── User Models ───────────────────────────────────────

data class UserData(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class ProfileResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: UserData? = null
)

// ─── Update Profile Models ─────────────────────────────

data class UpdateProfileRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address") val address: String? = null
)

data class UpdateProfileResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: UserData? = null
)

// ─── Change Password Models ────────────────────────────

data class ChangePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String,
    @SerializedName("new_password_confirmation") val newPasswordConfirmation: String
)

data class ChangePasswordResponse(
    @SerializedName("message") val message: String? = null
)

// ─── Error Model ───────────────────────────────────────

data class ErrorResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("errors") val errors: Map<String, List<String>>? = null
)

// ─── Dashboard Model ───────────────────────────────────

data class DashboardResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: UserData? = null,
    @SerializedName("stats") val stats: DashboardStats? = null
)

data class DashboardStats(
    @SerializedName("total_orders") val totalOrders: Int? = 0,
    @SerializedName("pending_orders") val pendingOrders: Int? = 0,
    @SerializedName("completed_orders") val completedOrders: Int? = 0
)
