package com.swiftserve.app.feature.profile

import com.swiftserve.app.core.model.ProfileResponse

interface ProfileContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showProfileData(body: ProfileResponse?)
        fun navigateToLogin()
        fun showError(message: String)
        fun loadFromSession()
    }
    interface Presenter {
        fun loadProfile(token: String)
        fun logout(token: String)
        fun onDestroy()
    }
}
