package com.swiftserve.app.feature.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.swiftserve.app.core.api.RetrofitClient
import com.swiftserve.app.core.model.Order
import com.swiftserve.app.core.utils.SessionManager
import com.swiftserve.app.databinding.ActivityMyOrdersBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyOrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyOrdersBinding
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchOrders()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnOrderNow.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(emptyList())
        binding.rvOrders.adapter = orderAdapter
    }

    private fun fetchOrders() {
        val token = SessionManager.getBearerToken(this)
        val userId = token.substringAfter("supabase_user_").toIntOrNull() ?: 1
        val userIdFilter = "eq.$userId"

        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmptyOrders.visibility = View.GONE
        binding.rvOrders.visibility = View.GONE

        RetrofitClient.instance.getOrders(userIdFilter).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val orders = response.body() ?: emptyList()
                    orderAdapter.updateItems(orders)
                    if (orders.isEmpty()) {
                        binding.layoutEmptyOrders.visibility = View.VISIBLE
                        binding.rvOrders.visibility = View.GONE
                    } else {
                        binding.layoutEmptyOrders.visibility = View.GONE
                        binding.rvOrders.visibility = View.VISIBLE
                    }
                } else {
                    binding.layoutEmptyOrders.visibility = View.VISIBLE
                    Toast.makeText(this@MyOrdersActivity, "Failed to load orders.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.layoutEmptyOrders.visibility = View.VISIBLE
                Toast.makeText(this@MyOrdersActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
