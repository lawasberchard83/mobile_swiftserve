package com.swiftserve.app.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        binding.btnViewProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.btnLogout.setOnClickListener { showLogoutDialog() }
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
        val name = SessionManager.getUserName(this) ?: "User"
        val email = SessionManager.getUserEmail(this) ?: ""
        val photo = SessionManager.getUserPhoto(this)

        binding.tvWelcome.text = "Welcome back, $name!"
        binding.tvUserEmail.text = email

        if (!photo.isNullOrEmpty()) {
            Glide.with(this).load(photo).circleCrop()
                .placeholder(R.drawable.ic_person_placeholder)
                .into(binding.ivProfile)
        }
    }

    private fun updateUI(body: DashboardResponse?) {
        val user = body?.user
        val stats = body?.stats

        binding.tvWelcome.text = "Welcome back, ${user?.name ?: "User"}!"
        binding.tvUserEmail.text = user?.email ?: ""

        val photo = user?.photo
        if (!photo.isNullOrEmpty()) {
            Glide.with(this).load(photo).circleCrop()
                .placeholder(R.drawable.ic_person_placeholder)
                .into(binding.ivProfile)
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_person_placeholder)
        }

        binding.tvTotalOrders.text = (stats?.totalOrders ?: 0).toString()
        binding.tvPendingOrders.text = (stats?.pendingOrders ?: 0).toString()
        binding.tvCompletedOrders.text = (stats?.completedOrders ?: 0).toString()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ -> performLogout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        val token = SessionManager.getBearerToken(this)
        RetrofitClient.instance.logout(token).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                SessionManager.clearSession(this@DashboardActivity)
                navigateToLogin()
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                SessionManager.clearSession(this@DashboardActivity)
                navigateToLogin()
            }
        })
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
