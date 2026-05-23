package com.swiftserve.app.feature.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swiftserve.app.R
import com.swiftserve.app.core.utils.CartItemWrapper
import com.swiftserve.app.databinding.ItemCartBinding

class CartAdapter(
    private var items: List<CartItemWrapper>,
    private val onQuantityChanged: (CartItemWrapper, Int) -> Unit,
    private val onRemoveItem: (CartItemWrapper) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    fun updateItems(newItems: List<CartItemWrapper>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(wrapper: CartItemWrapper) {
            val product = wrapper.product
            binding.tvProductName.text = product.name ?: "Unknown"
            binding.tvProductDescription.text = product.description ?: "No description"
            binding.tvProductPrice.text = "₱%.2f".format(product.price ?: 0.0)
            binding.tvQuantity.text = wrapper.quantity.toString()

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

            // Click listeners
            binding.btnPlus.setOnClickListener {
                onQuantityChanged(wrapper, wrapper.quantity + 1)
            }

            binding.btnMinus.setOnClickListener {
                if (wrapper.quantity > 1) {
                    onQuantityChanged(wrapper, wrapper.quantity - 1)
                }
            }

            binding.btnRemove.setOnClickListener {
                onRemoveItem(wrapper)
            }
        }
    }
}
