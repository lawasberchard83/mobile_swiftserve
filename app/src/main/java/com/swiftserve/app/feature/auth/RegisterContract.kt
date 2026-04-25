package com.swiftserve.app.feature.auth

import com.swiftserve.app.core.model.RegisterRequest

interface RegisterContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun navigateToLogin(message: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun performRegister(request: RegisterRequest)
        fun onDestroy()
    }
}
