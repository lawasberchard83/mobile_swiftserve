package com.swiftserve.app.feature.dashboard

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swiftserve.app.core.model.Order
import com.swiftserve.app.databinding.ItemOrderBinding

class OrderAdapter(
    private var items: List<Order>
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    fun updateItems(newItems: List<Order>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvOrderId.text = "Order #${order.id ?: 0}"
            binding.tvOrderItems.text = order.items ?: "No item details"
            
            val total = order.totalAmount ?: order.total ?: 0.0
            binding.tvOrderTotal.text = "₱%.2f".format(total)

            // Format date: "2026-05-21T12:15:00" -> "2026-05-21 12:15:00"
            val rawDate = order.createdAt ?: ""
            binding.tvOrderDate.text = rawDate.replace("T", " ").substringBefore(".")

            // Status tag
            val status = order.status ?: "Pending"
            binding.tvOrderStatus.text = status

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 16f
            }

            when (status.lowercase()) {
                "pending" -> {
                    drawable.setColor(Color.parseColor("#E65100")) // Orange
                }
                "preparing" -> {
                    drawable.setColor(Color.parseColor("#0D47A1")) // Blue
                }
                "delivered" -> {
                    drawable.setColor(Color.parseColor("#1B5E20")) // Green
                }
                else -> {
                    drawable.setColor(Color.parseColor("#555555")) // Gray
                }
            }
            binding.tvOrderStatus.background = drawable
        }
    }
}
