package com.swiftserve.app.feature.auth

import com.swiftserve.app.core.model.UserData

interface LoginContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun navigateToDashboard()
        fun showError(message: String)
        fun saveUserData(token: String, user: UserData)
    }

    interface Presenter {
        fun performLogin(email: String, pass: String)
        fun onDestroy()
    }
}
