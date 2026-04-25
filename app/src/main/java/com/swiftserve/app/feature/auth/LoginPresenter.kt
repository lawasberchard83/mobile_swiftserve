package com.swiftserve.app.feature.auth

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.LoginRequest
import com.swiftserve.app.core.model.LoginResponse
import com.swiftserve.app.core.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPresenter(private var view: LoginContract.View?) : LoginContract.Presenter {
    override fun performLogin(email: String, pass: String) {
        view?.showLoading()
        val request = LoginRequest(email, pass)
        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                view?.hideLoading()
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.token
                    if (!token.isNullOrEmpty()) {
                        body.user?.let { user ->
                            view?.saveUserData(token, user)
                        }
                        view?.navigateToDashboard()
                    } else {
                        view?.showError(body?.message ?: "Login failed.")
                    }
                } else {
                    view?.showError(NetworkUtils.parseError(response))
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                view?.hideLoading()
                view?.showError("Network error: ${t.message ?: "Check connection."}")
            }
        })
    }
    override fun onDestroy() {
        view = null
    }
}
