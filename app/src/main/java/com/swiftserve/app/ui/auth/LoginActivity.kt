package com.swiftserve.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.swiftserve.app.data.api.RetrofitClient
import com.swiftserve.app.data.model.LoginRequest
import com.swiftserve.app.data.model.LoginResponse
import com.swiftserve.app.databinding.ActivityLoginBinding
import com.swiftserve.app.ui.dashboard.DashboardActivity
import com.swiftserve.app.utils.NetworkUtils
import com.swiftserve.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener { performLogin() }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!validateInputs(email, password)) return

        if (!NetworkUtils.isNetworkAvailable(this)) {
            showError("No internet connection. Please check your network.")
            return
        }

        setLoading(true)

        val request = LoginRequest(email, password)
        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                setLoading(false)
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.token
                    if (!token.isNullOrEmpty()) {
                        SessionManager.saveToken(this@LoginActivity, token)
                        body.user?.let { user ->
                            SessionManager.saveUserData(
                                context = this@LoginActivity,
                                id = user.id,
                                name = user.name,
                                email = user.email,
                                phone = user.phone,
                                address = user.address,
                                photo = user.photo
                            )
                        }
                        navigateToDashboard()
                    } else {
                        showError(body?.message ?: "Login failed. Please try again.")
                    }
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    showError(errorMsg)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                setLoading(false)
                showError("Network error: ${t.message ?: "Please check your connection."}")
            }
        })
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "" else "Login"
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
