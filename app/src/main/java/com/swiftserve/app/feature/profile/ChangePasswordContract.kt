package com.swiftserve.app.feature.profile

import com.swiftserve.app.core.model.ChangePasswordRequest

interface ChangePasswordContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSuccess(message: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun changePassword(token: String, request: ChangePasswordRequest)
        fun onDestroy()
    }
}
