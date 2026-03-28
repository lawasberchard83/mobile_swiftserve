package com.swiftserve.app.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.swiftserve.app.R
import com.swiftserve.app.data.api.RetrofitClient
import com.swiftserve.app.data.model.DashboardResponse
import com.swiftserve.app.databinding.ActivityDashboardBinding
import com.swiftserve.app.ui.auth.LoginActivity
import com.swiftserve.app.ui.profile.ProfileActivity
import com.swiftserve.app.utils.NetworkUtils
import com.swiftserve.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        loadDashboard()
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }

    private fun setupClickListeners() {
        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.swipeRefresh.setOnRefreshListener { loadDashboard() }
    }

    private fun loadDashboard() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            binding.swipeRefresh.isRefreshing = false
            loadFromSession()
            Toast.makeText(this, "No internet connection. Showing cached data.", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SessionManager.getBearerToken(this)
        if (token.isBlank() || token == "Bearer null") {
            navigateToLogin()
            return
        }

        setLoading(true)

        RetrofitClient.instance.getDashboard(token).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(
                call: Call<DashboardResponse>,
                response: Response<DashboardResponse>
            ) {
                setLoading(false)
                binding.swipeRefresh.isRefreshing = false

                when {
                    response.isSuccessful -> {
                        val body = response.body()
                        body?.user?.let { user ->
                            SessionManager.saveUserData(
                                context = this@DashboardActivity,
                                name = user.name,
                                email = user.email,
                                phone = user.phone,
                                address = user.address,
                                photo = user.photo
                            )
                        }
                        updateUI(body)
                    }
                    response.code() == 401 -> {
                        SessionManager.clearSession(this@DashboardActivity)
                        navigateToLogin()
                    }
                    else -> {
                        loadFromSession()
                        Toast.makeText(
                            this@DashboardActivity,
                            NetworkUtils.parseError(response),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                setLoading(false)
                binding.swipeRefresh.isRefreshing = false
                loadFromSession()
                Toast.makeText(
                    this@DashboardActivity,
                    "Network error. Showing cached data.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun loadFromSession() {
        val photo = SessionManager.getUserPhoto(this)
        if (!photo.isNullOrEmpty()) {
            Glide.with(this).load(photo).circleCrop()
                .placeholder(R.drawable.ic_person_placeholder)
                .into(binding.ivProfile)
        }
    }

    private fun updateUI(body: DashboardResponse?) {
        val user = body?.user
        val photo = user?.photo
        if (!photo.isNullOrEmpty()) {
            Glide.with(this).load(photo).circleCrop()
                .placeholder(R.drawable.ic_person_placeholder)
                .into(binding.ivProfile)
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_person_placeholder)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
