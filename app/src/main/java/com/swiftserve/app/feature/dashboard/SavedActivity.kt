package com.swiftserve.app.feature.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.swiftserve.app.core.utils.CartManager
import com.swiftserve.app.core.utils.SavedManager
import com.swiftserve.app.databinding.ActivitySavedBinding

class SavedActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedBinding
    private lateinit var savedAdapter: SavedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        refreshSavedUi()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnBrowseMenu.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvSavedItems.layoutManager = LinearLayoutManager(this)
        savedAdapter = SavedAdapter(
            items = emptyList(),
            onAddToCartClick = { product ->
                CartManager.addToCart(this, product, 1)
                SavedManager.removeFromSaved(this, product.id ?: 0)
                refreshSavedUi()
                Toast.makeText(this, "${product.name} moved to cart!", Toast.LENGTH_SHORT).show()
            },
            onRemoveClick = { product ->
                SavedManager.removeFromSaved(this, product.id ?: 0)
                refreshSavedUi()
                Toast.makeText(this, "${product.name} removed from saved list", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvSavedItems.adapter = savedAdapter
    }

    private fun refreshSavedUi() {
        val savedItems = SavedManager.getSavedItems(this)
        savedAdapter.updateItems(savedItems)

        if (savedItems.isEmpty()) {
            binding.layoutEmptySaved.visibility = View.VISIBLE
            binding.rvSavedItems.visibility = View.GONE
        } else {
            binding.layoutEmptySaved.visibility = View.GONE
            binding.rvSavedItems.visibility = View.VISIBLE
        }
    }
}
