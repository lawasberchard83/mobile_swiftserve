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

        val totalAmount = intent.getDoubleExtra("total_amount", 0.0)
        binding.tvTotal.text = "₱%.2f".format(totalAmount)

        binding.btnPlaceOrder.setOnClickListener {
            val token = com.swiftserve.app.core.utils.SessionManager.getBearerToken(this)
            val userId = token.removePrefix("supabase_user_").toIntOrNull() ?: 1

            val request = com.swiftserve.app.core.model.CreateOrderRequest(
                userId = userId,
                status = "Pending",
                total = totalAmount
            )

            com.swiftserve.app.core.api.RetrofitClient.instance.createOrder(request).enqueue(object : retrofit2.Callback<List<com.swiftserve.app.core.model.Order>> {
                override fun onResponse(call: retrofit2.Call<List<com.swiftserve.app.core.model.Order>>, response: retrofit2.Response<List<com.swiftserve.app.core.model.Order>>) {
                    if (response.isSuccessful) {
                        android.widget.Toast.makeText(this@PaymentActivity, "Order placed successfully!", android.widget.Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@PaymentActivity, AfterPaymentActivity::class.java))
                        finish()
                    } else {
                        android.widget.Toast.makeText(this@PaymentActivity, "Failed to place order.", android.widget.Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@PaymentActivity, AfterPaymentActivity::class.java))
                        finish()
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<com.swiftserve.app.core.model.Order>>, t: Throwable) {
                    android.widget.Toast.makeText(this@PaymentActivity, "Network error placing order.", android.widget.Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@PaymentActivity, AfterPaymentActivity::class.java))
                    finish()
                }
            })
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}
