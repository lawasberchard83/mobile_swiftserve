package com.swiftserve.app.feature.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.swiftserve.app.R
import com.swiftserve.app.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        // Load premium graphics via Glide
        Glide.with(this)
            .load("https://images.unsplash.com/photo-1551782450-a2132b4ba21d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1600&q=80")
            .placeholder(R.color.skeleton_gray)
            .into(binding.ivHero)

        Glide.with(this)
            .load("https://images.unsplash.com/photo-1571091718767-18b5b1457add?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80")
            .placeholder(R.color.skeleton_gray)
            .into(binding.ivCreator)

        Glide.with(this)
            .load("https://images.unsplash.com/photo-1498654896293-37aacf113fd9?ixlib=rb-4.0.3&auto=format&fit=crop&w=900&q=80")
            .placeholder(R.color.skeleton_gray)
            .into(binding.ivBanner)

        Glide.with(this)
            .load("https://images.unsplash.com/photo-1504674900247-0877df9cc836?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80")
            .placeholder(R.color.skeleton_gray)
            .into(binding.ivCatMeals)

        Glide.with(this)
            .load("https://images.unsplash.com/photo-1555939594-58d7cb561ad1?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80")
            .placeholder(R.color.skeleton_gray)
            .into(binding.ivCatDrinks)

        Glide.with(this)
            .load("https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80")
            .placeholder(R.color.skeleton_gray)
            .into(binding.ivCatSnacks)
    }
}
