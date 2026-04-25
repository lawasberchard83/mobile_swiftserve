package com.swiftserve.app.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import com.swiftserve.app.core.model.ErrorResponse
import retrofit2.Response

object NetworkUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun <T> parseError(response: Response<T>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            when {
                errorResponse?.errors != null -> {
                    errorResponse.errors.values.flatten().firstOrNull()
                        ?: "Validation error occurred"
                }
                errorResponse?.message != null -> errorResponse.message
                else -> getDefaultErrorMessage(response.code())
            }
        } catch (e: Exception) {
            getDefaultErrorMessage(response.code())
        }
    }

    fun getDefaultErrorMessage(code: Int): String = when (code) {
        400 -> "Bad request. Please check your input."
        401 -> "Unauthorized. Please login again."
        403 -> "Access forbidden."
        404 -> "Resource not found."
        422 -> "Validation failed. Please check your input."
        500 -> "Server error. Please try again later."
        503 -> "Service unavailable. Please try again later."
        else -> "Something went wrong (Error $code)"
    }
}
