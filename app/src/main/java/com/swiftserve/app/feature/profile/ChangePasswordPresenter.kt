package com.swiftserve.app.feature.profile

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.ChangePasswordRequest
import com.swiftserve.app.core.model.ChangePasswordResponse
import com.swiftserve.app.core.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordPresenter(private var view: ChangePasswordContract.View?) : ChangePasswordContract.Presenter {
    override fun changePassword(token: String, request: ChangePasswordRequest) {
        view?.showLoading()
        RetrofitClient.instance.changePassword(token, request)
            .enqueue(object : Callback<ChangePasswordResponse> {
                override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                    view?.hideLoading()
                    if (response.isSuccessful) {
                        view?.showSuccess(response.body()?.message ?: "Password changed successfully!")
                    } else {
                        when (response.code()) {
                            401 -> view?.showError("Current password is incorrect.")
                            else -> view?.showError(NetworkUtils.parseError(response))
                        }
                    }
                }
                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    view?.hideLoading()
                    view?.showError("Network error: ${t.message ?: "Please check your connection."}")
                }
            })
    }

    override fun onDestroy() {
        view = null
    }
}
