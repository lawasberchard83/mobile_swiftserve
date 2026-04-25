package com.swiftserve.app.feature.dashboard

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.DashboardResponse
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

        RetrofitClient.instance.getDashboard(token).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                view?.hideLoading()
                when {
                    response.isSuccessful -> {
                        view?.showDashboardData(response.body())
                    }
                    response.code() == 401 -> {
                        view?.navigateToLogin()
                    }
                    else -> {
                        view?.loadFromSession()
                        view?.showError(NetworkUtils.parseError(response))
                    }
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                view?.hideLoading()
                view?.loadFromSession()
                view?.showError("Network error. Showing cached data.")
            }
        })
    }

    override fun onDestroy() {
        view = null
    }
}
