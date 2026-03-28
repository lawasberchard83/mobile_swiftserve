package com.swiftserve.app.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.swiftserve.app.R
import com.swiftserve.app.data.api.RetrofitClient
import com.swiftserve.app.data.model.UpdateProfileRequest
import com.swiftserve.app.data.model.UpdateProfileResponse
import com.swiftserve.app.databinding.ActivityUpdateProfileBinding
import com.swiftserve.app.utils.NetworkUtils
import com.swiftserve.app.utils.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    Glide.with(this).load(it).circleCrop()
                        .placeholder(R.drawable.ic_person_placeholder)
                        .into(binding.ivProfilePhoto)
                }
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openImagePicker()
            else Toast.makeText(this, "Permission denied to access photos.", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"

        prefillData()
        setupClickListeners()
    }

    private fun prefillData() {
        binding.etName.setText(SessionManager.getUserName(this))
        binding.etEmail.setText(SessionManager.getUserEmail(this))
        binding.etPhone.setText(SessionManager.getUserPhone(this))
        binding.etAddress.setText(SessionManager.getUserAddress(this))

        val photo = SessionManager.getUserPhoto(this)
        if (!photo.isNullOrEmpty()) {
            Glide.with(this).load(photo).circleCrop()
                .placeholder(R.drawable.ic_person_placeholder)
                .into(binding.ivProfilePhoto)
        }
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.ivProfilePhoto.setOnClickListener { checkPermissionAndOpenPicker() }
        binding.btnChangePhoto.setOnClickListener { checkPermissionAndOpenPicker() }
        binding.btnSave.setOnClickListener { performUpdateProfile() }
    }

    private fun checkPermissionAndOpenPicker() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun performUpdateProfile() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (!validateInputs(name, email)) return

        if (!NetworkUtils.isNetworkAvailable(this)) {
            showError("No internet connection.")
            return
        }

        setLoading(true)

        // Upload photo if selected
        if (selectedImageUri != null) {
            uploadPhotoAndUpdateProfile(name, email, phone, address)
        } else {
            updateProfileOnly(name, email, phone, address)
        }
    }

    private fun uploadPhotoAndUpdateProfile(name: String, email: String, phone: String, address: String) {
        val token = SessionManager.getBearerToken(this)
        val uri = selectedImageUri ?: return

        try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "profile_photo_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { inputStream?.copyTo(it) }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

            RetrofitClient.instance.uploadPhoto(token, photoPart)
                .enqueue(object : Callback<UpdateProfileResponse> {
                    override fun onResponse(
                        call: Call<UpdateProfileResponse>,
                        response: Response<UpdateProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.user?.photo?.let { photoUrl ->
                                SessionManager.saveUserData(this@UpdateProfileActivity, photo = photoUrl)
                            }
                        }
                        updateProfileOnly(name, email, phone, address)
                    }

                    override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                        updateProfileOnly(name, email, phone, address)
                    }
                })
        } catch (e: Exception) {
            updateProfileOnly(name, email, phone, address)
        }
    }

    private fun updateProfileOnly(name: String, email: String, phone: String, address: String) {
        val token = SessionManager.getBearerToken(this)
        val request = UpdateProfileRequest(name = name, email = email, phone = phone.ifEmpty { null }, address = address.ifEmpty { null })

        RetrofitClient.instance.updateProfile(token, request)
            .enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        val user = response.body()?.user
                        SessionManager.saveUserData(
                            context = this@UpdateProfileActivity,
                            name = user?.name ?: name,
                            email = user?.email ?: email,
                            phone = user?.phone ?: phone,
                            address = user?.address ?: address
                        )
                        Toast.makeText(this@UpdateProfileActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        showError(NetworkUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    setLoading(false)
                    showError("Network error: ${t.message}")
                }
            })
    }

    private fun validateInputs(name: String, email: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.tilName.error = "Name is required"
            isValid = false
        } else {
            binding.tilName.error = null
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

        return isValid
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSave.isEnabled = !isLoading
        binding.btnSave.text = if (isLoading) "" else "Save Changes"
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
