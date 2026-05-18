package com.swiftserve.app.feature.checkout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.swiftserve.app.R
import com.swiftserve.app.databinding.ActivityCheckoutBinding
import com.swiftserve.app.feature.payment.PaymentActivity

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val productId = intent.getIntExtra("product_id", 0)
        val productName = intent.getStringExtra("product_name") ?: "Unknown Product"
        val productDesc = intent.getStringExtra("product_desc") ?: "No description available"
        val productPrice = intent.getDoubleExtra("product_price", 0.0)
        val imageUrl = intent.getStringExtra("product_image")

        // Display Data
        binding.tvProductName.text = productName
        binding.tvProductDescription.text = productDesc
        
        if (!imageUrl.isNullOrEmpty()) {
            com.bumptech.glide.Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(binding.ivProductImage)
        }

        // Calculate Totals
        val subtotal = productPrice
        val tax = subtotal * 0.05
        val total = subtotal + tax

        binding.tvSubtotal.text = "₱%.2f".format(subtotal)
        binding.tvTax.text = "₱%.2f".format(tax)
        binding.tvTotal.text = "₱%.2f".format(total)

        binding.btnCheckout.setOnClickListener {
            val paymentIntent = Intent(this, PaymentActivity::class.java)
            paymentIntent.putExtra("total_amount", total)
            startActivity(paymentIntent)
        }

        binding.btnCancelAll.setOnClickListener {
            finish()
        }
    }
}
