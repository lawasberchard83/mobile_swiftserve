package com.swiftserve.app.feature.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.swiftserve.app.core.model.RegisterRequest
import com.swiftserve.app.databinding.ActivityRegisterBinding
import com.swiftserve.app.core.utils.NetworkUtils

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var presenter: RegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = RegisterPresenter(this)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val name = "$firstName $lastName".trim()
            val phone = binding.etPhone.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(firstName, lastName, phone, email, address, password, confirmPassword)) {
                if (!NetworkUtils.isNetworkAvailable(this)) {
                    showError("No internet connection. Please check your network.")
                } else {
                    val request = RegisterRequest(
                        username = email,
                        password = password,
                        fullName = name,
                        email = email,
                        phone = phone,
                        address = address
                    )
                    presenter.performRegister(request)
                }
            }
        }
        binding.tvLogin.setOnClickListener { finish() }
    }

    private fun validateInputs(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        address: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        if (firstName.isEmpty()) {
            binding.tilFirstName.error = "First name is required"
            isValid = false
        } else {
            binding.tilFirstName.error = null
        }

        if (lastName.isEmpty()) {
            binding.tilLastName.error = "Last name is required"
            isValid = false
        } else {
            binding.tilLastName.error = null
        }

        if (phone.isEmpty()) {
            binding.tilPhone.error = "Phone number is required"
            isValid = false
        } else if (phone.length != 11 || !phone.all { it.isDigit() }) {
            binding.tilPhone.error = "Phone number must be exactly 11 digits"
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (address.isEmpty()) {
            binding.tilAddress.error = "Address is required"
            isValid = false
        } else {
            binding.tilAddress.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 8) {
            binding.tilPassword.error = "Password must be at least 8 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        return isValid
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = ""
        binding.etFirstName.isEnabled = false
        binding.etLastName.isEnabled = false
        binding.etPhone.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etAddress.isEnabled = false
        binding.etPassword.isEnabled = false
        binding.etConfirmPassword.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnRegister.isEnabled = true
        binding.btnRegister.text = "Create Account"
        binding.etFirstName.isEnabled = true
        binding.etLastName.isEnabled = true
        binding.etPhone.isEnabled = true
        binding.etEmail.isEnabled = true
        binding.etAddress.isEnabled = true
        binding.etPassword.isEnabled = true
        binding.etConfirmPassword.isEnabled = true
    }

    override fun navigateToLogin(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
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
