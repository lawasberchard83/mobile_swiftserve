package com.swiftserve.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.swiftserve.app.R
import com.swiftserve.app.data.api.RetrofitClient
import com.swiftserve.app.data.model.ProfileResponse
import com.swiftserve.app.databinding.ActivityProfileBinding
import com.swiftserve.app.ui.auth.LoginActivity
import com.swiftserve.app.utils.NetworkUtils
import com.swiftserve.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener { finish() }
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, UpdateProfileActivity::class.java))
        }
        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
        binding.swipeRefresh.setOnRefreshListener { loadProfile() }
        
        binding.btnLogout.setOnClickListener { showLogoutDialog() }
    }

    private fun loadProfile() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            binding.swipeRefresh.isRefreshing = false
            loadFromSession()
            Toast.makeText(this, "Showing cached profile.", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SessionManager.getBearerToken(this)
        setLoading(true)

        RetrofitClient.instance.getProfile(token).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                setLoading(false)
                binding.swipeRefresh.isRefreshing = false

                when {
                    response.isSuccessful -> {
                        val user = response.body()?.user
                        user?.let {
                            SessionManager.saveUserData(
                                context = this@ProfileActivity,
                                name = it.name,
                                email = it.email,
                                phone = it.phone,
                                address = it.address,
                                photo = it.photo
                            )
                        }
                        populateUI(
                            name = user?.name,
                            email = user?.email,
                            phone = user?.phone,
                            address = user?.address,
                            photo = user?.photo
                        )
                    }
                    response.code() == 401 -> {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Session expired. Please login again.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    else -> {
                        loadFromSession()
                        Toast.makeText(
                            this@ProfileActivity,
                            NetworkUtils.parseError(response),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                setLoading(false)
                binding.swipeRefresh.isRefreshing = false
                loadFromSession()
                Toast.makeText(this@ProfileActivity, "Network error.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadFromSession() {
        populateUI(
            name = SessionManager.getUserName(this),
            email = SessionManager.getUserEmail(this),
            phone = SessionManager.getUserPhone(this),
            address = SessionManager.getUserAddress(this),
            photo = SessionManager.getUserPhoto(this)
        )
    }

    private fun populateUI(
        name: String?,
        email: String?,
        phone: String?,
        address: String?,
        photo: String?
    ) {
        binding.tvName.text = if (name.isNullOrEmpty()) "Name not set" else name
        binding.tvEmail.text = if (email.isNullOrEmpty()) "Email not set" else email
        binding.tvPhone.text = if (phone.isNullOrEmpty()) "Phone not set" else phone
        binding.tvAddress.text = if (address.isNullOrEmpty()) "Address not set" else address

        if (!photo.isNullOrEmpty()) {
            Glide.with(this).load(photo).circleCrop()
                .placeholder(R.drawable.img_profile)
                .error(R.drawable.img_profile)
                .into(binding.ivProfilePhoto)
                
            Glide.with(this).load(photo).circleCrop()
                .placeholder(R.drawable.img_profile)
                .into(binding.ivHeaderProfile)
        } else {
            binding.ivProfilePhoto.setImageResource(R.drawable.img_profile)
            binding.ivHeaderProfile.setImageResource(R.drawable.img_profile)
        }
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
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                SessionManager.clearSession(this@ProfileActivity)
                navigateToLogin()
            }
            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                SessionManager.clearSession(this@ProfileActivity)
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

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.scrollContent.alpha = if (isLoading) 0.5f else 1.0f
    }
}
