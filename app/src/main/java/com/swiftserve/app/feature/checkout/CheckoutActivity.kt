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

        // Mock Navigation to Payment
        binding.btnCheckout.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }

        binding.btnCancelAll.setOnClickListener {
            finish()
        }
    }
}
