package com.swiftserve.app.feature.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swiftserve.app.R
import com.swiftserve.app.core.model.Product
import com.swiftserve.app.databinding.ItemSavedBinding

class SavedAdapter(
    private var items: List<Product>,
    private val onAddToCartClick: (Product) -> Unit,
    private val onRemoveClick: (Product) -> Unit
) : RecyclerView.Adapter<SavedAdapter.ViewHolder>() {

    fun updateItems(newItems: List<Product>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemSavedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.name ?: "Unknown"
            binding.tvProductDescription.text = product.description ?: "No description"
            binding.tvProductPrice.text = "₱%.2f".format(product.price ?: 0.0)

            // Load Image
            val imageUrl = product.imageUrl
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(binding.ivProductImage.context)
                    .load(imageUrl)
                    .placeholder(R.color.skeleton_gray)
                    .error(R.color.skeleton_gray)
                    .into(binding.ivProductImage)
            } else {
                binding.ivProductImage.setImageResource(R.color.skeleton_gray)
            }

            binding.btnAddToCart.setOnClickListener {
                onAddToCartClick(product)
            }

            binding.btnRemove.setOnClickListener {
                onRemoveClick(product)
            }
        }
    }
}
