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
                showProductDetailDialog(product)
            },
            onSaveForLaterClick = { product ->
                com.swiftserve.app.core.utils.SavedManager.addToSaved(this, product)
                Toast.makeText(this, "${product.name} saved for later!", Toast.LENGTH_SHORT).show()
            },
            onProductClick = { product ->
                showProductDetailDialog(product)
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
        binding.ivSaved.setOnClickListener {
            startActivity(Intent(this, SavedActivity::class.java))
        }
        binding.ivOrders.setOnClickListener {
            startActivity(Intent(this, MyOrdersActivity::class.java))
        }
        binding.ivCart.setOnClickListener {
            startActivity(Intent(this, com.swiftserve.app.feature.checkout.CheckoutActivity::class.java))
        }
        binding.swipeRefresh.setOnRefreshListener { fetchDashboard() }

        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString() ?: ""
                updateFilters()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.chipAll.setOnClickListener { setCategory("ALL") }
        binding.chipMeals.setOnClickListener { setCategory("MEALS") }
        binding.chipDrinks.setOnClickListener { setCategory("DRINKS") }
        binding.chipSnacks.setOnClickListener { setCategory("SNACKS") }
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

    private var allProducts: List<com.swiftserve.app.core.model.Product> = emptyList()
    private var currentCategory: String = "ALL"
    private var searchQuery: String = ""

    override fun showProducts(products: List<com.swiftserve.app.core.model.Product>) {
        allProducts = products
        updateFilters()
    }

    private fun updateFilters() {
        val filtered = allProducts.filter { p ->
            val matchesCategory = currentCategory == "ALL" || p.category?.equals(currentCategory, ignoreCase = true) == true
            val matchesSearch = p.name?.contains(searchQuery, ignoreCase = true) == true ||
                                p.description?.contains(searchQuery, ignoreCase = true) == true
            matchesCategory && matchesSearch
        }
        productAdapter.setProducts(filtered)
    }

    private fun setCategory(category: String) {
        currentCategory = category
        binding.chipAll.setBackgroundResource(if (category == "ALL") R.drawable.bg_chip_active else R.drawable.bg_chip_inactive)
        binding.chipMeals.setBackgroundResource(if (category == "MEALS") R.drawable.bg_chip_active else R.drawable.bg_chip_inactive)
        binding.chipDrinks.setBackgroundResource(if (category == "DRINKS") R.drawable.bg_chip_active else R.drawable.bg_chip_inactive)
        binding.chipSnacks.setBackgroundResource(if (category == "SNACKS") R.drawable.bg_chip_active else R.drawable.bg_chip_inactive)
        
        binding.chipAll.setTextColor(if (category == "ALL") android.graphics.Color.WHITE else resources.getColor(R.color.brand_primary))
        binding.chipMeals.setTextColor(if (category == "MEALS") android.graphics.Color.WHITE else resources.getColor(R.color.brand_primary))
        binding.chipDrinks.setTextColor(if (category == "DRINKS") android.graphics.Color.WHITE else resources.getColor(R.color.brand_primary))
        binding.chipSnacks.setTextColor(if (category == "SNACKS") android.graphics.Color.WHITE else resources.getColor(R.color.brand_primary))
        
        updateFilters()
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

    private fun showProductDetailDialog(product: com.swiftserve.app.core.model.Product) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val dialogBinding = com.swiftserve.app.databinding.DialogProductBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvProductName.text = product.name ?: "Unknown Product"
        dialogBinding.tvProductDescription.text = product.description ?: ""
        dialogBinding.tvProductPrice.text = "₱%.2f".format(product.price ?: 0.0)
        dialogBinding.tvProductRating.text = "⭐ 4.5 rating"

        if (!product.imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(product.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.img_burger)
                .into(dialogBinding.ivProductImage)
        } else {
            dialogBinding.ivProductImage.setImageResource(R.drawable.img_burger)
        }

        var quantity = 1
        val updateQuantityViews = {
            dialogBinding.tvQuantity.text = quantity.toString()
            val total = (product.price ?: 0.0) * quantity
            dialogBinding.btnAddToCart.text = "Add to Cart - ₱%.2f".format(total)
        }
        updateQuantityViews()

        dialogBinding.btnPlus.setOnClickListener {
            quantity++
            updateQuantityViews()
        }

        dialogBinding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityViews()
            }
        }

        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnAddToCart.setOnClickListener {
            dialog.dismiss()
            com.swiftserve.app.core.utils.CartManager.addToCart(this, product, quantity)
            Toast.makeText(this, "${product.name} added to cart!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
