package com.example.skinovate.data

data class Product(
    val id: String,
    val name: String,
    val brand: String,          // <--- Added
    val description: String,
    val imageResId: Int,
    val category: String,       // Keep as String to be simple
    val price: Double,
    val rating: Double,         // <--- Added
    val reviewCount: Int,       // <--- Added
    val storeUrl: String,       // <--- Added
    val targetSkinConditions: List<String>
)