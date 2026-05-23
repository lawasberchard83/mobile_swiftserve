package com.swiftserve.app.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swiftserve.app.core.model.Product

object SavedManager {
    private const val PREF_NAME = "SwiftServeSaved"
    private const val KEY_SAVED_ITEMS = "saved_items"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getSavedItems(context: Context): List<Product> {
        val json = prefs(context).getString(KEY_SAVED_ITEMS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Product>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveSavedItems(context: Context, items: List<Product>) {
        val json = Gson().toJson(items)
        prefs(context).edit().putString(KEY_SAVED_ITEMS, json).apply()
    }

    fun addToSaved(context: Context, product: Product) {
        val items = getSavedItems(context).toMutableList()
        if (items.none { it.id == product.id }) {
            items.add(product)
            saveSavedItems(context, items)
        }
    }

    fun removeFromSaved(context: Context, productId: Int) {
        val items = getSavedItems(context).toMutableList()
        items.removeAll { it.id == productId }
        saveSavedItems(context, items)
    }

    fun isSaved(context: Context, productId: Int): Boolean {
        return getSavedItems(context).any { it.id == productId }
    }
}
