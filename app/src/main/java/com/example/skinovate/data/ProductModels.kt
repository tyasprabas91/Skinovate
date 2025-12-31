package com.example.skinovate.data

import androidx.annotation.DrawableRes

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val description: String,
    val price: Double,
    val targetSkinConditions: List<String>,
    @DrawableRes val imageResId: Int
)