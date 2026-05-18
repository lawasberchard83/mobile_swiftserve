package com.swiftserve.app.core.api

import com.swiftserve.app.core.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // ─── Auth ─────────────────────────────────────────────────────────────────
    // Supabase PostgREST: query users table directly
    // Login: match by username (or email) and password
    @GET("users")
    fun login(
        @Query("username") usernameFilter: String,    // format: "eq.<value>"
        @Query("password") passwordFilter: String,    // format: "eq.<value>"
        @Query("select") select: String = "*",
        @Header("Range") range: String = "0-0"
    ): Call<List<SupabaseUser>>

    // Register: insert a new user row
    @POST("users")
    fun register(@Body request: RegisterRequest): Call<List<SupabaseUser>>

    // ─── Profile ──────────────────────────────────────────────────────────────
    // Get user by id
    @GET("users")
    fun getProfile(
        @Query("id") idFilter: String,               // format: "eq.<id>"
        @Query("select") select: String = "*"
    ): Call<List<SupabaseUser>>

    // Update user profile
    @PATCH("users")
    fun updateProfile(
        @Query("id") idFilter: String,               // format: "eq.<id>"
        @Body request: UpdateProfileRequest
    ): Call<List<SupabaseUser>>

    // ─── Change Password ──────────────────────────────────────────────────────
    @PATCH("users")
    fun changePassword(
        @Query("id") idFilter: String,               // format: "eq.<id>"
        @Body request: ChangePasswordRequest
    ): Call<List<SupabaseUser>>

    // ─── Products ─────────────────────────────────────────────────────────────
    @GET("products")
    fun getProducts(
        @Query("select") select: String = "*",
        @Query("order") order: String = "id.asc"
    ): Call<List<Product>>

    // ─── Orders ───────────────────────────────────────────────────────────────
    @GET("orders")
    fun getOrders(
        @Query("user_id") userIdFilter: String,      // format: "eq.<id>"
        @Query("select") select: String = "*",
        @Query("order") order: String = "id.desc"
    ): Call<List<Order>>

    @POST("orders")
    fun createOrder(@Body request: CreateOrderRequest): Call<List<Order>>

    // ─── Cart Items ───────────────────────────────────────────────────────────
    @GET("cart_items")
    fun getCartItems(
        @Query("user_id") userIdFilter: String,      // format: "eq.<id>"
        @Query("select") select: String = "*"
    ): Call<List<CartItem>>

    @POST("cart_items")
    fun addCartItem(@Body request: CartItem): Call<List<CartItem>>

    @DELETE("cart_items")
    fun removeCartItem(
        @Query("id") idFilter: String               // format: "eq.<id>"
    ): Call<Unit>

    // ─── Order Items ──────────────────────────────────────────────────────────
    @GET("order_items")
    fun getOrderItems(
        @Query("order_id") orderIdFilter: String,   // format: "eq.<id>"
        @Query("select") select: String = "*"
    ): Call<List<OrderItem>>
}
