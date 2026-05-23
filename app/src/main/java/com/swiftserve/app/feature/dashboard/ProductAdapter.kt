package com.swiftserve.app.feature.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swiftserve.app.R
import com.swiftserve.app.core.model.Product
import com.swiftserve.app.databinding.ItemProductBinding

class ProductAdapter(
    private val onAddToCartClick: (Product) -> Unit,
    private val onSaveForLaterClick: (Product) -> Unit,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val products = mutableListOf<Product>()

    fun setProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.name ?: "Unknown Product"
            binding.tvProductDescription.text = product.description ?: ""
            binding.tvProductPrice.text = "₱${product.price ?: "0.00"}"

            if (!product.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(product.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.img_burger)
                    .into(binding.ivProductImage)
            } else {
                binding.ivProductImage.setImageResource(R.drawable.img_burger)
            }

            binding.btnAddToCart.setOnClickListener {
                onAddToCartClick(product)
            }

            binding.btnSaveForLater.setOnClickListener {
                onSaveForLaterClick(product)
            }

            binding.root.setOnClickListener {
                onProductClick(product)
            }
        }
    }
}
