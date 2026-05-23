package com.swiftserve.app.feature.profile

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.SupabaseUser
import com.swiftserve.app.core.model.UpdateProfileRequest
import com.swiftserve.app.core.utils.NetworkUtils
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfilePresenter(private var view: UpdateProfileContract.View?) : UpdateProfileContract.Presenter {
    override fun updateProfile(token: String, name: String, email: String, phone: String, address: String, photoPart: MultipartBody.Part?) {
        view?.showLoading()
        // Photo upload not supported directly via Supabase REST — skip and update profile fields
        updateProfileOnly(token, name, email, phone, address)
    }

    private fun updateProfileOnly(token: String, name: String, email: String, phone: String, address: String) {
        val userId = token.substringAfter("supabase_user_").toIntOrNull()
            ?: run {
                view?.hideLoading()
                view?.showError("Session expired. Please login again.")
                return
            }

        val request = UpdateProfileRequest(
            fullName = name.ifEmpty { null },
            email    = email.ifEmpty { null },
            phone    = phone.ifEmpty { null },
            address  = address.ifEmpty { null }
        )

        RetrofitClient.instance.updateProfile(
            idFilter = "eq.$userId",
            request  = request
        ).enqueue(object : Callback<List<SupabaseUser>> {
            override fun onResponse(call: Call<List<SupabaseUser>>, response: Response<List<SupabaseUser>>) {
                view?.hideLoading()
                if (response.isSuccessful) {
                    val user = response.body()?.firstOrNull()
                    view?.saveUserData(
                        user?.fullName ?: name,
                        user?.email ?: email,
                        user?.phone ?: phone,
                        user?.address ?: address,
                        null
                    )
                    view?.showSuccess("Profile updated successfully!")
                } else {
                    view?.showError(NetworkUtils.parseError(response))
                }
            }
            override fun onFailure(call: Call<List<SupabaseUser>>, t: Throwable) {
                view?.hideLoading()
                view?.showError("Network error: ${t.message}")
            }
        })
    }

    override fun onDestroy() {
        view = null
    }
}
