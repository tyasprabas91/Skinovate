package com.example.skinovate.data

import com.example.skinovate.R

object ProductRepository {

    val allProducts = listOf(
        Product(
            id = "p1",
            name = "Gentle Foam Cleanser",
            brand = "CeraVe", // <--- NEW
            description = "A soft foam that cleans without stripping oils.",
            imageResId = R.drawable.ic_launcher_background,
            category = "Cleanser",
            price = 12.00,
            rating = 4.8,      // <--- NEW
            reviewCount = 120, // <--- NEW
            storeUrl = "https://www.amazon.com", // <--- NEW
            targetSkinConditions = listOf("Oily", "Sensitive", "Acne")
        ),
        Product(
            id = "p2",
            name = "Hydra-Boost Gel",
            brand = "Neutrogena",
            description = "Lightweight hydration for thirsty skin.",
            imageResId = R.drawable.ic_launcher_background,
            category = "Moisturizer",
            price = 18.50,
            rating = 4.5,
            reviewCount = 85,
            storeUrl = "https://www.amazon.com",
            targetSkinConditions = listOf("Dry", "Dehydrated", "Combination")
        ),
        // ... Add these new fields to the rest of your items ...
    )

    // ... (Keep the rest of the functions the same)
    // Make sure this function is physically inside the {} of the object
    fun getRecommendations(userSkinCondition: String): List<Product> {
        if (userSkinCondition.isEmpty()) return allProducts

        return allProducts.filter { product ->
            product.targetSkinConditions.contains(userSkinCondition) ||
                    product.targetSkinConditions.contains("All")
        }
    }
}

