package com.swiftserve.app.feature.profile

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.UpdateProfileRequest
import com.swiftserve.app.core.model.UpdateProfileResponse
import com.swiftserve.app.core.utils.NetworkUtils
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfilePresenter(private var view: UpdateProfileContract.View?) : UpdateProfileContract.Presenter {
    override fun updateProfile(token: String, name: String, email: String, phone: String, address: String, photoPart: MultipartBody.Part?) {
        view?.showLoading()
        if (photoPart != null) {
            RetrofitClient.instance.uploadPhoto(token, photoPart).enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    val url = if (response.isSuccessful) response.body()?.user?.photo else null
                    updateProfileOnly(token, name, email, phone, address, url)
                }
                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    updateProfileOnly(token, name, email, phone, address, null)
                }
            })
        } else {
            updateProfileOnly(token, name, email, phone, address, null)
        }
    }

    private fun updateProfileOnly(token: String, name: String, email: String, phone: String, address: String, newPhotoUrl: String?) {
        val request = UpdateProfileRequest(name, email, phone.ifEmpty { null }, address.ifEmpty { null })
        RetrofitClient.instance.updateProfile(token, request).enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                view?.hideLoading()
                if (response.isSuccessful) {
                    val user = response.body()?.user
                    view?.saveUserData(
                        user?.name ?: name,
                        user?.email ?: email,
                        user?.phone ?: phone,
                        user?.address ?: address,
                        newPhotoUrl ?: user?.photo
                    )
                    view?.showSuccess("Profile updated successfully!")
                } else {
                    view?.showError(NetworkUtils.parseError(response))
                }
            }
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                view?.hideLoading()
                view?.showError("Network error: ${t.message}")
            }
        })
    }
    
    override fun onDestroy() {
        view = null
    }
}
