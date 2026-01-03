package com.example.skinovate.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk Product operations
 */
@Dao
interface ProductDao {
    
    /**
     * Get all products
     */
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>
    
    /**
     * Get product by ID
     */
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?
    
    /**
     * Search products by name or brand
     */
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<ProductEntity>>
    
    /**
     * Get products by category
     */
    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>
    
    /**
     * Insert single product
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
    
    /**
     * Insert multiple products
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)
    
    /**
     * Update product
     */
    @Update
    suspend fun updateProduct(product: ProductEntity)
    
    /**
     * Delete product
     */
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
    
    /**
     * Delete all products
     */
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}

