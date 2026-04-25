package com.swiftserve.app.feature.profile

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.ProfileResponse
import com.swiftserve.app.core.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePresenter(private var view: ProfileContract.View?) : ProfileContract.Presenter {
    override fun loadProfile(token: String) {
        if (token.isBlank() || token == "Bearer null") {
            view?.navigateToLogin()
            return
        }
        view?.showLoading()

        RetrofitClient.instance.getProfile(token).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                view?.hideLoading()

                when {
                    response.isSuccessful -> {
                        view?.showProfileData(response.body())
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

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                view?.hideLoading()
                view?.loadFromSession()
                view?.showError("Network error.")
            }
        })
    }

    override fun logout(token: String) {
        view?.showLoading()
        RetrofitClient.instance.logout(token).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                view?.hideLoading()
                view?.navigateToLogin()
            }
            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                view?.hideLoading()
                view?.navigateToLogin()
            }
        })
    }

    override fun onDestroy() {
        view = null
    }
}
