package com.example.skinovate.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.skinovate.data.Product
import com.example.skinovate.data.ProductRepository
import com.example.skinovate.data.SkinProblem
import com.example.skinovate.data.SkinProblemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemDetailScreen(
    navController: NavController,
    problemId: String
) {
    val problem = remember(problemId) {
        SkinProblemRepository.getProblemById(problemId)
    }
    
    if (problem == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detail Permasalahan") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                Text("Permasalahan tidak ditemukan")
            }
        }
        return
    }
    
    val context = LocalContext.current
    
    // Initialize ProductRepository
    LaunchedEffect(Unit) {
        ProductRepository.init(context)
    }
    
    // Get recommended products
    val allProducts by ProductRepository.allProductsFlow.collectAsState()
    val recommendedProducts = remember(allProducts, problem) {
        val productsFromIds = allProducts.filter { it.id in problem.recommendedProductIds }
        val productsFromCategories = allProducts.filter { 
            it.category in problem.recommendedCategories && it.id !in problem.recommendedProductIds 
        }
        (productsFromIds + productsFromCategories).distinctBy { it.id }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        problem.title,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with icon and description
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = problem.icon,
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = problem.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // Causes
            item {
                SectionCard(
                    title = "Penyebab",
                    items = problem.causes
                )
            }
            
            // Symptoms
            item {
                SectionCard(
                    title = "Gejala",
                    items = problem.symptoms
                )
            }
            
            // Prevention
            item {
                SectionCard(
                    title = "Cara Mencegah",
                    items = problem.prevention
                )
            }
            
            // Treatment
            item {
                SectionCard(
                    title = "Cara Mengatasi",
                    items = problem.treatment
                )
            }
            
            // Recommended Products
            if (recommendedProducts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rekomendasi Produk",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(recommendedProducts) { product ->
                    RecommendedProductCard(product = product, navController = navController)
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    items: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendedProductCard(
    product: Product,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = {
            navController.navigate("product_detail/${product.id}")
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.ui.platform.LocalContext.current
                // You can add Image here if you have image resources
                Text(
                    text = product.brand.take(2).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
            
            // Product Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = "Rp ${String.format("%,.0f", product.price)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

