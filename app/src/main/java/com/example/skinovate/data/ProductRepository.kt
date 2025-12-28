package com.example.skinovate.data

import com.example.skinovate.R
import com.example.skinovate.data.Product

object ProductRepository {

    // ---------------------------------------------------------
    // MASTER PRODUCT LIST
    // Add or remove items here to update the app content.
    // ---------------------------------------------------------
    val allProducts = listOf(
        Product(
            id = "p1",
            name = "Gentle Foam Cleanser",
            description = "A soft foam that cleans without stripping oils.",
            imageResId = R.drawable.ic_launcher_background, //R.drawable.cleanser_bottle, // ERROR? Change to an image you actually have
            category = "Cleanser",
            price = 12.00,
            targetSkinConditions = listOf("Oily", "Sensitive", "Acne")
        ),
        Product(
            id = "p2",
            name = "Hydra-Boost Gel",
            description = "Lightweight hydration for thirsty skin.",
            imageResId = R.drawable.ic_launcher_background, //R.drawable.moisturizer_jar,
            category = "Moisturizer",
            price = 18.50,
            targetSkinConditions = listOf("Dry", "Dehydrated", "Combination")
        ),
        Product(
            id = "p3",
            name = "Salicylic Acid Spot Treatment",
            description = "Targeted treatment for active breakouts.",
            imageResId = R.drawable.ic_launcher_background, //R.drawable.serum_bottle,
            category = "Treatment",
            price = 9.99,
            targetSkinConditions = listOf("Acne", "Oily")
        ),
        Product(
            id = "p4",
            name = "Retinol Night Cream",
            description = "Anti-aging cream that renews skin texture.",
            imageResId = R.drawable.ic_launcher_background, //R.drawable.night_cream,
            category = "Treatment",
            price = 24.00,
            targetSkinConditions = listOf("Wrinkles", "Aging", "Normal")
        ),
        Product(
            id = "p5",
            name = "Daily SPF 50",
            description = "Non-greasy sun protection.",
            imageResId = R.drawable.ic_launcher_background, //R.drawable.sunscreen,
            category = "Sunscreen",
            price = 15.00,
            targetSkinConditions = listOf("All", "Normal", "Dry", "Oily")
        )
    )

    // ---------------------------------------------------------
    // LOGIC FUNCTIONS
    // ---------------------------------------------------------

    // Call this function when loading the "Highlighted Products" section
    fun getRecommendations(userSkinCondition: String): List<Product> {
        // If the analysis is generic or empty, show everything (or just popular ones)
        if (userSkinCondition.isEmpty()) return allProducts

        return allProducts.filter { product ->
            // Check if the product targets the user's condition OR is for "All" skin types
            product.targetSkinConditions.contains(userSkinCondition) ||
                    product.targetSkinConditions.contains("All")
        }
    }

    // Helper to find a specific product (useful for the Product Detail page)
    fun getProductById(id: String): Product? {
        return allProducts.find { it.id == id }
    }
}