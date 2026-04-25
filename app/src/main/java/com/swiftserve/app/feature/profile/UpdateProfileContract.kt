package com.swiftserve.app.feature.profile

import com.swiftserve.app.core.model.UpdateProfileRequest
import okhttp3.MultipartBody

interface UpdateProfileContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSuccess(message: String)
        fun showError(message: String)
        fun saveUserData(name: String, email: String, phone: String, address: String, photoUrl: String?)
    }

    interface Presenter {
        fun updateProfile(token: String, name: String, email: String, phone: String, address: String, photoPart: MultipartBody.Part?)
        fun onDestroy()
    }
}
