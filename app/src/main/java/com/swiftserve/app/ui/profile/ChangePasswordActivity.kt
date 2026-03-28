package com.swiftserve.app.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.swiftserve.app.data.api.RetrofitClient
import com.swiftserve.app.data.model.ChangePasswordRequest
import com.swiftserve.app.data.model.ChangePasswordResponse
import com.swiftserve.app.databinding.ActivityChangePasswordBinding
import com.swiftserve.app.utils.NetworkUtils
import com.swiftserve.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        setLoading(true)

        val token = SessionManager.getBearerToken(this)
        val request = ChangePasswordRequest(
            currentPassword = currentPassword,
            newPassword = newPassword,
            newPasswordConfirmation = confirmPassword
        )

        RetrofitClient.instance.changePassword(token, request)
            .enqueue(object : Callback<ChangePasswordResponse> {
                override fun onResponse(
                    call: Call<ChangePasswordResponse>,
                    response: Response<ChangePasswordResponse>
                ) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        val message = response.body()?.message ?: "Password changed successfully!"
                        Toast.makeText(this@ChangePasswordActivity, message, Toast.LENGTH_LONG).show()
                        clearFields()
                        finish()
                    } else {
                        when (response.code()) {
                            401 -> showError("Current password is incorrect.")
                            422 -> showError(NetworkUtils.parseError(response))
                            else -> showError(NetworkUtils.parseError(response))
                        }
                    }
                }

                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    setLoading(false)
                    showError("Network error: ${t.message ?: "Please check your connection."}")
                }
            })
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

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnChangePassword.isEnabled = !isLoading
        binding.btnChangePassword.text = if (isLoading) "" else "Change Password"
        binding.etCurrentPassword.isEnabled = !isLoading
        binding.etNewPassword.isEnabled = !isLoading
        binding.etConfirmNewPassword.isEnabled = !isLoading
    }

    private fun clearFields() {
        binding.etCurrentPassword.text?.clear()
        binding.etNewPassword.text?.clear()
        binding.etConfirmNewPassword.text?.clear()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
