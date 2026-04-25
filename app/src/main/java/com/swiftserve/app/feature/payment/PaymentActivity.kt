package com.swiftserve.app.feature.payment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.swiftserve.app.R
import com.swiftserve.app.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlaceOrder.setOnClickListener {
            startActivity(Intent(this, AfterPaymentActivity::class.java))
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}
