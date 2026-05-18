package com.swiftserve.app.feature.profile

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.ChangePasswordRequest
import com.swiftserve.app.core.model.SupabaseUser
import com.swiftserve.app.core.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordPresenter(private var view: ChangePasswordContract.View?) : ChangePasswordContract.Presenter {
    override fun changePassword(token: String, request: ChangePasswordRequest) {
        view?.showLoading()

        val userId = token.removePrefix("supabase_user_").toIntOrNull()
            ?: run {
                view?.hideLoading()
                view?.showError("Session expired. Please login again.")
                return
            }

        // Supabase: PATCH /users?id=eq.<id> with { "password": "<newPassword>" }
        RetrofitClient.instance.changePassword(
            idFilter = "eq.$userId",
            request  = ChangePasswordRequest(password = request.password)
        ).enqueue(object : Callback<List<SupabaseUser>> {
            override fun onResponse(call: Call<List<SupabaseUser>>, response: Response<List<SupabaseUser>>) {
                view?.hideLoading()
                if (response.isSuccessful) {
                    view?.showSuccess("Password changed successfully!")
                } else {
                    when (response.code()) {
                        401  -> view?.showError("Unauthorized. Please login again.")
                        else -> view?.showError(NetworkUtils.parseError(response))
                    }
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
