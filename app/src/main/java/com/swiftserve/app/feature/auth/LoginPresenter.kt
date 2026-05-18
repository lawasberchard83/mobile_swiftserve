package com.swiftserve.app.feature.auth

import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.SupabaseUser
import com.swiftserve.app.core.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPresenter(private var view: LoginContract.View?) : LoginContract.Presenter {
    override fun performLogin(email: String, pass: String) {
        view?.showLoading()
        // Supabase PostgREST: query users where email=eq.X and password=eq.Y
        RetrofitClient.instance.login(
            usernameFilter = "eq.$email",
            passwordFilter = "eq.$pass"
        ).enqueue(object : Callback<List<SupabaseUser>> {
            override fun onResponse(call: Call<List<SupabaseUser>>, response: Response<List<SupabaseUser>>) {
                view?.hideLoading()
                if (response.isSuccessful) {
                    val users = response.body()
                    if (!users.isNullOrEmpty()) {
                        val user = users[0]
                        // Use id as token since Supabase anon key handles auth
                        val sessionToken = "supabase_user_${user.id}"
                        view?.saveUserData(sessionToken, user.toUserData())
                        view?.navigateToDashboard()
                    } else {
                        view?.showError("Invalid username or password.")
                    }
                } else {
                    view?.showError(NetworkUtils.parseError(response))
                }
            }
            override fun onFailure(call: Call<List<SupabaseUser>>, t: Throwable) {
                view?.hideLoading()
                view?.showError("Network error: ${t.message ?: "Check connection."}")
            }
        })
    }
    override fun onDestroy() {
        view = null
    }
}
