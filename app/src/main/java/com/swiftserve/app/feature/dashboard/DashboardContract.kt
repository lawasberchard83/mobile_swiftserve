package com.swiftserve.app.feature.dashboard

import com.swiftserve.app.core.model.DashboardResponse

interface DashboardContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showDashboardData(body: DashboardResponse?)
        fun showProducts(products: List<com.swiftserve.app.core.model.Product>)
        fun navigateToLogin()
        fun showError(message: String)
        fun loadFromSession()
    }
    interface Presenter {
        fun loadDashboard(token: String)
        fun loadProducts()
        fun onDestroy()
    }
}
