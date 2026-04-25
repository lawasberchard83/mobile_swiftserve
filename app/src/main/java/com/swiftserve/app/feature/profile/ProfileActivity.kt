package com.swiftserve.app.feature.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.swiftserve.app.R
import com.swiftserve.app.core.model.ProfileResponse
import com.swiftserve.app.databinding.ActivityProfileBinding
import com.swiftserve.app.feature.auth.LoginActivity
import com.swiftserve.app.core.utils.NetworkUtils
import com.swiftserve.app.core.utils.SessionManager

class ProfileActivity : AppCompatActivity(), ProfileContract.View {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var presenter: ProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = ProfilePresenter(this)
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        fetchProfile()
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener { finish() }
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, UpdateProfileActivity::class.java))
        }
        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
        binding.swipeRefresh.setOnRefreshListener { fetchProfile() }

        binding.btnLogout.setOnClickListener { showLogoutDialog() }
    }

    private fun fetchProfile() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            binding.swipeRefresh.isRefreshing = false
            loadFromSession()
            Toast.makeText(this, "Showing cached profile.", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SessionManager.getBearerToken(this)
        presenter.loadProfile(token)
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.scrollContent.alpha = 0.5f
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.scrollContent.alpha = 1.0f
        binding.swipeRefresh.isRefreshing = false
    }

    override fun loadFromSession() {
        populateUI(
            name = SessionManager.getUserName(this),
            email = SessionManager.getUserEmail(this),
            phone = SessionManager.getUserPhone(this),
            address = SessionManager.getUserAddress(this),
            photo = SessionManager.getUserPhoto(this)
        )
    }

    override fun showProfileData(body: ProfileResponse?) {
        val user = body?.user
        user?.let {
            SessionManager.saveUserData(
                context = this,
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
        presenter.logout(token)
    }

    override fun navigateToLogin() {
        SessionManager.clearSession(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
