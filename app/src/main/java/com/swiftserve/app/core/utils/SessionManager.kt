package com.swiftserve.app.core.utils

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private const val PREF_NAME = "SwiftServeSession"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_ADDRESS = "user_address"
    private const val KEY_USER_PHOTO = "user_photo"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(context: Context, token: String) {
        prefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? =
        prefs(context).getString(KEY_TOKEN, null)

    fun getBearerToken(context: Context): String =
        "Bearer ${getToken(context)}"

    fun saveUserData(
        context: Context,
        id: Int? = null,
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        address: String? = null,
        photo: String? = null
    ) {
        prefs(context).edit().apply {
            id?.let { putInt(KEY_USER_ID, it) }
            name?.let { putString(KEY_USER_NAME, it) }
            email?.let { putString(KEY_USER_EMAIL, it) }
            phone?.let { putString(KEY_USER_PHONE, it) }
            address?.let { putString(KEY_USER_ADDRESS, it) }
            photo?.let { putString(KEY_USER_PHOTO, it) }
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUserName(context: Context): String? =
        prefs(context).getString(KEY_USER_NAME, null)

    fun getUserEmail(context: Context): String? =
        prefs(context).getString(KEY_USER_EMAIL, null)

    fun getUserPhone(context: Context): String? =
        prefs(context).getString(KEY_USER_PHONE, null)

    fun getUserAddress(context: Context): String? =
        prefs(context).getString(KEY_USER_ADDRESS, null)

    fun getUserPhoto(context: Context): String? =
        prefs(context).getString(KEY_USER_PHOTO, null)

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearSession(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
