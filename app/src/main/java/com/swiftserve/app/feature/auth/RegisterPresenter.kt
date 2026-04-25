package com.swiftserve.app.feature.auth

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.RegisterRequest
import com.swiftserve.app.core.model.RegisterResponse
import com.swiftserve.app.core.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPresenter(private var view: RegisterContract.View?) : RegisterContract.Presenter {
    override fun performRegister(request: RegisterRequest) {
        view?.showLoading()
        RetrofitClient.instance.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                view?.hideLoading()
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Registration successful!"
                    view?.navigateToLogin(message)
                } else {
                    view?.showError(NetworkUtils.parseError(response))
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                view?.hideLoading()
                view?.showError("Network error: ${t.message ?: "Please check your connection."}")
            }
        })
    }
    override fun onDestroy() {
        view = null
    }
}
