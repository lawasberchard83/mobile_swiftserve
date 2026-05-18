package com.swiftserve.app.feature.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.swiftserve.app.R
import com.swiftserve.app.databinding.ActivityAfterPaymentBinding
import com.swiftserve.app.feature.dashboard.DashboardActivity

class AfterPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAfterPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfterPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReturnToShop.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
