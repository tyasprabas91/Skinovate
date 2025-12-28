package com.example.skinovate.data

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val imageResId: Int, // Refers to R.drawable.image_name
    val category: String,
    val price: Double,
    val targetSkinConditions: List<String> // Matches the result from your Face Scan
)