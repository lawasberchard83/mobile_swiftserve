package com.swiftserve.app.core.model

import com.google.gson.annotations.SerializedName

// ─── Supabase Table: users ─────────────────────────────────────────────────
// Matches columns: id, username, password, full_name, email, phone, address

data class SupabaseUser(
    @SerializedName("id")        val id: Int? = null,
    @SerializedName("username")  val username: String? = null,
    @SerializedName("password")  val password: String? = null,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("email")     val email: String? = null,
    @SerializedName("phone")     val phone: String? = null,
    @SerializedName("address")   val address: String? = null
) {
    /** Convert to the app-wide UserData model */
    fun toUserData(): UserData = UserData(
        id       = id,
        name     = fullName,
        username = username,
        email    = email,
        phone    = phone,
        address  = address
    )
}

// ─── Auth Request Models ───────────────────────────────────────────────────

data class RegisterRequest(
    @SerializedName("username")  val username: String,
    @SerializedName("password")  val password: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email")     val email: String,
    @SerializedName("phone")     val phone: String? = null,
    @SerializedName("address")   val address: String? = null
)

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

// ─── Legacy Response Wrappers (kept for presenter compatibility) ───────────

data class LoginResponse(
    @SerializedName("token")   val token: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("user")    val user: UserData? = null
)

data class RegisterResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("user")    val user: UserData? = null
)

// ─── User Models ───────────────────────────────────────────────────────────

data class UserData(
    @SerializedName("id")        val id: Int? = null,
    @SerializedName("name")      val name: String? = null,
    @SerializedName("username")  val username: String? = null,
    @SerializedName("email")     val email: String? = null,
    @SerializedName("phone")     val phone: String? = null,
    @SerializedName("address")   val address: String? = null,
    @SerializedName("photo")     val photo: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class ProfileResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("user")    val user: UserData? = null
)

// ─── Update Profile Models ─────────────────────────────────────────────────

data class UpdateProfileRequest(
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("email")     val email: String? = null,
    @SerializedName("phone")     val phone: String? = null,
    @SerializedName("address")   val address: String? = null
)

data class UpdateProfileResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("user")    val user: UserData? = null
)

// ─── Change Password Models ────────────────────────────────────────────────

data class ChangePasswordRequest(
    @SerializedName("password") val password: String
)

data class ChangePasswordResponse(
    @SerializedName("message") val message: String? = null
)

// ─── Error Model ───────────────────────────────────────────────────────────

data class ErrorResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("hint")    val hint: String? = null,
    @SerializedName("details") val details: String? = null,
    @SerializedName("errors")  val errors: Map<String, List<String>>? = null
)

// ─── Dashboard Model (assembled from Supabase queries) ─────────────────────

data class DashboardResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("user")    val user: UserData? = null,
    @SerializedName("stats")   val stats: DashboardStats? = null
)

data class DashboardStats(
    @SerializedName("total_orders")     val totalOrders: Int? = 0,
    @SerializedName("pending_orders")   val pendingOrders: Int? = 0,
    @SerializedName("completed_orders") val completedOrders: Int? = 0
)

// ─── Supabase Table: products ──────────────────────────────────────────────

data class Product(
    @SerializedName("id")          val id: Int? = null,
    @SerializedName("name")        val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("price")       val price: Double? = null,
    @SerializedName("image_url")   val imageUrl: String? = null,
    @SerializedName("category")    val category: String? = null,
    @SerializedName("stock")       val stock: Int? = null
)

// ─── Supabase Table: orders ────────────────────────────────────────────────

data class Order(
    @SerializedName("id")          val id: Int? = null,
    @SerializedName("user_id")     val userId: Int? = null,
    @SerializedName("status")      val status: String? = null,
    @SerializedName("total")       val total: Double? = null,
    @SerializedName("created_at")  val createdAt: String? = null
)

data class CreateOrderRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("status")  val status: String = "pending",
    @SerializedName("total")   val total: Double
)

// ─── Supabase Table: cart_items ────────────────────────────────────────────

data class CartItem(
    @SerializedName("id")         val id: Int? = null,
    @SerializedName("user_id")    val userId: Int? = null,
    @SerializedName("product_id") val productId: Int? = null,
    @SerializedName("quantity")   val quantity: Int? = null
)

// ─── Supabase Table: order_items ───────────────────────────────────────────

data class OrderItem(
    @SerializedName("id")         val id: Int? = null,
    @SerializedName("order_id")   val orderId: Int? = null,
    @SerializedName("product_id") val productId: Int? = null,
    @SerializedName("quantity")   val quantity: Int? = null,
    @SerializedName("price")      val price: Double? = null
)
