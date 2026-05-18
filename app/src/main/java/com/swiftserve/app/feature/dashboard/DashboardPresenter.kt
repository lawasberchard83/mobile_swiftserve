package com.swiftserve.app.feature.dashboard

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.DashboardResponse
import com.swiftserve.app.core.model.DashboardStats
import com.swiftserve.app.core.model.SupabaseUser
import com.swiftserve.app.core.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardPresenter(private var view: DashboardContract.View?) : DashboardContract.Presenter {
    override fun loadDashboard(token: String) {
        if (token.isBlank() || token == "Bearer null") {
            view?.navigateToLogin()
            return
        }

        view?.showLoading()

        // Extract user ID from session token (format: "supabase_user_<id>")
        val userId = token.removePrefix("supabase_user_").toIntOrNull()
            ?: run {
                view?.loadFromSession()
                view?.hideLoading()
                return
            }

        // Query Supabase for the user profile
        RetrofitClient.instance.getProfile(
            idFilter = "eq.$userId"
        ).enqueue(object : Callback<List<SupabaseUser>> {
            override fun onResponse(call: Call<List<SupabaseUser>>, response: Response<List<SupabaseUser>>) {
                view?.hideLoading()
                if (response.isSuccessful) {
                    val user = response.body()?.firstOrNull()
                    val dashboardResponse = DashboardResponse(
                        message = "Welcome back, ${user?.fullName ?: user?.username}!",
                        user    = user?.toUserData(),
                        stats   = DashboardStats()
                    )
                    view?.showDashboardData(dashboardResponse)
                } else if (response.code() == 401) {
                    view?.navigateToLogin()
                } else {
                    view?.loadFromSession()
                    view?.showError(NetworkUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<List<SupabaseUser>>, t: Throwable) {
                view?.hideLoading()
                view?.loadFromSession()
                view?.showError("Network error. Showing cached data.")
            }
        })
    }

    override fun loadProducts() {
        RetrofitClient.instance.getProducts().enqueue(object : Callback<List<com.swiftserve.app.core.model.Product>> {
            override fun onResponse(call: Call<List<com.swiftserve.app.core.model.Product>>, response: Response<List<com.swiftserve.app.core.model.Product>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    view?.showProducts(products)
                } else {
                    view?.showError("Failed to load products")
                }
            }

            override fun onFailure(call: Call<List<com.swiftserve.app.core.model.Product>>, t: Throwable) {
                view?.showError("Network error loading products")
            }
        })
    }

    override fun onDestroy() {
        view = null
    }
}
