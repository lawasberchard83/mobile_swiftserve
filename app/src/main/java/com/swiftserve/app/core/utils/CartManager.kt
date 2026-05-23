package com.swiftserve.app.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swiftserve.app.core.model.Product

data class CartItemWrapper(
    val product: Product,
    var quantity: Int
)

object CartManager {
    private const val PREF_NAME = "SwiftServeCart"
    private const val KEY_CART_ITEMS = "cart_items"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getCartItems(context: Context): List<CartItemWrapper> {
        val json = prefs(context).getString(KEY_CART_ITEMS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<CartItemWrapper>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveCartItems(context: Context, items: List<CartItemWrapper>) {
        val json = Gson().toJson(items)
        prefs(context).edit().putString(KEY_CART_ITEMS, json).apply()
    }

    fun addToCart(context: Context, product: Product, quantity: Int) {
        val items = getCartItems(context).toMutableList()
        val existing = items.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            items.add(CartItemWrapper(product, quantity))
        }
        saveCartItems(context, items)
    }

    fun updateQuantity(context: Context, productId: Int, quantity: Int) {
        val items = getCartItems(context).toMutableList()
        val existing = items.find { it.product.id == productId }
        if (existing != null) {
            existing.quantity = quantity
            if (existing.quantity <= 0) {
                items.remove(existing)
            }
        }
        saveCartItems(context, items)
    }

    fun removeFromCart(context: Context, productId: Int) {
        val items = getCartItems(context).toMutableList()
        items.removeAll { it.product.id == productId }
        saveCartItems(context, items)
    }

    fun clearCart(context: Context) {
        prefs(context).edit().remove(KEY_CART_ITEMS).apply()
    }
}
