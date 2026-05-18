package com.swiftserve.app.feature.auth

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.RegisterRequest
import com.swiftserve.app.core.model.SupabaseUser
import com.swiftserve.app.core.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPresenter(private var view: RegisterContract.View?) : RegisterContract.Presenter {
    override fun performRegister(request: RegisterRequest) {
        view?.showLoading()
        // Supabase PostgREST: POST /users inserts a new row
        RetrofitClient.instance.register(request).enqueue(object : Callback<List<SupabaseUser>> {
            override fun onResponse(call: Call<List<SupabaseUser>>, response: Response<List<SupabaseUser>>) {
                view?.hideLoading()
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    view?.navigateToLogin("Registration successful! Please login.")
                } else if (response.isSuccessful) {
                    view?.navigateToLogin("Registered! Please login.")
                } else {
                    view?.showError(NetworkUtils.parseError(response))
                }
            }
            override fun onFailure(call: Call<List<SupabaseUser>>, t: Throwable) {
                view?.hideLoading()
                view?.showError("Network error: ${t.message ?: "Please check your connection."}")
            }
        })
    }
    override fun onDestroy() {
        view = null
    }
}
