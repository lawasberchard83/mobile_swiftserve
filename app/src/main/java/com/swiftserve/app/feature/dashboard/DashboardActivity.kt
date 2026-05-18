package com.swiftserve.app.feature.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.swiftserve.app.R
import com.swiftserve.app.core.model.DashboardResponse
import com.swiftserve.app.databinding.ActivityDashboardBinding
import com.swiftserve.app.feature.auth.LoginActivity
import com.swiftserve.app.feature.profile.ProfileActivity
import com.swiftserve.app.core.utils.NetworkUtils
import com.swiftserve.app.core.utils.SessionManager

class DashboardActivity : AppCompatActivity(), DashboardContract.View {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var presenter: DashboardPresenter
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = DashboardPresenter(this)

        setupRecyclerView()
        setupClickListeners()
        fetchDashboard()
    }

    override fun onResume() {
        super.onResume()
        fetchDashboard()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onAddToCartClick = { product ->
                val intent = Intent(this, com.swiftserve.app.feature.checkout.CheckoutActivity::class.java)
                intent.putExtra("product_id", product.id)
                intent.putExtra("product_name", product.name)
                intent.putExtra("product_desc", product.description)
                intent.putExtra("product_price", product.price)
                intent.putExtra("product_image", product.imageUrl)
                startActivity(intent)
            },
            onSaveForLaterClick = { product ->
                Toast.makeText(this, "${product.name} saved for later!", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvProducts.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@DashboardActivity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
            adapter = productAdapter
        }
    }

    private fun setupClickListeners() {
        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.swipeRefresh.setOnRefreshListener { fetchDashboard() }
    }

    private fun fetchDashboard() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            binding.swipeRefresh.isRefreshing = false
            loadFromSession()
            Toast.makeText(this, "No internet connection. Showing cached data.", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SessionManager.getBearerToken(this)
        presenter.loadDashboard(token)
        presenter.loadProducts()
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.swipeRefresh.isRefreshing = false
    }

    override fun loadFromSession() {
        val photo = SessionManager.getUserPhoto(this)
        if (!photo.isNullOrEmpty()) {
            Glide.with(this).load(photo).circleCrop()
                .placeholder(R.drawable.ic_person_placeholder)
                .into(binding.ivProfile)
        }
    }

    override fun showDashboardData(body: DashboardResponse?) {
        body?.user?.let { user ->
            SessionManager.saveUserData(
                context = this,
                name = user.name,
                email = user.email,
                phone = user.phone,
                address = user.address,
                photo = user.photo
            )
        }
        val photo = body?.user?.photo
        if (!photo.isNullOrEmpty()) {
            Glide.with(this).load(photo).circleCrop()
                .placeholder(R.drawable.ic_person_placeholder)
                .into(binding.ivProfile)
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_person_placeholder)
        }
    }

    override fun showProducts(products: List<com.swiftserve.app.core.model.Product>) {
        productAdapter.setProducts(products)
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
