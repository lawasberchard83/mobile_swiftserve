package com.swiftserve.app.feature.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.swiftserve.app.core.model.ChangePasswordRequest
import com.swiftserve.app.databinding.ActivityChangePasswordBinding
import com.swiftserve.app.core.utils.NetworkUtils
import com.swiftserve.app.core.utils.SessionManager

class ChangePasswordActivity : AppCompatActivity(), ChangePasswordContract.View {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var presenter: ChangePasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = ChangePasswordPresenter(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Change Password"

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnChangePassword.setOnClickListener { performChangePassword() }
    }

    private fun performChangePassword() {
        val currentPassword = binding.etCurrentPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmNewPassword.text.toString().trim()

        if (!validateInputs(currentPassword, newPassword, confirmPassword)) return

        if (!NetworkUtils.isNetworkAvailable(this)) {
            showError("No internet connection.")
            return
        }

        val token = SessionManager.getBearerToken(this)
        // Supabase stores plain password in 'password' column — send the new password only
        val request = ChangePasswordRequest(password = newPassword)
        presenter.changePassword(token, request)
    }

    private fun validateInputs(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        if (currentPassword.isEmpty()) {
            binding.tilCurrentPassword.error = "Current password is required"
            isValid = false
        } else {
            binding.tilCurrentPassword.error = null
        }

        if (newPassword.isEmpty()) {
            binding.tilNewPassword.error = "New password is required"
            isValid = false
        } else if (newPassword.length < 8) {
            binding.tilNewPassword.error = "Password must be at least 8 characters"
            isValid = false
        } else if (newPassword == currentPassword) {
            binding.tilNewPassword.error = "New password must be different from current password"
            isValid = false
        } else {
            binding.tilNewPassword.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmNewPassword.error = "Please confirm your new password"
            isValid = false
        } else if (newPassword != confirmPassword) {
            binding.tilConfirmNewPassword.error = "Passwords do not match"
            isValid = false
        } else {
            binding.tilConfirmNewPassword.error = null
        }

        return isValid
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnChangePassword.isEnabled = false
        binding.btnChangePassword.text = ""
        binding.etCurrentPassword.isEnabled = false
        binding.etNewPassword.isEnabled = false
        binding.etConfirmNewPassword.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnChangePassword.isEnabled = true
        binding.btnChangePassword.text = "Change Password"
        binding.etCurrentPassword.isEnabled = true
        binding.etNewPassword.isEnabled = true
        binding.etConfirmNewPassword.isEnabled = true
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        binding.etCurrentPassword.text?.clear()
        binding.etNewPassword.text?.clear()
        binding.etConfirmNewPassword.text?.clear()
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
