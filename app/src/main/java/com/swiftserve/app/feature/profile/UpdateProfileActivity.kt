package com.swiftserve.app.feature.profile

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
import com.swiftserve.app.databinding.ActivityUpdateProfileBinding
import com.swiftserve.app.core.utils.NetworkUtils
import com.swiftserve.app.core.utils.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UpdateProfileActivity : AppCompatActivity(), UpdateProfileContract.View {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var presenter: UpdateProfilePresenter
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

        presenter = UpdateProfilePresenter(this)

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

        if (!validateInputs(name, email, phone)) return

        if (!NetworkUtils.isNetworkAvailable(this)) {
            showError("No internet connection.")
            return
        }

        val token = SessionManager.getBearerToken(this)
        
        var photoPart: MultipartBody.Part? = null
        selectedImageUri?.let { uri ->
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val file = File(cacheDir, "profile_photo_${System.currentTimeMillis()}.jpg")
                file.outputStream().use { inputStream?.copyTo(it) }

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
            } catch (e: Exception) {
                // Ignore error, proceed without photo
            }
        }
        
        presenter.updateProfile(token, name, email, phone, address, photoPart)
    }

    private fun validateInputs(name: String, email: String, phone: String): Boolean {
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

        if (phone.isNotEmpty() && (phone.length != 11 || !phone.all { it.isDigit() })) {
            binding.tilPhone.error = "Phone number must be exactly 11 digits"
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        return isValid
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false
        binding.btnSave.text = ""
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSave.isEnabled = true
        binding.btnSave.text = "Save Changes"
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun saveUserData(name: String, email: String, phone: String, address: String, photoUrl: String?) {
        SessionManager.saveUserData(
            context = this,
            name = name,
            email = email,
            phone = phone,
            address = address,
            photo = photoUrl
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
