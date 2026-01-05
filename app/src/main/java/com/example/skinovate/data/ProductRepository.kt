package com.example.skinovate.data

import android.content.Context
import android.content.SharedPreferences
import com.example.skinovate.data.database.DatabaseModule
import com.example.skinovate.data.database.ProductEntity
import com.example.skinovate.data.database.SkinovateDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

object ProductRepository {

    private const val PREFS_NAME = "skinovate_products_prefs"
    private const val KEY_DATA_SEEDED = "data_seeded"
    
    private var databaseInitialized = false
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Cached products list (loaded from database)
    private var _cachedProducts: List<Product>? = null
    private val _allProductsFlow = MutableStateFlow<List<Product>>(emptyList())
    
    /**
     * All products as Flow (reactive)
     */
    val allProductsFlow: StateFlow<List<Product>> = _allProductsFlow.asStateFlow()
    
    /**
     * All products as List (backward compatibility)
     * This will load from database on first access
     */
    val allProducts: List<Product>
        get() {
            // Return cached if available
            _cachedProducts?.let { return it }
            // Return from Flow if available
            if (_allProductsFlow.value.isNotEmpty()) {
                _cachedProducts = _allProductsFlow.value
                return _cachedProducts!!
            }
            // Fallback to seed data if database not ready
            return ProductSeedData.defaultProducts
        }
    
    /**
     * Initialize repository dengan context
     * Seed data dan load products dari database
     */
    fun init(context: Context) {
        // Immediately set seed data as initial value so UI doesn't show empty
        if (_allProductsFlow.value.isEmpty()) {
            _cachedProducts = ProductSeedData.defaultProducts
            _allProductsFlow.value = ProductSeedData.defaultProducts
        }
        
        if (databaseInitialized) return
        
        val database = DatabaseModule.getDatabase(context)
        databaseInitialized = true
        
        repositoryScope.launch {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val isSeeded = prefs.getBoolean(KEY_DATA_SEEDED, false)
                
                // Seed data jika belum pernah di-seed
                if (!isSeeded) {
                    seedProducts(context, database)
                    prefs.edit().putBoolean(KEY_DATA_SEEDED, true).apply()
                }
                
                // Load products from database
                loadProductsFromDatabase(database)
            } catch (e: Exception) {
                e.printStackTrace()
                // On error, use seed data as fallback
                _cachedProducts = ProductSeedData.defaultProducts
                _allProductsFlow.value = ProductSeedData.defaultProducts
            }
        }
    }
    
    /**
     * Seed products ke database
     */
    private suspend fun seedProducts(context: Context, database: SkinovateDatabase) {
        val seedProducts = ProductSeedData.defaultProducts
        val entities = seedProducts.map { ProductEntity.fromProduct(it) }
        database.productDao().insertProducts(entities)
    }
    
    /**
     * Load products from database
     */
    private suspend fun loadProductsFromDatabase(database: SkinovateDatabase) {
        try {
            val productsFlow = database.productDao().getAllProducts()
            val products = productsFlow.first().map { it.toProduct() }
            if (products.isEmpty()) {
                // If database is empty, use seed data
                _cachedProducts = ProductSeedData.defaultProducts
                _allProductsFlow.value = ProductSeedData.defaultProducts
            } else {
                _cachedProducts = products
                _allProductsFlow.value = products
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to seed data
            _cachedProducts = ProductSeedData.defaultProducts
            _allProductsFlow.value = ProductSeedData.defaultProducts
        }
    }
    
    /**
     * Get all products as Flow (reactive)
     */
    fun getAllProductsFlow(context: Context): Flow<List<Product>> {
        val database = DatabaseModule.getDatabase(context)
        return database.productDao().getAllProducts().map { entities ->
            entities.map { it.toProduct() }
        }
    }
    
    /**
     * Search products
     */
    fun searchProducts(query: String, context: Context): Flow<List<Product>> {
        val database = DatabaseModule.getDatabase(context)
        return database.productDao().searchProducts(query).map { entities ->
            entities.map { it.toProduct() }
        }
    }
    
    /**
     * Get products by category
     */
    fun getProductsByCategory(category: String, context: Context): Flow<List<Product>> {
        val database = DatabaseModule.getDatabase(context)
        return database.productDao().getProductsByCategory(category).map { entities ->
            entities.map { it.toProduct() }
        }
    }
    
    /**
     * Get product by ID
     */
    suspend fun getProductById(productId: String, context: Context): Product? {
        val database = DatabaseModule.getDatabase(context)
        return database.productDao().getProductById(productId)?.toProduct()
    }
}
