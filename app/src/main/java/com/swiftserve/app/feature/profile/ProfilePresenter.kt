package com.swiftserve.app.feature.profile

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.ProfileResponse
import com.swiftserve.app.core.model.SupabaseUser
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

        // Extract user ID from session token
        val userId = token.substringAfter("supabase_user_").toIntOrNull()
            ?: run {
                view?.loadFromSession()
                view?.hideLoading()
                return
            }

        RetrofitClient.instance.getProfile(
            idFilter = "eq.$userId"
        ).enqueue(object : Callback<List<SupabaseUser>> {
            override fun onResponse(call: Call<List<SupabaseUser>>, response: Response<List<SupabaseUser>>) {
                view?.hideLoading()
                when {
                    response.isSuccessful -> {
                        val user = response.body()?.firstOrNull()
                        val profileResponse = ProfileResponse(
                            message = "Profile loaded",
                            user    = user?.toUserData()
                        )
                        view?.showProfileData(profileResponse)
                    }
                    response.code() == 401 -> view?.navigateToLogin()
                    else -> {
                        view?.loadFromSession()
                        view?.showError(NetworkUtils.parseError(response))
                    }
                }
            }

            override fun onFailure(call: Call<List<SupabaseUser>>, t: Throwable) {
                view?.hideLoading()
                view?.loadFromSession()
                view?.showError("Network error.")
            }
        })
    }

    override fun logout(token: String) {
        // Supabase anon key doesn't require a logout endpoint — just clear session locally
        view?.navigateToLogin()
    }

    override fun onDestroy() {
        view = null
    }
}
