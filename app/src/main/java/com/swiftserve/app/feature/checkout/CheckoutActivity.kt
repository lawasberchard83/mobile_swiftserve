package com.swiftserve.app.feature.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.swiftserve.app.R
import com.swiftserve.app.core.utils.CartItemWrapper
import com.swiftserve.app.core.utils.CartManager
import com.swiftserve.app.databinding.ActivityCheckoutBinding
import com.swiftserve.app.feature.payment.PaymentActivity

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var cartAdapter: CartAdapter
    private var currentTotal = 0.0
    private var itemsString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        refreshCartUi()

        binding.btnCheckout.setOnClickListener {
            val cartItems = CartManager.getCartItems(this)
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("total_amount", currentTotal)
            intent.putExtra("order_items", itemsString)
            startActivity(intent)
        }

        binding.btnCancelAll.setOnClickListener {
            CartManager.clearCart(this)
            refreshCartUi()
            Toast.makeText(this, "Cart cleared.", Toast.LENGTH_SHORT).show()
        }

        binding.btnBrowseMenu.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(
            items = emptyList(),
            onQuantityChanged = { wrapper, newQty ->
                CartManager.updateQuantity(this, wrapper.product.id ?: 0, newQty)
                refreshCartUi()
            },
            onRemoveItem = { wrapper ->
                CartManager.removeFromCart(this, wrapper.product.id ?: 0)
                refreshCartUi()
                Toast.makeText(this, "${wrapper.product.name} removed from cart", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun refreshCartUi() {
        val cartItems = CartManager.getCartItems(this)
        cartAdapter.updateItems(cartItems)

        if (cartItems.isEmpty()) {
            binding.layoutEmptyCart.visibility = View.VISIBLE
            binding.rvCartItems.visibility = View.GONE
            binding.btnCheckout.isEnabled = false
            binding.btnCancelAll.isEnabled = false

            binding.tvSubtotal.text = "₱0.00"
            binding.tvShipping.text = "₱0.00"
            binding.tvTax.text = "₱0.00"
            binding.tvTotal.text = "₱0.00"
            currentTotal = 0.0
            itemsString = ""
        } else {
            binding.layoutEmptyCart.visibility = View.GONE
            binding.rvCartItems.visibility = View.VISIBLE
            binding.btnCheckout.isEnabled = true
            binding.btnCancelAll.isEnabled = true

            val subtotal = cartItems.sumOf { (it.product.price ?: 0.0) * it.quantity }
            val shipping = 5.0
            val tax = subtotal * 0.05
            val total = subtotal + shipping + tax

            binding.tvSubtotal.text = "₱%.2f".format(subtotal)
            binding.tvShipping.text = "₱%.2f".format(shipping)
            binding.tvTax.text = "₱%.2f".format(tax)
            binding.tvTotal.text = "₱%.2f".format(total)

            currentTotal = total
            itemsString = cartItems.joinToString(", ") { "${it.product.name} (x${it.quantity})" }
        }
    }
}

