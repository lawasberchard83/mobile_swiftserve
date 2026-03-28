package com.swiftserve.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.swiftserve.app.R
import com.swiftserve.app.data.api.RetrofitClient
import com.swiftserve.app.data.model.ProfileResponse
import com.swiftserve.app.databinding.ActivityProfileBinding
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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Profile"

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, UpdateProfileActivity::class.java))
        }
        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
        binding.swipeRefresh.setOnRefreshListener { loadProfile() }
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
                            photo = user?.photo,
                            joinDate = user?.createdAt
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
            photo = SessionManager.getUserPhoto(this),
            joinDate = null
        )
    }

    private fun populateUI(
        name: String?,
        email: String?,
        phone: String?,
        address: String?,
        photo: String?,
        joinDate: String?
    ) {
        binding.tvName.text = name ?: "—"
        binding.tvEmail.text = email ?: "—"
        binding.tvPhone.text = if (phone.isNullOrEmpty()) "—" else phone
        binding.tvAddress.text = if (address.isNullOrEmpty()) "—" else address
        binding.tvJoinDate.text = if (joinDate.isNullOrEmpty()) "—" else joinDate.take(10)

        if (!photo.isNullOrEmpty()) {
            Glide.with(this).load(photo).circleCrop()
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .into(binding.ivProfilePhoto)
        } else {
            binding.ivProfilePhoto.setImageResource(R.drawable.ic_person_placeholder)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.scrollContent.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}
