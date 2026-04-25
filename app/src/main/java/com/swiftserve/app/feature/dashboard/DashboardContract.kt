package com.swiftserve.app.feature.dashboard

import com.swiftserve.app.core.model.DashboardResponse

interface DashboardContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showDashboardData(body: DashboardResponse?)
        fun navigateToLogin()
        fun showError(message: String)
        fun loadFromSession()
    }
    interface Presenter {
        fun loadDashboard(token: String)
        fun onDestroy()
    }
}
