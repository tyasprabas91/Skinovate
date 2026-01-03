package com.example.skinovate.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skinovate.data.Product

/**
 * Entity untuk Product dalam Room Database
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val description: String,
    val price: Double,
    val targetSkinConditions: String, // Stored as comma-separated string, converted by TypeConverter
    val imageResId: Int
) {
    /**
     * Convert Entity to Domain Model
     */
    fun toProduct(): Product {
        return Product(
            id = id,
            name = name,
            brand = brand,
            category = category,
            description = description,
            price = price,
            targetSkinConditions = ConversionHelpers.toStringList(targetSkinConditions),
            imageResId = imageResId
        )
    }
    
    companion object {
        /**
         * Convert Domain Model to Entity
         */
        fun fromProduct(product: Product): ProductEntity {
            return ProductEntity(
                id = product.id,
                name = product.name,
                brand = product.brand,
                category = product.category,
                description = product.description,
                price = product.price,
                targetSkinConditions = ConversionHelpers.fromStringList(product.targetSkinConditions),
                imageResId = product.imageResId
            )
        }
    }
}

